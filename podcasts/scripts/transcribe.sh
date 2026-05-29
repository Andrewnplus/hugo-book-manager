#!/usr/bin/env bash
#
# transcribe.sh — 產生中文逐字稿 / 字幕（whisper.cpp，Apple Silicon 吃 Metal 加速）
#
# 輸出與輸入同名的 .srt（字幕）與 .txt（純文字稿）。
# 逐字稿可拿去 Descript / DaVinci 做「文字稿驅動剪輯」，或當節目文字稿。
#
# 用法：
#   ./transcribe.sh input.wav
#
# 首次使用需下載模型，預設找：~/.whisper-models/ggml-large-v3.bin
#   可用環境變數覆寫：MODEL=/path/to/ggml-medium.bin ./transcribe.sh in.wav
#   語言：LANG_CODE=en ./transcribe.sh in.wav
#
set -euo pipefail

IN="${1:?用法: $0 input.(wav|mp3|mp4)}"
MODEL="${MODEL:-$HOME/.whisper-models/ggml-large-v3.bin}"
LANG_CODE="${LANG_CODE:-zh}"
TMP="${IN%.*}-16k.wav"

# whisper.cpp 在不同版本下可執行檔名不同（新版 whisper-cli，舊版 main）
WHISPER_BIN="$(command -v whisper-cli || command -v whisper-cpp || command -v main || true)"

command -v ffmpeg >/dev/null || { echo "需要 ffmpeg：brew install ffmpeg"; exit 1; }
[ -n "$WHISPER_BIN" ] || { echo "需要 whisper.cpp：brew install whisper-cpp"; exit 1; }
[ -f "$IN" ]    || { echo "找不到輸入檔：$IN"; exit 1; }
[ -f "$MODEL" ] || { echo "找不到模型：$MODEL（請下載 ggml-large-v3.bin 放到該路徑）"; exit 1; }

echo "[1/2] 轉 16k 單聲道 wav…"
ffmpeg -hide_banner -y -i "$IN" -ar 16000 -ac 1 "$TMP"

echo "[2/2] 轉錄（$LANG_CODE）…"
"$WHISPER_BIN" -m "$MODEL" -l "$LANG_CODE" -osrt -otxt "$TMP" -of "${IN%.*}"

rm -f "$TMP"
echo "完成：${IN%.*}.srt（字幕）/ ${IN%.*}.txt（文字稿）"
