#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT

keys() { grep -oE "^  '[a-z-]+':" "$DIR/cover-gen.mjs" | tr -d " ':"; }

render() {
  local key="$1" out="$2"
  node "$DIR/cover-gen.mjs" "$key" "$TMP/$key.html"
  google-chrome --headless=new --disable-gpu --hide-scrollbars \
    --force-device-scale-factor=2 --window-size=900,1200 \
    --screenshot="$TMP/$key@2x.png" "file://$TMP/$key.html" >/dev/null 2>&1
  convert "$TMP/$key@2x.png" -resize 900x1200 -strip -quality 95 "$out"
  echo "rendered $key -> $out"
}

if [[ "${1:-}" == "--install" ]]; then
  root="${2:?usage: ./render.sh --install <handbooks-root>}"
  for k in $(keys); do
    dest="$root/$k/site/content/cover.png"
    [[ -d "$root/$k/site/content" ]] || { echo "skip $k (no $dest)"; continue; }
    render "$k" "$dest"
  done
else
  key="${1:?usage: ./render.sh <key> [outfile.png]}"
  out="${2:-$DIR/$key.png}"
  render "$key" "$out"
fi
