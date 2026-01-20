---
title: clean-docs
weight: 4
---

# clean-docs

將 PDF、HTML、MHTML 檔案轉換為乾淨的 Markdown 格式。這是文件處理的第一階段。

## 使用方式

```bash
# 清理整個目錄
./gradlew cleanDocs -PinputDir=/path/to/html/files

# 預覽模式
./gradlew cleanDocs -PinputDir=/path/to/html/files -PdryRun=true

# 指定輸出目錄
./gradlew cleanDocs -PinputDir=/path/to/html -PoutputDir=/path/to/output

# 清理單一檔案
./gradlew cleanDocs -PinputDir=. -Psingle=sampleFile.mhtml

# CLI 模式
./gradlew run --args="clean-docs --input-dir /path/to/html --dry-run"
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-PinputDir` | ✓ | 輸入目錄路徑 |
| `-PoutputDir` | - | 輸出目錄（預設為輸入目錄下的 `cleaned/`） |
| `-Psingle` | - | 單一檔案名稱（在 inputDir 中） |
| `-PdryRun` | - | 預覽模式 |

## 支援格式

| 格式 | 處理方式 |
|------|----------|
| **PDF** | 使用 Apache PDFBox 擷取文字，偵測標題、列表、段落 |
| **MHTML** | 解析 multipart 格式，解碼 quoted-printable |
| **HTML/HTM** | 使用 Jsoup 解析，擷取主要內容 |

## 處理功能

- 擷取標題結構（H1-H6）
- 保留列表格式（有序、無序）
- 保留粗體和斜體
- 移除廣告和導覽元素
- 處理 Slate.js 編輯器格式（極客時間等）
- 解碼 quoted-printable 編碼

## 輸出範例

```
Cleaning documents from: /path/to/html
Output directory: /path/to/html/cleaned

Found 5 documents to clean:
  - chapter01.mhtml
  - chapter02.mhtml
  - chapter03.html
  - appendix.pdf
  - notes.htm

[1/5] Cleaning: chapter01.mhtml
  ✓ Saved: cleaned/chapter01.md

[2/5] Cleaning: chapter02.mhtml
  ✓ Saved: cleaned/chapter02.md

...

Done! Cleaned 5 documents.
```

## 工作流程

clean-docs 通常是文件處理的第一步：

```
原始文件 (PDF/HTML/MHTML)
    ↓
[clean-docs] 清理為 Markdown
    ↓
清理後的 Markdown
    ↓
[convert-docs] 轉換為 Hugo Book 格式
    ↓
Hugo Book 格式的文件
```

## 相關指令

- [convert-docs]({{< relref "/docs/commands/convert-docs" >}}) - 第二階段：轉換為 Hugo Book 格式
