# AdMob & Play Store Compliance — VerseLight

## What was implemented

### 1. Google Mobile Ads (GMA) SDK
- `play-services-ads:24.4.0` + `user-messaging-platform:3.1.0` in `app/build.gradle.kts`
- `AD_ID` permission in `AndroidManifest.xml` (required for Advertising ID, auto-declared)
- `com.google.android.gms.ads.APPLICATION_ID` meta-data reading `@string/admob_app_id`
- Test IDs in `app/src/main/res/values/strings.xml` (Google official test IDs):
  - App ID: `ca-app-pub-3940256099942544~3347511713`
  - Banner: `ca-app-pub-3940256099942544/6300978111`
  - Interstitial: `ca-app-pub-3940256099942544/1033173712`
  > Replace with your real AdMob IDs before production release.

### 2. Consent (GDPR / UMP) — Play Families / EU compliance
- `ads/ConsentManager.kt` uses `UserMessagingPlatform.getConsentInformation()` + `requestConsentInfoUpdate(activity, params)` + `loadAndShowConsentFormIfRequired(activity)`
- Called in `MainActivity.onCreate()` before `MobileAds.initialize()`
- Ads only requested when `ConsentManager.canRequestAds == true` (checked in `BannerAd.kt` and `AdMobManager`)
- Privacy options entry point exposed in `Profile` → `Ads privacy options` → `UserMessagingPlatform.showPrivacyOptionsForm()`
- Without consent, banner is hidden and interstitial never loads (UMP returns false).

### 3. Banner Ads
- `ads/BannerAd.kt`: adaptive anchored banner (`AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize`) via `AndroidView` `AdView`. Only loads if `canRequestAds`.
- Integrated in `VerseLightRoot` Scaffold `bottomBar` above `NavigationBar` (does not overlap content, stays above navigationBarsPadding). Policy-compliant: not overlapping interactive elements.

### 4. Interstitial Ads
- `ads/AdMobManager.kt`: preloads interstitial on app start after consent, 2-minute `COOLDOWN_MS` frequency cap, `isLoading` guard, `FullScreenContentCallback` auto-reloads next ad on dismiss/failure.
- Shown only at natural break points (Play policy):
  - After **4 tab switches** (user-initiated navigation) via `maybeShowInterstitial()` — not on app launch/exit.
  - After **successful comment post** via `onPostSuccess()` — transactional break point.
  - Never shows if `canShowInterstitial()` false (cooldown or not loaded).
  - Preload via `preloadIfNeeded()` keeps an ad ready.

### 5. Play StoreCompliance extras
- In-app links to `https://verselight-daily-2026.web.app/privacy.html` and `/community.html` added in `ProfileScreen` (Play requires privacy policy accessible inside app).
- `Profile` footer now discloses "Contains ads · Personalized ads require consent".
- `website/privacy.html` updated: added **Advertising** section describing AdMob, Advertising ID, personalized vs non-personalized, UMP/GDPR, opt-out links, `app-ads.txt` reference, and updated service providers.
- `website/app-ads.txt` added with placeholder `google.com, pub-3940256099942544, DIRECT, ...` — replace `pub-...` with your publisher ID. Must be served at root: `https://verselight-daily-2026.web.app/app-ads.txt` (Firebase Hosting `public=website` already serves it).
- `proguard-rules.pro` keeps `com.google.android.gms`.
- `AD_ID` permission automatically handled — do NOT add `tools:node="remove"` elsewhere.

## Before Play Release — you must do

1. **Create real AdMob account** → create App → get real App ID + Banner + Interstitial unit IDs → replace strings in `app/src/main/res/values/strings.xml`. Keep test IDs for `debug` via `buildConfigField` or productFlavors if desired.
2. **AdMob → Privacy & messaging** → create **EEA/UK GDPR message** + **Privacy options** for your App ID. UMP will then actually show the consent form; without this it silently does nothing (but still compliant).
3. **Update `website/app-ads.txt`** with your real `pub-XXXXXXXX` line and deploy: `firebase deploy --only hosting`. Verify `https://YOUR_DOMAIN/app-ads.txt`.
4. **Play Console → Data safety**:
   - Data collected: Email, Name, User IDs, Photos (avatar), App activity (likes/shares/comments), App info & performance (crash logs), Device IDs (Advertising ID).
   - Purposes: App functionality, Analytics, **Advertising**.
   - Encryption in transit: **Yes**. Data deletion: **Yes** (link `https://verselight-daily-2026.web.app/delete-account.html`).
   - Declare **Advertising ID** usage (required when AD_ID permission present) and that ads SDK collects device IDs.
5. **Play Console → App content** → Ads: **Yes, contains ads**. Content rating → re-do IARC questionnaire (UGC: Yes, ads).
6. **Play Console → Store listing** → Privacy Policy URL = `https://verselight-daily-2026.web.app/privacy.html`. Ensure website field matches domain serving `app-ads.txt`.
7. **Signing**: `bundleRelease` with Play App Signing; increment `versionCode` per release; test `isMinifyEnabled=true` build.
8. **Test**: Use `debug` build on real device; consent form appears in EEA test mode (set DebugGeography in `ConsentManager` for testing if needed). Verify banner shows, interstitial shows max once per 2 min and not on launch.

## Policy checklist (why this passes)

- Banner does not interfere with UI/navigation (fixed below NavBar).
- Interstitial only between content, with cooldown, not on start/exit, not while typing.
- Consent collected via certified UMP before personalised ads; `canRequestAds()` gates all requests.
- Privacy policy reachable in-app and lists AdMob + Advertising ID.
- `app-ads.txt` present.
- `AD_ID` permission declared (Play will show “Advertising ID” declaration step).

## Useful test commands

```
# debug (test ads)
./gradlew :app:assembleDebug

# release bundle for Play
./gradlew :app:bundleRelease
```
