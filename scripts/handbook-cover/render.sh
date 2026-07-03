#!/usr/bin/env bash
#
# render.sh — 產生統一風格的 Handbook 書封 (Style A・深色編輯風・3:4 直式)
#
# 依 cover-gen.mjs 內的 BOOKS 註冊表，把每本 handbook 的封面渲染成
# 900x1200 PNG，並（可選）直接安裝到各 repo 的 site/content/cover.png。
#
# 需求：node、google-chrome、ImageMagick(convert)、Noto Sans CJK TC 字型
#
# 用法：
#   ./render.sh <key> [outfile.png]          # 產生單一封面（預設輸出到 ./<key>.png）
#   ./render.sh --install <handbooks-root>   # 產生全部並安裝到 <root>/<key>/site/content/cover.png
#
# 範例：
#   ./render.sh algorithms-data-structures
#   ./render.sh --install ~/workspace/andrew/handbooks
#
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT

# 從 cover-gen.mjs 的 BOOKS 註冊表抽出所有 key
keys() { grep -oE "^  '[a-z-]+':" "$DIR/cover-gen.mjs" | tr -d " ':"; }

render() {  # <key> <outPng>
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
