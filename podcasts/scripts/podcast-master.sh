#!/usr/bin/env bash
#
# podcast-master.sh — 單人人聲母帶處理（two-pass loudnorm）
#
# 流程：High-pass 80Hz → 去噪 → 輕壓縮 → 響度正規化（-16 LUFS / -1 dBTP）
# 兩段式 loudnorm：先量測、再套用，響度才準。請在「剪輯完成後」對成品執行。
#
# 用法：
#   ./podcast-master.sh input.wav [output.wav]
#
# 可用環境變數覆寫預設（單聲道建議 TARGET_I=-19）：
#   TARGET_I=-19 ./podcast-master.sh in.wav
#   DENOISE=anull ./podcast-master.sh in.wav        # 關閉去噪
#
set -euo pipefail

IN="${1:?用法: $0 input.wav [output.wav]}"
OUT="${2:-${IN%.*}-master.wav}"

# ---- 可調參數 ----
TARGET_I="${TARGET_I:--16}"     # 整體響度 LUFS（單聲道建議 -19）
TARGET_TP="${TARGET_TP:--1}"    # 真實峰值上限 dBTP
TARGET_LRA="${TARGET_LRA:-11}"  # 響度範圍
HPF="${HPF:-80}"                # 高通頻率 Hz
DENOISE="${DENOISE:-afftdn=nf=-25}"  # 去噪（輕量）；不要就設為 anull
COMPRESS="${COMPRESS:-acompressor=threshold=-18dB:ratio=3:attack=20:release=250}"

PRE="highpass=f=${HPF},${DENOISE},${COMPRESS}"

command -v ffmpeg  >/dev/null || { echo "需要 ffmpeg：brew install ffmpeg"; exit 1; }
command -v python3 >/dev/null || { echo "需要 python3：brew install python"; exit 1; }
[ -f "$IN" ] || { echo "找不到輸入檔：$IN"; exit 1; }

echo "[1/2] 量測響度…"
MEASURE=$(ffmpeg -hide_banner -i "$IN" \
  -af "${PRE},loudnorm=I=${TARGET_I}:TP=${TARGET_TP}:LRA=${TARGET_LRA}:print_format=json" \
  -f null - 2>&1)

PARSE='import sys,json,re
s=sys.stdin.read()
m=re.search(r"\{[^{}]*\"input_i\"[^{}]*\}", s, re.S)
if not m:
    sys.stderr.write("無法解析 loudnorm 量測結果\n"); sys.exit(1)
d=json.loads(m.group(0))
print(d["input_i"], d["input_tp"], d["input_lra"], d["input_thresh"], d["target_offset"])'

read -r MI MTP MLRA MTHRESH OFFSET < <(printf '%s' "$MEASURE" | python3 -c "$PARSE")
echo "    measured_I=$MI  TP=$MTP  LRA=$MLRA  thresh=$MTHRESH  offset=$OFFSET"

echo "[2/2] 套用母帶處理 → $OUT"
ffmpeg -hide_banner -y -i "$IN" \
  -af "${PRE},loudnorm=I=${TARGET_I}:TP=${TARGET_TP}:LRA=${TARGET_LRA}:measured_I=${MI}:measured_TP=${MTP}:measured_LRA=${MLRA}:measured_thresh=${MTHRESH}:offset=${OFFSET}:linear=true:print_format=summary" \
  -ar 48000 "$OUT"

echo "完成：$OUT"
