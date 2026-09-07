#!/usr/bin/env python3
"""
Publish LumaVerse to Play PRODUCTION track as a DRAFT release (not submitted for review)
via Google Play Android Publisher API.

Uploads: release AAB, text listings (title/short/full/video) for every locale,
and graphics (icon, feature graphic, phone screenshots) for every locale that has them.

Requires: service account JSON path in env var PLAY_SERVICE_ACCOUNT_JSON or
./scripts/verselight-play-publisher.json
Package: com.chartmann1590.verselight
"""
import os, sys
from pathlib import Path

PKG = "com.chartmann1590.verselight"
ROOT = Path("H:/bible-verse-app")
AAB_CANDIDATES = list(ROOT.glob("app/build/outputs/bundle/release/*.aab")) + list(ROOT.glob("app/build/outputs/bundle/**/*.aab"))

IMAGE_TYPES = {
    "icon.png": "icon",
    "featureGraphic.png": "featureGraphic",
}

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
    print(f"Key: {key or 'NOT FOUND'}")
    print(f"AAB: {aab or 'NOT FOUND - run ./gradlew bundleRelease first'}")
    if not key:
        print("\n!! Missing service account JSON.")
        sys.exit(2)
    if not aab:
        print("\n!! No AAB found. Run ./gradlew bundleRelease first.")
        sys.exit(3)

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
    version_code = bundle.get("versionCode")
    print(f"Bundle uploaded: versionCode {version_code}")

    # Update text listings + graphics for each locale from fastlane/metadata
    meta_root = ROOT / "fastlane" / "metadata" / "android"
    for locale_dir in sorted(meta_root.iterdir()):
        if not locale_dir.is_dir():
            continue
        locale = locale_dir.name
        title = (locale_dir / "title.txt").read_text(encoding="utf-8").strip() if (locale_dir / "title.txt").exists() else None
        short = (locale_dir / "short_description.txt").read_text(encoding="utf-8").strip() if (locale_dir / "short_description.txt").exists() else None
        full = (locale_dir / "full_description.txt").read_text(encoding="utf-8").strip() if (locale_dir / "full_description.txt").exists() else None
        video = (locale_dir / "video.txt").read_text(encoding="utf-8").strip() if (locale_dir / "video.txt").exists() else None
        if title and short and full:
            body = {"title": title[:50], "shortDescription": short[:80], "fullDescription": full[:4000]}
            if video and "PLACEHOLDER" not in video:
                body["video"] = video
            try:
                edits.listings().update(packageName=PKG, editId=edit_id, language=locale, body=body).execute()
                print(f"Listing {locale}: OK")
            except Exception as e:
                print(f"Listing {locale}: FAILED {e}")

        images_dir = locale_dir / "images"
        if not images_dir.exists():
            continue

        for filename, image_type in IMAGE_TYPES.items():
            fpath = images_dir / filename
            if not fpath.exists():
                continue
            try:
                edits.images().deleteall(packageName=PKG, editId=edit_id, language=locale, imageType=image_type).execute()
            except Exception:
                pass
            try:
                img_media = MediaFileUpload(str(fpath), mimetype="image/png")
                edits.images().upload(packageName=PKG, editId=edit_id, language=locale, imageType=image_type, media_body=img_media).execute()
                print(f"Image {locale}/{image_type}: OK")
            except Exception as e:
                print(f"Image {locale}/{image_type}: FAILED {e}")

        screenshots = sorted(images_dir.glob("phoneScreenshots_*.png"))
        if screenshots:
            try:
                edits.images().deleteall(packageName=PKG, editId=edit_id, language=locale, imageType="phoneScreenshots").execute()
            except Exception:
                pass
            for shot in screenshots:
                try:
                    img_media = MediaFileUpload(str(shot), mimetype="image/png")
                    edits.images().upload(packageName=PKG, editId=edit_id, language=locale, imageType="phoneScreenshots", media_body=img_media).execute()
                except Exception as e:
                    print(f"Screenshot {locale}/{shot.name}: FAILED {e}")
            print(f"Screenshots {locale}: {len(screenshots)} uploaded")

    # Assign to PRODUCTION track as a DRAFT (not submitted for review, not rolled out)
    track_body = {"releases": [{"versionCodes": [str(version_code)], "status": "draft"}]}
    edits.tracks().update(packageName=PKG, editId=edit_id, track="production", body=track_body).execute()
    print("Assigned to production track as DRAFT")

    # Commit
    result = edits.commit(packageName=PKG, editId=edit_id).execute()
    print(f"Committed: {result['id']}")
    print("\nDone! Check Play Console > Production > this release will show as Draft (not live, not under review).")
    print("You still need to manually review + Save/Submit for review in Play Console when ready.")
