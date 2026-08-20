import { initializeApp } from "https://www.gstatic.com/firebasejs/12.18.0/firebase-app.js";
import { getAuth, GoogleAuthProvider, onAuthStateChanged, signInWithPopup, signOut } from "https://www.gstatic.com/firebasejs/12.18.0/firebase-auth.js";

const ADMIN_EMAIL = "charles.h.hartmann1@gmail.com";
const API = "https://verselight-reports.charles-h-hartmann1.workers.dev";
const firebaseConfig = {
  projectId: "verselight-daily-2026",
  appId: "1:617118179680:web:26c7ed8499598073e07e05",
  storageBucket: "verselight-daily-2026.firebasestorage.app",
  apiKey: "AIzaSyCHeR7Maq0tn1ij29CLMDtCkJipKVyAHuM",
  authDomain: "verselight-daily-2026.firebaseapp.com",
  messagingSenderId: "617118179680",
};

const auth = getAuth(initializeApp(firebaseConfig));
const provider = new GoogleAuthProvider();
provider.setCustomParameters({ login_hint: ADMIN_EMAIL, prompt: "select_account" });
const reports = document.querySelector("#reports");
const status = document.querySelector("#status");
const signIn = document.querySelector("#sign-in");
const signOutButton = document.querySelector("#sign-out");

const escapeHtml = value => String(value ?? "").replace(/[&<>'"]/g, char => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" })[char]);

async function authorizedFetch(path, options = {}) {
  const user = auth.currentUser;
  if (!user) throw new Error("Please sign in first.");
  const token = await user.getIdToken();
  const response = await fetch(`${API}${path}`, { ...options, headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}`, ...(options.headers || {}) } });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(payload.error || `Request failed (${response.status})`);
  return payload;
}

async function debugToken() {
  try {
    const user = auth.currentUser;
    if (!user) return;
    const token = await user.getIdToken();
    const r = await fetch(`${API}/admin/debug`, { headers: { Authorization: `Bearer ${token}` } });
    const j = await r.json();
    console.log("admin/debug", j);
    if (!r.ok) status.textContent = `Debug: ${j.error || JSON.stringify(j)}`;
  } catch (e) { console.log("debug failed", e); }
}

async function loadReports() {
  reports.innerHTML = '<div class="empty">Loading reports…</div>';
  try {
    const data = await authorizedFetch("/admin/reports");
    if (!data.reports.length) {
      reports.innerHTML = '<div class="empty"><h2>Queue clear</h2><p>There are no submitted reports.</p></div>';
      return;
    }
    reports.innerHTML = data.reports.map(report => {
      const comment = JSON.parse(report.comment_snapshot);
      const resolved = report.status === "reviewed";
      return `<article class="report">
        <header><div><span class="reason">${escapeHtml(report.reason)}</span><h2>${escapeHtml(comment.authorName)}</h2><div class="meta">${escapeHtml(new Date(report.created_at).toLocaleString())} · ${escapeHtml(report.status)}</div></div></header>
        <div class="comment">${escapeHtml(comment.body)}</div>
        ${resolved ? `<strong>Decision: ${escapeHtml(report.resolution)}</strong>` : `<div class="admin-actions"><button class="button" data-id="${escapeHtml(report.id)}" data-action="restore">Restore</button><button class="button secondary" style="color:#733c46!important;border-color:#733c4655" data-id="${escapeHtml(report.id)}" data-action="remove">Remove</button></div>`}
      </article>`;
    }).join("");
  } catch (error) {
    reports.innerHTML = `<div class="empty"><h2>Could not load reports</h2><p>${escapeHtml(error.message)}</p><p style="font-size:12px;opacity:.6">Check console for admin/debug output. Try signing out and picking ${ADMIN_EMAIL} exactly.</p></div>`;
  }
}

signIn.addEventListener("click", async () => {
  status.textContent = "Opening Google sign-in…";
  try { await signInWithPopup(auth, provider); } catch (error) { status.textContent = error.message; console.error(error); }
});
signOutButton.addEventListener("click", () => signOut(auth));
reports.addEventListener("click", async event => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;
  const action = button.dataset.action;
  if (!confirm(`${action === "remove" ? "Remove" : "Restore"} this comment?`)) return;
  button.disabled = true;
  try {
    await authorizedFetch(`/admin/reports/${encodeURIComponent(button.dataset.id)}`, { method: "POST", body: JSON.stringify({ action }) });
    await loadReports();
  } catch (error) { status.textContent = error.message; button.disabled = false; }
});

onAuthStateChanged(auth, async user => {
  const email = user?.email?.toLowerCase();
  if (user && email !== ADMIN_EMAIL.toLowerCase()) {
    status.textContent = `Signed in as ${user.email} — not admin. Will still try server check (see console).`;
  }
  signIn.hidden = Boolean(user);
  signOutButton.hidden = !user;
  status.textContent = user ? `Administrator: ${user.email}` : `Sign in as ${ADMIN_EMAIL}`;
  if (user) {
    await debugToken();
    await loadReports();
  } else reports.innerHTML = '<div class="empty">The moderation queue will appear after administrator sign-in.</div>';
});
