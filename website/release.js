// LumaVerse — latest GitHub Release panel (external file so the
// Content-Security-Policy `script-src 'self' ...` stays intact).
(function () {
  var api = "https://api.github.com/repos/chartmann1590/verselight-android/releases/latest";
  var versionEl = document.getElementById("gh-release-version");
  var dateEl = document.getElementById("gh-release-date");
  var dlEl = document.getElementById("gh-release-download");
  var notesEl = document.getElementById("gh-release-notes");
  if (!versionEl || !dlEl) return;
  fetch(api, { headers: { Accept: "application/vnd.github+json" } })
    .then(function (r) {
      if (!r.ok) throw new Error("HTTP " + r.status);
      return r.json();
    })
    .then(function (rel) {
      var tag = rel.tag_name || rel.name || "latest";
      versionEl.textContent = rel.name || tag;
      if (rel.published_at) {
        try {
          dateEl.textContent =
            "Published " +
            new Date(rel.published_at).toLocaleDateString(undefined, {
              year: "numeric",
              month: "long",
              day: "numeric",
            }) +
            " · " +
            tag;
        } catch (e) {
          dateEl.textContent = tag;
        }
      } else {
        dateEl.textContent = tag;
      }
      var assets = Array.isArray(rel.assets) ? rel.assets : [];
      var apk =
        assets.find(function (a) {
          return /\.apk$/i.test(a.name || "");
        }) || assets[0];
      if (apk && apk.browser_download_url) {
        dlEl.href = apk.browser_download_url;
        var size = apk.size ? " (" + (apk.size / 1048576).toFixed(1) + " MB)" : "";
        dlEl.textContent = "Download " + (apk.name || "APK") + size;
      } else if (rel.html_url) {
        dlEl.href = rel.html_url;
      }
      if (rel.html_url && notesEl) notesEl.href = rel.html_url;
    })
    .catch(function () {
      versionEl.textContent = "Latest release on GitHub";
      dateEl.textContent =
        "Couldn't reach the GitHub API — open the releases page to download the newest APK.";
    });
})();
