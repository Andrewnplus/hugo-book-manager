#!/usr/bin/env bash
#
# trim-silence.sh — 自動切掉死空氣（粗剪，剪輯第一步）
#
# 依賴 auto-editor + ffmpeg。音／影皆可。切點左右保留 padding，避免聽起來上氣不接下氣。
#
# 用法：
#   ./trim-silence.sh input.wav [output.wav]
#   ./trim-silence.sh input.mp4               # 影片也行
#
# 可用環境變數覆寫：
#   MARGIN=0.3sec THRESH=3% ./trim-silence.sh in.wav
#
set -euo pipefail

IN="${1:?用法: $0 input.(wav|mp4) [output]}"
EXT="${IN##*.}"
OUT="${2:-${IN%.*}-trimmed.${EXT}}"

MARGIN="${MARGIN:-0.2sec}"   # 切點左右保留的 padding（別設 0，會很喘）
THRESH="${THRESH:-4%}"       # 低於此音量視為靜默

command -v auto-editor >/dev/null || { echo "需要 auto-editor：pip3 install auto-editor"; exit 1; }
command -v ffmpeg      >/dev/null || { echo "需要 ffmpeg：brew install ffmpeg"; exit 1; }
[ -f "$IN" ] || { echo "找不到輸入檔：$IN"; exit 1; }

auto-editor "$IN" --margin "$MARGIN" --edit "audio:threshold=${THRESH}" -o "$OUT"
echo "完成：$OUT"
