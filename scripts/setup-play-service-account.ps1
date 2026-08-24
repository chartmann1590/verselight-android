# Run after installing gcloud CLI and logging in: gcloud auth login
# Project: verselight-daily-2026  Package: com.chartmann1590.verselight

$PROJECT_ID = "verselight-daily-2026"
$SA_NAME = "verselight-play-publisher"
$SA_EMAIL = "$SA_NAME@$PROJECT_ID.iam.gserviceaccount.com"
$KEY_FILE = "$PSScriptRoot\verselight-play-publisher.json"

Write-Host "1. Enable Google Play Android Developer API..."
gcloud services enable androidpublisher.googleapis.com --project $PROJECT_ID

Write-Host "2. Create service account..."
gcloud iam service-accounts create $SA_NAME --display-name="VerseLight Play Publisher" --project $PROJECT_ID

Write-Host "3. Create JSON key (keep private, add to .gitignore)..."
gcloud iam service-accounts keys create $KEY_FILE --iam-account $SA_EMAIL --project $PROJECT_ID
Write-Host "Key saved to $KEY_FILE"

Write-Host @"

4. Grant access in PLAY CONSOLE (manual, required):
   https://play.google.com/console -> Users and permissions -> Invite new users
   Email: $SA_EMAIL
   Permissions: Admin (or granular):
     - View app information
     - Manage store presence (listing, graphics)
     - Manage production / testing tracks
     - Manage orders & subscriptions (if needed)
   Scope: Apply to 'VerseLight' (com.chartmann1590.verselight) -> Send invite

5. First AAB must be uploaded manually before API can publish updates.

6. Test API access:
   gcloud auth activate-service-account $SA_EMAIL --key-file=$KEY_FILE
   # Or use with fastlane supply / gradle-play-publisher

"@

Write-Host "Done. Add $KEY_FILE to .gitignore and store in GitHub Secrets as PLAY_SERVICE_ACCOUNT_JSON if using CI."
