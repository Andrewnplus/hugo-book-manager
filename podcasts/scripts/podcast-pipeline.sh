#!/usr/bin/env bash
#
# podcast-pipeline.sh — 音檔處理 Manager（掃描媒體根目錄、原地產出、冪等）
#
# 心法：你只要把錄音丟進 PODCAST_ROOT 底下（可任意巢狀分頻道/系列/集），
# 這支 Manager 會掃描每個「來源音檔」，按需要跑：去空白 → 母帶 → 逐字稿。
# 產出檔放在來源檔「旁邊」（sibling），用檔名後綴標示階段，所以巢狀結構照舊。
#
# 三段式流程對應（見 ../planning/剪輯流程與工具指南.md）：
#   ① 去空白(自動)   ② 人耳精修(DaVinci,手動,選用)   ③ 母帶(自動)
#      *-trimmed.wav      你匯出 *-edited.wav             *-master.wav
#
# 狀態完全由「檔案是否存在」決定，沒有隱藏狀態檔，重跑安全：
#   來源:   foo.m4a            （不含 -trimmed/-edited/-master 後綴的音檔）
#   去空白: foo-trimmed.wav    （auto-editor 自動切死空氣）
#   精修:   foo-edited.wav     （選用；你在 Resolve 剪完手動匯出放這）
#   母帶:   foo-master.wav     （ffmpeg，-16 LUFS / -1 dBTP）
#   逐字稿: foo-master.srt/.txt（whisper.cpp，對母帶轉錄）
#
# 用法：
#   podcast-pipeline.sh status [路徑]          # 看每個來源在哪一階段
#   podcast-pipeline.sh trim [路徑]            # 只跑去空白（剪輯前）
#   podcast-pipeline.sh master [路徑]          # 只跑母帶（剪輯後；有 -edited 用它，否則用 -trimmed）
#   podcast-pipeline.sh transcribe [路徑]      # 只跑逐字稿（對 -master）
#   podcast-pipeline.sh auto [路徑]            # 一條龍：trim → master → transcribe（略過人耳精修）
#
#   [路徑] 可省略（=整個根目錄），或給某個子資料夾 / 單一檔案來限定範圍。
#
# 環境變數：
#   PODCAST_ROOT   媒體根目錄（預設 ~/workspace/podcasts）
#   MASTER_FROM    母帶來源：auto(預設,有edited用edited否則trimmed) | trimmed | edited
#   其餘 trim/master/transcribe 各自的環境變數（MARGIN/THRESH/TARGET_I/MODEL/LANG_CODE…）
#   會原樣傳遞給底層腳本。
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="${PODCAST_ROOT:-$HOME/workspace/podcasts}"
MASTER_FROM="${MASTER_FROM:-auto}"

TRIM="$SCRIPT_DIR/trim-silence.sh"
MASTER="$SCRIPT_DIR/podcast-master.sh"
TRANSCRIBE="$SCRIPT_DIR/transcribe.sh"

CMD="${1:-status}"
SCOPE="${2:-$ROOT}"
# 相對路徑的 scope 視為相對於 ROOT
[ -e "$SCOPE" ] || SCOPE="$ROOT/$SCOPE"

[ -d "$ROOT" ] || { echo "找不到媒體根目錄：${ROOT}（設 PODCAST_ROOT 或先建立）"; exit 1; }

# ---- 工具 ----
# 列出 scope 下所有「來源音檔」（排除衍生後綴），NUL 分隔以容忍空白/中文檔名
list_sources() {
  local target="$1"
  if [ -f "$target" ]; then
    printf '%s\0' "$target"
    return
  fi
  find "$target" -type f \
    \( -iname '*.wav' -o -iname '*.mp3' -o -iname '*.m4a' -o -iname '*.flac' -o -iname '*.aac' \) \
    -not -path '*/.*' \
    ! -name '*-trimmed.*' ! -name '*-edited.*' ! -name '*-master.*' \
    -print0 | sort -z
}

# 各階段衍生檔路徑
base_of()    { local f="$1"; echo "${f%.*}"; }                 # 去副檔名
trimmed_of() { echo "$(base_of "$1")-trimmed.wav"; }
edited_of()  { echo "$(base_of "$1")-edited.wav"; }
master_of()  { echo "$(base_of "$1")-master.wav"; }
srt_of()     { echo "$(base_of "$1")-master.srt"; }

# 母帶的輸入來源（依 MASTER_FROM）
master_src_of() {
  local f="$1" ed tr; ed="$(edited_of "$f")"; tr="$(trimmed_of "$f")"
  case "$MASTER_FROM" in
    edited)  [ -f "$ed" ] && echo "$ed" ;;
    trimmed) [ -f "$tr" ] && echo "$tr" ;;
    *)       if [ -f "$ed" ]; then echo "$ed"; elif [ -f "$tr" ]; then echo "$tr"; fi ;;
  esac
}

rel() { local p="$1"; echo "${p#$ROOT/}"; }

# ---- 各階段 ----
do_status() {
  local f n=0
  printf "%-7s %-7s %-7s %-7s  %s\n" "去空白" "精修" "母帶" "逐字稿" "來源"
  printf "%-7s %-7s %-7s %-7s  %s\n" "------" "----" "----" "------" "----"
  while IFS= read -r -d '' f; do
    n=$((n+1))
    local t e m s
    [ -f "$(trimmed_of "$f")" ] && t="✓"   || t="·"
    [ -f "$(edited_of  "$f")" ] && e="✓"   || e="·"
    [ -f "$(master_of  "$f")" ] && m="✓"   || m="·"
    [ -f "$(srt_of     "$f")" ] && s="✓"   || s="·"
    printf "  %-5s   %-5s   %-5s   %-5s    %s\n" "$t" "$e" "$m" "$s" "$(rel "$f")"
  done < <(list_sources "$SCOPE")
  echo
  echo "共 $n 個來源音檔（根目錄：${ROOT}）"
}

do_trim() {
  local f out done=0 skip=0
  while IFS= read -r -d '' f; do
    out="$(trimmed_of "$f")"
    if [ -f "$out" ]; then skip=$((skip+1)); continue; fi
    echo "── 去空白：$(rel "$f")"
    "$TRIM" "$f" "$out"
    done=$((done+1))
  done < <(list_sources "$SCOPE")
  echo "去空白完成：新處理 ${done}，略過(已存在) ${skip}"
}

do_master() {
  local f src out done=0 skip=0 nosrc=0
  while IFS= read -r -d '' f; do
    out="$(master_of "$f")"
    if [ -f "$out" ]; then skip=$((skip+1)); continue; fi
    src="$(master_src_of "$f")"
    if [ -z "$src" ]; then
      echo "⚠ 無可母帶的來源(缺 -trimmed/-edited)：$(rel "$f")"; nosrc=$((nosrc+1)); continue
    fi
    echo "── 母帶：$(rel "$src") → $(rel "$out")"
    "$MASTER" "$src" "$out"
    done=$((done+1))
  done < <(list_sources "$SCOPE")
  echo "母帶完成：新處理 ${done}，略過(已存在) ${skip}，待處理(缺來源) ${nosrc}"
}

do_transcribe() {
  local f m srt done=0 skip=0 nosrc=0
  while IFS= read -r -d '' f; do
    m="$(master_of "$f")"; srt="$(srt_of "$f")"
    if [ -f "$srt" ]; then skip=$((skip+1)); continue; fi
    if [ ! -f "$m" ]; then nosrc=$((nosrc+1)); continue; fi
    echo "── 逐字稿：$(rel "$m")"
    "$TRANSCRIBE" "$m"
    done=$((done+1))
  done < <(list_sources "$SCOPE")
  echo "逐字稿完成：新處理 ${done}，略過(已存在) ${skip}，待處理(缺母帶) ${nosrc}"
}

case "$CMD" in
  status)     do_status ;;
  trim)       do_trim ;;
  master)     do_master ;;
  transcribe) do_transcribe ;;
  auto)       do_trim; echo; do_master; echo; do_transcribe ;;
  *) echo "未知指令：$CMD"; echo "用法：$0 {status|trim|master|transcribe|auto} [路徑]"; exit 1 ;;
esac
