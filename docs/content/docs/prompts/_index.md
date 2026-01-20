---
title: Prompt 範本
weight: 4
bookCollapseSection: true
---

# Prompt 範本

Hugo Book Manager 提供多種可重複使用的 Prompt 範本，用於書籍專案的維護任務。

## 使用方式

1. 使用 [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) 指令產生 prompt
2. 複製 prompt 內容
3. 在目標書籍專案開啟 Claude Code
4. 貼上 prompt，Claude 會自動執行任務

```bash
# 產生 prompt（輸出到終端）
./gradlew generatePrompt -Ptype=doc-convert

# 儲存到檔案
./gradlew generatePrompt -Ptype=doc-convert -Poutput=PROMPT.md
```

## 可用的 Prompt

### 文件轉換

| Prompt | 說明 |
|--------|------|
| [doc-convert]({{< relref "/docs/prompts/doc-convert" >}}) | 轉換 Markdown 為 Hugo Book 格式 |

### AI 任務用（內部使用）

| Prompt | 說明 |
|--------|------|
| [book-metadata]({{< relref "/docs/prompts/book-metadata" >}}) | 產生書籍 repo metadata |
| [book-structure]({{< relref "/docs/prompts/book-structure" >}}) | 產生 docs 資料夾結構 |

### 內容增強

| Prompt | 說明 |
|--------|------|
| [enhance-katex]({{< relref "/docs/prompts/enhance-katex" >}}) | 加入 KaTeX 數學公式支援 |
| [enhance-mermaid]({{< relref "/docs/prompts/enhance-mermaid" >}}) | 加入 Mermaid 圖表支援 |

### 格式審查

| Prompt | 說明 |
|--------|------|
| [review-markdown]({{< relref "/docs/prompts/review-markdown" >}}) | 審查並統一 Markdown 格式 |
| [restructure-folders]({{< relref "/docs/prompts/restructure-folders" >}}) | 根據標題重新命名資料夾 |
| [list-to-table]({{< relref "/docs/prompts/list-to-table" >}}) | 將條列式轉換為表格 |

## Prompt 設計原則

這些 Prompt 的設計遵循以下原則：

1. **任務明確**：每個 Prompt 專注於一個特定任務
2. **規則清晰**：提供具體的格式規範和範例
3. **可獨立使用**：複製貼上即可執行，不需額外設定
4. **漸進執行**：先掃描，再確認，最後執行
