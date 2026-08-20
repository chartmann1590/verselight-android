# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| main (latest) | ✅ |
| older releases | ❌ Please update to latest `main` |

## Reporting a Vulnerability

**Please do not open a public issue for security vulnerabilities.**

Use one of these private channels:

1. **GitHub Private Vulnerability Reporting (preferred):**
   Go to **Security → Report a vulnerability** at https://github.com/chartmann1590/verselight-android/security/advisories/new
2. **Email:** If you cannot use private reporting, use the support channel at https://github.com/chartmann1590/verselight-android/issues/new and mark it as security-sensitive — we will triage privately. Do not include exploit payloads in public issues; request a private contact.

We aim to acknowledge within **48 hours** and publish a fix/advisory within **90 days** where possible.

## What to include

- Affected version/commit, steps to reproduce, impact, and suggested mitigation if known.
- Do not include `google-services.json`, private keys, or user PII in the report.

## Security Features Enabled

- **Secret scanning + push protection** (blocks pushes containing secrets)
- **Dependabot security updates & version updates** (weekly: Gradle, npm/worker, GitHub Actions)
- **CodeQL analysis** (`java-kotlin` + `javascript`) on push/PR/weekly via `.github/workflows/codeql.yml`
- **Dependency Review** on PRs via `.github/workflows/dependency-review.yml`
- Firestore rules least-privilege, Cloudflare Worker verified Firebase ID tokens, rate-limited reports.

## Disclosure

We follow coordinated disclosure. Once a fix is released, we will publish a GitHub Security Advisory and credit the reporter (unless they prefer anonymity).

Thank you for helping keep VerseLight safe.
