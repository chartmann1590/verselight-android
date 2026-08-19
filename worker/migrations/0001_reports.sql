CREATE TABLE IF NOT EXISTS reports (
  id TEXT PRIMARY KEY,
  day_key TEXT NOT NULL,
  comment_id TEXT NOT NULL,
  reporter_uid TEXT NOT NULL,
  reason TEXT NOT NULL,
  details TEXT NOT NULL DEFAULT '',
  comment_snapshot TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'queued',
  created_at TEXT NOT NULL,
  reviewed_at TEXT,
  reviewer_uid TEXT,
  resolution TEXT,
  UNIQUE(comment_id, reporter_uid)
);
CREATE INDEX IF NOT EXISTS reports_status_created_idx ON reports(status, created_at DESC);
CREATE INDEX IF NOT EXISTS reports_comment_idx ON reports(comment_id);

CREATE TABLE IF NOT EXISTS moderation_audit (
  id TEXT PRIMARY KEY,
  report_id TEXT NOT NULL,
  comment_id TEXT NOT NULL,
  action TEXT NOT NULL,
  moderator_uid TEXT NOT NULL,
  created_at TEXT NOT NULL
);

