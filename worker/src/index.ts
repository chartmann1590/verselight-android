import { createRemoteJWKSet, importPKCS8, jwtVerify, SignJWT, type JWTPayload } from "jose";

interface Env {
  DB: D1Database;
  REPORT_RATE_LIMITER: RateLimit;
  FIREBASE_PROJECT_ID: string;
  FIREBASE_CLIENT_EMAIL: string;
  FIREBASE_PRIVATE_KEY: string;
  ALLOWED_ORIGIN: string;
  CF_ACCESS_TEAM_DOMAIN?: string;
  CF_ACCESS_AUD?: string;
  ADMIN_TOKEN?: string;
}

type ReportBody = { dayKey: string; commentId: string; reason: string; optionalDetails?: string };
type FirestoreComment = { authorUid: string; authorName: string; body: string; moderationStatus: string };

const json = (data: unknown, status = 200, headers: HeadersInit = {}) => Response.json(data, { status, headers });
const cleanPrivateKey = (value: string) => value.replace(/\\n/g, "\n");

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const cors = { "Access-Control-Allow-Origin": env.ALLOWED_ORIGIN, "Vary": "Origin" };
    if (request.method === "OPTIONS") return new Response(null, { status: 204, headers: { ...cors, "Access-Control-Allow-Headers": "Authorization, Content-Type", "Access-Control-Allow-Methods": "GET, POST, OPTIONS" } });
    try {
      if (url.pathname === "/health") return json({ status: "ok", service: "verselight-reports" }, 200, cors);
      if (url.pathname === "/v1/reports" && request.method === "POST") return await createReport(request, env, cors);
      if (url.pathname === "/v1/account/comments" && request.method === "DELETE") return await deleteAccountComments(request, env, cors);
      if (url.pathname === "/admin" && request.method === "GET") return await adminPage(request, env);
      if (url.pathname === "/admin/reports" && request.method === "GET") return await listReports(request, env);
      if (url.pathname.startsWith("/admin/reports/") && request.method === "POST") return await reviewReport(request, env, url.pathname.split("/")[3]);
      return json({ error: "Not found" }, 404, cors);
    } catch (error) {
      console.error(JSON.stringify({ event: "request_failed", path: url.pathname, error: error instanceof Error ? error.message : String(error) }));
      return json({ error: error instanceof PublicError ? error.message : "Request failed" }, error instanceof PublicError ? error.status : 500, cors);
    }
  },
};

async function createReport(request: Request, env: Env, cors: HeadersInit): Promise<Response> {
  const user = await verifyFirebaseUser(request, env);
  const rate = await env.REPORT_RATE_LIMITER.limit({ key: user.sub! });
  if (!rate.success) throw new PublicError(429, "Too many reports. Please try again later.");
  const body = await request.json<ReportBody>();
  if (!/^\d{4}-\d{2}-\d{2}$/.test(body.dayKey || "") || !/^[A-Za-z0-9]{8,40}$/.test(body.commentId || "")) throw new PublicError(400, "Invalid comment reference.");
  if (!["abuse", "hate", "threat", "sexual", "spam", "other"].includes(body.reason)) throw new PublicError(400, "Invalid report reason.");
  const comment = await readComment(env, body.dayKey, body.commentId);
  if (comment.authorUid === user.sub) throw new PublicError(400, "You cannot report your own comment.");
  const id = crypto.randomUUID();
  try {
    await env.DB.prepare("INSERT INTO reports (id, day_key, comment_id, reporter_uid, reason, details, comment_snapshot, created_at) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)")
      .bind(id, body.dayKey, body.commentId, user.sub, body.reason, (body.optionalDetails || "").slice(0, 500), JSON.stringify(comment), new Date().toISOString()).run();
  } catch (error) {
    if (String(error).includes("UNIQUE")) throw new PublicError(409, "You already reported this comment.");
    throw error;
  }
  const countRow = await env.DB.prepare("SELECT COUNT(DISTINCT reporter_uid) AS count FROM reports WHERE comment_id = ?1").bind(body.commentId).first<{ count: number }>();
  const thresholdReached = Number(countRow?.count || 0) >= 3;
  if (thresholdReached && comment.moderationStatus === "visible") await updateCommentStatus(env, body.dayKey, body.commentId, "hidden_pending_review");
  return json({ reportId: id, status: thresholdReached ? "hidden_pending_review" : "queued", thresholdReached }, 201, cors);
}

async function verifyFirebaseUser(request: Request, env: Env): Promise<JWTPayload> {
  const token = request.headers.get("Authorization")?.match(/^Bearer (.+)$/)?.[1];
  if (!token) throw new PublicError(401, "Sign in is required.");
  const jwks = createRemoteJWKSet(new URL("https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"));
  const { payload } = await jwtVerify(token, jwks, { issuer: `https://securetoken.google.com/${env.FIREBASE_PROJECT_ID}`, audience: env.FIREBASE_PROJECT_ID });
  if (!payload.sub) throw new PublicError(401, "Invalid account token.");
  return payload;
}

async function googleAccessToken(env: Env): Promise<string> {
  const key = await importPKCS8(cleanPrivateKey(env.FIREBASE_PRIVATE_KEY), "RS256");
  const assertion = await new SignJWT({ scope: "https://www.googleapis.com/auth/datastore" })
    .setProtectedHeader({ alg: "RS256", typ: "JWT" }).setIssuer(env.FIREBASE_CLIENT_EMAIL).setAudience("https://oauth2.googleapis.com/token").setIssuedAt().setExpirationTime("10m").sign(key);
  const response = await fetch("https://oauth2.googleapis.com/token", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: new URLSearchParams({ grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer", assertion }) });
  if (!response.ok) throw new Error(`Google token exchange failed: ${response.status}`);
  return (await response.json<{ access_token: string }>()).access_token;
}

async function readComment(env: Env, dayKey: string, commentId: string): Promise<FirestoreComment> {
  const token = await googleAccessToken(env);
  const url = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/dailyVerses/${dayKey}/comments/${commentId}`;
  const response = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
  if (response.status === 404) throw new PublicError(404, "That comment no longer exists.");
  if (!response.ok) throw new Error(`Firestore read failed: ${response.status}`);
  const document = await response.json<{ fields: Record<string, { stringValue?: string }> }>();
  const value = (name: string) => document.fields[name]?.stringValue || "";
  return { authorUid: value("authorUid"), authorName: value("authorName"), body: value("body"), moderationStatus: value("moderationStatus") };
}

async function updateCommentStatus(env: Env, dayKey: string, commentId: string, status: string): Promise<void> {
  const token = await googleAccessToken(env);
  const url = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/dailyVerses/${dayKey}/comments/${commentId}?updateMask.fieldPaths=moderationStatus`;
  const response = await fetch(url, { method: "PATCH", headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }, body: JSON.stringify({ fields: { moderationStatus: { stringValue: status } } }) });
  if (!response.ok) throw new Error(`Firestore moderation update failed: ${response.status}`);
}

async function deleteAccountComments(request: Request, env: Env, cors: HeadersInit): Promise<Response> {
  const user = await verifyFirebaseUser(request, env);
  const token = await googleAccessToken(env);
  const base = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)`;
  const queryResponse = await fetch(`${base}/documents:runQuery`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" },
    body: JSON.stringify({ structuredQuery: { from: [{ collectionId: "comments", allDescendants: true }], where: { fieldFilter: { field: { fieldPath: "authorUid" }, op: "EQUAL", value: { stringValue: user.sub } } }, limit: 500 } }),
  });
  if (!queryResponse.ok) throw new Error(`Firestore cleanup query failed: ${queryResponse.status}`);
  const rows = await queryResponse.json<Array<{ document?: { name: string } }>>();
  const names = rows.flatMap(row => row.document?.name ? [row.document.name] : []);
  if (names.length) {
    const deleteResponse = await fetch(`${base}/documents:batchWrite`, { method: "POST", headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }, body: JSON.stringify({ writes: names.map(name => ({ delete: name })) }) });
    if (!deleteResponse.ok) throw new Error(`Firestore cleanup delete failed: ${deleteResponse.status}`);
  }
  return json({ deletedComments: names.length }, 200, cors);
}

async function verifyAdmin(request: Request, env: Env): Promise<string> {
  const bearer = request.headers.get("Authorization")?.replace(/^Bearer /, "");
  if (env.ADMIN_TOKEN && bearer === env.ADMIN_TOKEN) return "admin-token";
  const token = request.headers.get("Cf-Access-Jwt-Assertion");
  if (!token || !env.CF_ACCESS_TEAM_DOMAIN || !env.CF_ACCESS_AUD) throw new PublicError(401, "Cloudflare Access authentication required.");
  const issuer = `https://${env.CF_ACCESS_TEAM_DOMAIN}`;
  const { payload } = await jwtVerify(token, createRemoteJWKSet(new URL(`${issuer}/cdn-cgi/access/certs`)), { issuer, audience: env.CF_ACCESS_AUD });
  return String(payload.email || payload.sub || "access-admin");
}

async function listReports(request: Request, env: Env): Promise<Response> {
  await verifyAdmin(request, env);
  const rows = await env.DB.prepare("SELECT id, day_key, comment_id, reason, comment_snapshot, status, created_at, reviewed_at, resolution FROM reports ORDER BY created_at DESC LIMIT 100").all();
  return json({ reports: rows.results });
}

async function reviewReport(request: Request, env: Env, reportId: string): Promise<Response> {
  const admin = await verifyAdmin(request, env);
  const body = await request.json<{ action: "restore" | "remove" }>();
  if (!["restore", "remove"].includes(body.action)) throw new PublicError(400, "Invalid action.");
  const report = await env.DB.prepare("SELECT day_key, comment_id FROM reports WHERE id = ?1").bind(reportId).first<{ day_key: string; comment_id: string }>();
  if (!report) throw new PublicError(404, "Report not found.");
  const now = new Date().toISOString();
  await updateCommentStatus(env, report.day_key, report.comment_id, body.action === "restore" ? "restored" : "removed");
  await env.DB.batch([
    env.DB.prepare("UPDATE reports SET status = 'reviewed', reviewed_at = ?1, reviewer_uid = ?2, resolution = ?3 WHERE comment_id = ?4").bind(now, admin, body.action, report.comment_id),
    env.DB.prepare("INSERT INTO moderation_audit (id, report_id, comment_id, action, moderator_uid, created_at) VALUES (?1, ?2, ?3, ?4, ?5, ?6)").bind(crypto.randomUUID(), reportId, report.comment_id, body.action, admin, now),
  ]);
  return json({ success: true, status: body.action === "restore" ? "restored" : "removed" });
}

async function adminPage(request: Request, env: Env): Promise<Response> {
  await verifyAdmin(request, env);
  return new Response(ADMIN_HTML, { headers: { "Content-Type": "text/html; charset=utf-8", "Cache-Control": "no-store" } });
}

class PublicError extends Error { constructor(public status: number, message: string) { super(message); } }

const ADMIN_HTML = `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width"><title>VerseLight moderation</title><style>body{font:16px system-ui;background:#f8f1e4;color:#173a38;max-width:960px;margin:auto;padding:2rem}article{background:white;padding:1rem;margin:1rem 0;border-radius:16px;box-shadow:0 4px 18px #0001}button{padding:.65rem 1rem;margin-right:.5rem;border:0;border-radius:99px;background:#173a38;color:white}</style></head><body><h1>VerseLight moderation</h1><main id="reports">Loading…</main><script>async function load(){const r=await fetch('/admin/reports');const d=await r.json();reports.innerHTML=d.reports.map(x=>{const c=JSON.parse(x.comment_snapshot);return '<article><small>'+x.created_at+' · '+x.reason+'</small><h3>'+c.authorName+'</h3><p>'+c.body.replace(/[<>&]/g,s=>({'<':'&lt;','>':'&gt;','&':'&amp;'}[s]))+'</p><button onclick="act(\\''+x.id+'\\',\\'restore\\')">Restore</button><button onclick="act(\\''+x.id+'\\',\\'remove\\')">Remove</button></article>'}).join('')||'No reports.'}async function act(id,action){await fetch('/admin/reports/'+id,{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify({action})});load()}load()</script></body></html>`;
