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
# 中間檔（trimmed/master 的大 WAV）留在來源旁邊當工作區、可重生、可刪；
# 真正要發佈的「交付物」由 publish 集中到 PUBLISH_DIR（預設 ROOT/_published），
# 鏡像來源的頻道結構、每集一夾，drop 掉 -master 後綴：
#   PUBLISH_DIR/<來源相對目錄>/<集名>/<集名>.m4a   上架音檔（AAC 壓縮自母帶）
#                                       /<集名>.srt   字幕（複製自 -master.srt）
#                                       /<集名>.txt   逐字稿（複製自 -master.txt）
#
# 用法：
#   podcast-pipeline.sh status [路徑]          # 看每個來源在哪一階段
#   podcast-pipeline.sh trim [路徑]            # 只跑去空白（剪輯前）
#   podcast-pipeline.sh master [路徑]          # 只跑母帶（剪輯後；有 -edited 用它，否則用 -trimmed）
#   podcast-pipeline.sh transcribe [路徑]      # 只跑逐字稿（對 -master）
#   podcast-pipeline.sh publish [路徑]         # 把母帶+字幕+逐字稿輸出到 _published（交付夾）
#   podcast-pipeline.sh auto [路徑]            # 一條龍：trim → master → transcribe → publish
#
#   [路徑] 可省略（=整個根目錄），或給某個子資料夾 / 單一檔案來限定範圍。
#
# 環境變數：
#   PODCAST_ROOT   媒體根目錄（預設 ~/workspace/podcasts）
#   PUBLISH_DIR    交付夾（預設 ROOT/_published）；掃描來源時會自動排除此夾
#   AUDIO_BR       上架音檔位元率（預設 192k）
#   MASTER_FROM    母帶來源：auto(預設,有edited用edited否則trimmed) | trimmed | edited
#   其餘 trim/master/transcribe 各自的環境變數（MARGIN/THRESH/TARGET_I/MODEL/LANG_CODE…）
#   會原樣傳遞給底層腳本。
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="${PODCAST_ROOT:-$HOME/workspace/podcasts}"
PUBLISH_DIR="${PUBLISH_DIR:-$ROOT/_published}"
AUDIO_BR="${AUDIO_BR:-192k}"
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
    ! -path "$PUBLISH_DIR/*" \
    ! -name '*-trimmed.*' ! -name '*-edited.*' ! -name '*-master.*' \
    -print0 | sort -z
}

# 各階段衍生檔路徑
base_of()    { local f="$1"; echo "${f%.*}"; }                 # 去副檔名
trimmed_of() { echo "$(base_of "$1")-trimmed.wav"; }
edited_of()  { echo "$(base_of "$1")-edited.wav"; }
master_of()  { echo "$(base_of "$1")-master.wav"; }
srt_of()     { echo "$(base_of "$1")-master.srt"; }
txt_of()     { echo "$(base_of "$1")-master.txt"; }

# 交付夾：鏡像來源的相對目錄、每集一夾（去掉 -master 後綴）
#   $ROOT/恩普拉氏/EP01.m4a → $PUBLISH_DIR/恩普拉氏/EP01
published_dir_of() {
  local f="$1" rd b
  rd="$(dirname "$(rel "$f")")"; b="$(basename "$(base_of "$f")")"
  if [ "$rd" = "." ]; then echo "$PUBLISH_DIR/$b"; else echo "$PUBLISH_DIR/$rd/$b"; fi
}
published_m4a_of() { local d; d="$(published_dir_of "$1")"; echo "$d/$(basename "$d").m4a"; }

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
  printf "%-7s %-7s %-7s %-7s %-7s  %s\n" "去空白" "精修" "母帶" "逐字稿" "發佈" "來源"
  printf "%-7s %-7s %-7s %-7s %-7s  %s\n" "------" "----" "----" "------" "----" "----"
  while IFS= read -r -d '' f; do
    n=$((n+1))
    local t e m s p
    [ -f "$(trimmed_of   "$f")" ] && t="✓"   || t="·"
    [ -f "$(edited_of    "$f")" ] && e="✓"   || e="·"
    [ -f "$(master_of    "$f")" ] && m="✓"   || m="·"
    [ -f "$(srt_of       "$f")" ] && s="✓"   || s="·"
    [ -f "$(published_m4a_of "$f")" ] && p="✓" || p="·"
    printf "  %-5s   %-5s   %-5s   %-5s   %-5s    %s\n" "$t" "$e" "$m" "$s" "$p" "$(rel "$f")"
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

do_publish() {
  local f m srt txt outdir b m4a done=0 skip=0 nosrc=0
  command -v ffmpeg >/dev/null || { echo "需要 ffmpeg：brew install ffmpeg"; exit 1; }
  while IFS= read -r -d '' f; do
    m="$(master_of "$f")"
    if [ ! -f "$m" ]; then nosrc=$((nosrc+1)); continue; fi   # 沒母帶不能發佈
    outdir="$(published_dir_of "$f")"; b="$(basename "$outdir")"; m4a="$outdir/$b.m4a"
    mkdir -p "$outdir"
    if [ -f "$m4a" ]; then
      skip=$((skip+1))
    else
      echo "── 發佈音檔：$(rel "$m") → ${m4a#$ROOT/}"
      ffmpeg -hide_banner -loglevel error -y -i "$m" -c:a aac -b:a "$AUDIO_BR" "$m4a"
      done=$((done+1))
    fi
    # 字幕/逐字稿每次刷新（便宜，且 transcribe 可能重產），去掉 -master 後綴
    srt="$(srt_of "$f")"; txt="$(txt_of "$f")"
    [ -f "$srt" ] && cp -f "$srt" "$outdir/$b.srt"
    [ -f "$txt" ] && cp -f "$txt" "$outdir/$b.txt"
  done < <(list_sources "$SCOPE")
  echo "發佈完成：新編碼 ${done}，略過(已存在) ${skip}，待處理(缺母帶) ${nosrc}"
  echo "交付夾：${PUBLISH_DIR}"
}

case "$CMD" in
  status)     do_status ;;
  trim)       do_trim ;;
  master)     do_master ;;
  transcribe) do_transcribe ;;
  publish)    do_publish ;;
  auto)       do_trim; echo; do_master; echo; do_transcribe; echo; do_publish ;;
  *) echo "未知指令：$CMD"; echo "用法：$0 {status|trim|master|transcribe|publish|auto} [路徑]"; exit 1 ;;
esac
