#!/usr/bin/env python3
"""
Publish LumaVerse to Play Internal track via Google Play Android Publisher API.
Requires: service account JSON path in env var PLAY_SERVICE_ACCOUNT_JSON or ./scripts/verselight-play-publisher.json
Package: com.chartmann1590.verselight
"""
import os, sys, glob, json, asyncio
from pathlib import Path

PKG = "com.chartmann1590.verselight"
ROOT = Path("H:/bible-verse-app")
AAB_CANDIDATES = list(ROOT.glob("app/build/outputs/bundle/release/*.aab")) + list(ROOT.glob("app/build/outputs/bundle/**/*.aab"))

def find_key():
    for p in [os.environ.get("PLAY_SERVICE_ACCOUNT_JSON"), "H:/bible-verse-app/scripts/verselight-play-publisher.json", "H:/bible-verse-app/verselight-play-publisher.json", str(ROOT / "app" / "play-service-account.json")]:
        if p and Path(p).exists():
            return p
    return None

def find_aab():
    if AAB_CANDIDATES:
        return max(AAB_CANDIDATES, key=lambda x: x.stat().st_mtime)
    return None

if __name__ == "__main__":
    key = find_key()
    aab = find_aab()
    print(f"[LumaVerse Publish] Package: {PKG}")
    print(f"Key: {key or 'NOT FOUND - drop JSON at scripts/verselight-play-publisher.json or set PLAY_SERVICE_ACCOUNT_JSON'}")
    print(f"AAB: {aab or 'NOT FOUND - run ./gradlew bundleRelease first'}")
    if not key:
        print("\n!! Missing service account JSON. You said you added SA as admin - now download its JSON key:")
        print("  GCP Console > IAM > Service Accounts > verselight-play-publisher > Keys > Add Key > JSON")
        print("  Save as H:/bible-verse-app/scripts/verselight-play-publisher.json")
        print("  Then re-run: python scripts/publish-play-internal.py")
        sys.exit(2)
    if not aab:
        print("\n!! No AAB found. Building...")
        os.system("cd H:/bible-verse-app && ./gradlew bundleRelease --stacktrace")
        aab = find_aab()
        if not aab:
            print("Build failed or AAB not found")
            sys.exit(3)

    # Now try publish via googleapiclient
    try:
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
        from googleapiclient.http import MediaFileUpload
    except ImportError:
        print("Installing google-api-python-client...")
        os.system("pip install google-api-python-client google-auth -q")
        from google.oauth2 import service_account
        from googleapiclient.discovery import build
        from googleapiclient.http import MediaFileUpload

    creds = service_account.Credentials.from_service_account_file(key, scopes=["https://www.googleapis.com/auth/androidpublisher"])
    service = build("androidpublisher", "v3", credentials=creds, cache_discovery=False)
    edits = service.edits()
    edit = edits.insert(packageName=PKG, body={}).execute()
    edit_id = edit["id"]
    print(f"Edit created: {edit_id}")

    # Upload AAB
    print(f"Uploading {aab} ...")
    media = MediaFileUpload(str(aab), mimetype="application/octet-stream", resumable=True)
    bundle = edits.bundles().upload(packageName=PKG, editId=edit_id, media_body=media).execute()
    print(f"Bundle uploaded: versionCode {bundle.get('versionCode')}")

    # Update listings for each locale from fastlane/metadata
    meta_root = ROOT / "fastlane" / "metadata" / "android"
    for locale_dir in meta_root.iterdir():
        if not locale_dir.is_dir(): continue
        locale = locale_dir.name
        title = (locale_dir / "title.txt").read_text(encoding="utf-8").strip() if (locale_dir/"title.txt").exists() else None
        short = (locale_dir / "short_description.txt").read_text(encoding="utf-8").strip() if (locale_dir/"short_description.txt").exists() else None
        full = (locale_dir / "full_description.txt").read_text(encoding="utf-8").strip() if (locale_dir/"full_description.txt").exists() else None
        video = (locale_dir / "video.txt").read_text(encoding="utf-8").strip() if (locale_dir/"video.txt").exists() else None
        if not (title and short and full): continue
        body = {"title": title[:50], "shortDescription": short[:80], "fullDescription": full[:4000]}
        if video and "PLACEHOLDER" not in video:
            body["video"] = video
        try:
            edits.listings().update(packageName=PKG, editId=edit_id, language=locale, body=body).execute()
            print(f"Listing {locale}: OK")
        except Exception as e:
            print(f"Listing {locale}: {e}")

    # Assign to internal track
    track_body = {"releases": [{"versionCodes": [str(bundle["versionCode"])], "status": "completed"}]}
    edits.tracks().update(packageName=PKG, editId=edit_id, track="internal", body=track_body).execute()
    print("Assigned to internal track")

    # Commit
    result = edits.commit(packageName=PKG, editId=edit_id).execute()
    print(f"Committed: {result['id']}")
    print("\nDone! Check Play Console > Internal testing")
