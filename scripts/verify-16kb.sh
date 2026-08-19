#!/usr/bin/env bash
set -euo pipefail

apk="${1:?usage: verify-16kb.sh path/to/app.apk}"
build_tools="$(find "${ANDROID_HOME:?ANDROID_HOME is required}/build-tools" -mindepth 1 -maxdepth 1 -type d | sort -V | tail -n 1)"

"$build_tools/zipalign" -c -P 16 -v 4 "$apk" >/dev/null

check_dir="$(mktemp -d)"
trap 'rm -rf "$check_dir"' EXIT
unzip -qq "$apk" 'lib/*/*.so' -d "$check_dir"

while IFS= read -r -d '' library; do
  while read -r alignment; do
    if (( alignment < 0x4000 )); then
      echo "16 KB ELF alignment failed: $library has LOAD alignment $(printf '0x%x' "$alignment")" >&2
      exit 1
    fi
  done < <(readelf -lW "$library" | awk '$1 == "LOAD" { print $NF }')
done < <(find "$check_dir/lib" -type f -name '*.so' -print0)

echo "16 KB APK and ELF alignment verified: $apk"
