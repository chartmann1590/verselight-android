# VerseLight

VerseLight is a native Android daily Bible verse app with a calm, biblical design, public community reflections, a private activity journey, on-device comment moderation, Firebase authentication/data, Cloudflare reporting, and a public Firebase Hosting website.

## What is included

- Kotlin, Jetpack Compose, Material 3, Android 8+ (`minSdk 26`)
- One deterministic UTC verse each day from the public-domain World English Bible
- Guest reading and Android sharing
- Firebase email/password and Google authentication
- Three-step first-run onboarding with account, Google, and explicit guest paths
- Persistent language selection with downloadable ML Kit on-device translation models
- Private likes, share history, preferences, and activity history in Firestore
- Public comments with editable public display name and optional Google avatar
- Embedded on-device safety classification plus Gemini Nano/AICore when supported by the device
- A second Worker-side safety gate; clients cannot write new comments directly to Firestore
- Firebase Crashlytics and Performance Monitoring for stability and performance diagnostics
- Cloudflare Worker, D1 moderation queue, distinct-reporter threshold hiding, and protected moderator routes
- Opt-in local daily notification
- Responsive landing, privacy, community-guidelines, support, and account-deletion pages
- Google Play account-deletion URL: https://verselight-daily-2026.web.app/delete-account

## Local Android setup

1. Use JDK 17 and Android SDK 36.
2. Register `com.chartmann1590.verselight` in Firebase and place `google-services.json` in `app/`.
3. Enable Email/Password and Google providers in Firebase Authentication. Add debug and release SHA-1/SHA-256 fingerprints.
4. Run `./gradlew :app:assembleDebug :app:testDebugUnitTest`.
5. Install with `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

The checked-in default Worker URL points to the VerseLight production Worker. Override it locally with `-PREPORTS_BASE_URL=https://...` if needed.

## Firebase

The live project is `verselight-daily-2026`. Deploy rules and indexes with:

```text
firebase deploy --only firestore --project verselight-daily-2026
```

`app/google-services.json`, signing keys, and service-account credentials are intentionally ignored.

## Cloudflare Worker

The Worker lives in `worker/` and uses a D1 database plus a rate-limit binding. It authenticates comment authors with Firebase ID tokens, repeats the deterministic safety check, and is the only production path that can create comments. Required secrets are `FIREBASE_CLIENT_EMAIL`, `FIREBASE_PRIVATE_KEY`, and an emergency `ADMIN_TOKEN`.

```text
cd worker
npm install
npm run typecheck
npm test
npm run deploy
```

The Firebase service account uses the standard Datastore User role. Moderator access additionally requires a verified Firebase ID token for `charles.h.hartmann1@gmail.com`; the emergency token is break-glass access and is not used by the browser dashboard.

## Website

Static public files are in `website/` and deploy to `https://verselight-daily-2026.web.app` with `firebase deploy --only hosting`. The Charles-only moderator dashboard is at `https://verselight-daily-2026.web.app/admin`.

## Privacy and content

The World English Bible text is public domain; the translation name is a trademark of eBible.org and the text is reproduced without modification. See [eBible.org](https://ebible.org/details.php?id=engwebp).

Comment drafts are moderated locally before any network request. Allowed drafts are checked again by the Worker before Firestore storage. Posted comments and public profile name/avatar are public. Account email, likes, shares, reporter identity, and activity history are private.

## License

App and infrastructure source are licensed under Apache-2.0. Scripture text remains public domain under its upstream terms.
