#!/bin/bash
# 產生 convert-request.json，自動排除已轉換的檔案
#
# 用法：
#   ./scripts/generate-convert-request.sh
#
# 此腳本會：
# 1. 掃描 /home/andrew/workspace/andrew/notes/geek-time 下所有課程
# 2. 找出每個課程 cleaned/ 資料夾中的 .md 檔案
# 3. 排除已存在於 cleaned/converted/ 的檔案
# 4. 產生 ai-tasks/input/convert-request.json

set -e

BASE_DIR="/home/andrew/workspace/andrew/notes/geek-time"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_FILE="$SCRIPT_DIR/ai-tasks/input/convert-request.json"

# 正規化檔名：將彎引號轉換為直引號
normalize_filename() {
  echo "$1" | sed 's/[""]/"/g'
}

# 檢查檔案是否已轉換（考慮引號差異）
file_exists_normalized() {
  local dir="$1"
  local filename="$2"
  local normalized=$(normalize_filename "$filename")

  # 直接匹配
  if [ -f "$dir/$filename" ]; then
    return 0
  fi

  # 正規化後匹配
  if [ -f "$dir/$normalized" ]; then
    return 0
  fi

  # 遍歷目錄查找正規化後相同的檔案
  for f in "$dir"/*.md 2>/dev/null; do
    if [ -f "$f" ]; then
      existing_name=$(basename "$f")
      existing_normalized=$(normalize_filename "$existing_name")
      if [ "$normalized" = "$existing_normalized" ]; then
        return 0
      fi
    fi
  done

  return 1
}

echo "掃描目錄: $BASE_DIR"
echo "輸出檔案: $OUTPUT_FILE"
echo ""

# 開始 JSON
echo '{' > "$OUTPUT_FILE"
echo '  "taskType": "convert",' >> "$OUTPUT_FILE"
echo '  "promptFile": "templates/prompts/doc-convert.txt",' >> "$OUTPUT_FILE"
echo '  "files": [' >> "$OUTPUT_FILE"

first=true
index=0
skipped=0

# 掃描所有子目錄
for course_dir in "$BASE_DIR"/*/; do
  course_name=$(basename "$course_dir")
  cleaned_dir="$course_dir/cleaned"
  converted_dir="$cleaned_dir/converted"

  # 跳過沒有 cleaned 資料夾的課程
  if [ ! -d "$cleaned_dir" ]; then
    continue
  fi

  # 確保 converted 資料夾存在
  mkdir -p "$converted_dir"

  # 處理每個 .md 檔案
  for input_file in "$cleaned_dir"/*.md; do
    # 跳過不存在的檔案（glob 沒匹配到時）
    if [ ! -f "$input_file" ]; then
      continue
    fi

    filename=$(basename "$input_file")
    output_file="$converted_dir/$filename"

    # 檢查是否已轉換（考慮引號差異）
    if file_exists_normalized "$converted_dir" "$filename"; then
      skipped=$((skipped + 1))
      continue
    fi

    index=$((index + 1))

    if [ "$first" = true ]; then
      first=false
    else
      echo ',' >> "$OUTPUT_FILE"
    fi

    # 輸出 JSON 項目
    printf '    {"index": %d, "input": "%s", "output": "%s"}' \
      "$index" "$input_file" "$output_file" >> "$OUTPUT_FILE"
  done
done

echo '' >> "$OUTPUT_FILE"
echo '  ]' >> "$OUTPUT_FILE"
echo '}' >> "$OUTPUT_FILE"

echo "========================================"
echo "完成！"
echo "待轉換: $index 個檔案"
echo "已跳過: $skipped 個檔案（已存在於 converted/）"
echo "========================================"

if [ "$index" -eq 0 ]; then
  echo ""
  echo "沒有新檔案需要轉換。"
else
  echo ""
  echo "分配建議（5 個 Claude 視窗）："
  per_session=$(( (index + 4) / 5 ))
  for i in 1 2 3 4 5; do
    start=$(( (i-1) * per_session + 1 ))
    end=$(( i * per_session ))
    if [ $end -gt $index ]; then
      end=$index
    fi
    if [ $start -le $index ]; then
      echo "  Claude $i: index $start - $end"
    fi
  done
fi
