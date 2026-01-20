---
title: generate-prompt
weight: 9
---

# generate-prompt

產生可重複使用的 Prompt 範本，用於書籍專案的維護任務。

## 使用方式

```bash
# 列出可用的 prompt
./gradlew generatePrompt

# 產生特定 prompt（輸出到終端）
./gradlew generatePrompt -Ptype=restructure-folders

# 儲存到檔案
./gradlew generatePrompt -Ptype=restructure-folders -Poutput=/path/to/book/PROMPT.md

# CLI 模式
./gradlew run --args="generate-prompt --list"
./gradlew run --args="generate-prompt --type restructure-folders"
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-Ptype` | - | Prompt 類型（見下方列表） |
| `-Poutput` | - | 輸出檔案路徑 |

## 可用的 Prompt 類型

| 類型 | 說明 | 詳細文件 |
|------|------|----------|
| `doc-convert` | 轉換文件為 Hugo Book 格式 | [doc-convert]({{< relref "/docs/prompts/doc-convert" >}}) |
| `restructure-folders` | 根據 `_index.md` 標題重新命名資料夾 | [restructure-folders]({{< relref "/docs/prompts/restructure-folders" >}}) |
| `enhance-katex` | 加入 KaTeX 數學公式支援 | [enhance-katex]({{< relref "/docs/prompts/enhance-katex" >}}) |
| `enhance-mermaid` | 加入 Mermaid 圖表支援 | [enhance-mermaid]({{< relref "/docs/prompts/enhance-mermaid" >}}) |
| `review-markdown` | 審查並統一 Markdown 格式 | [review-markdown]({{< relref "/docs/prompts/review-markdown" >}}) |
| `list-to-table` | 將條列式轉換為表格 | [list-to-table]({{< relref "/docs/prompts/list-to-table" >}}) |

## 使用流程

1. 產生需要的 prompt
2. 複製 prompt 內容
3. 在目標書籍專案開啟 Claude Code
4. 貼上 prompt，Claude 會自動執行任務

## 輸出範例

### 列出可用 prompt

```
Available prompt types:

  doc-convert          Convert documents to Hugo Book format
  restructure-folders  Rename folders based on _index.md titles
  enhance-katex        Add KaTeX math formula support
  enhance-mermaid      Add Mermaid diagram support
  review-markdown      Review and fix markdown quality issues
  list-to-table        Convert list items to markdown tables

Usage:
  ./gradlew generatePrompt -Ptype=<type>
  ./gradlew generatePrompt -Ptype=<type> -Poutput=PROMPT.md
```

### 產生 prompt

```
Generating prompt: restructure-folders

--- Prompt Start ---
# 任務：根據目錄建立 Hugo Book 文件結構
...
--- Prompt End ---

Copy the prompt above and paste it to Claude Code in your book project.
```

## 相關文件

- [Prompt 範本]({{< relref "/docs/prompts" >}}) - 完整的 Prompt 說明和內容
