---
title: Queue YAML 格式
weight: 2
---

# Queue YAML 格式

[init-books]({{< relref "/docs/commands/init-book" >}}) 指令從一份 queue
YAML 讀取待辦書籍。預設路徑是 `templates/books-queue.yaml`，可用
`-Pqueue=<path>` 指定其他檔案。

## 結構

頂層是 `books:` 列表，每筆書都帶有自己的 `id` 與 `status`，CLI 依序處理
所有 `pending` 的書。

```yaml
books:
-   id: example-book
    status: pending
    chinese_title: "範例書名"
    english_title: "Example Book Title"
    author: "Author Name"
    publication_date: "January 1, 2025"
    cover_url: "https://example.com/cover.jpg"
    purchase_url: "https://www.amazon.com/example"
    table_of_contents: |
      Part I: Introduction
        Chapter 1: Getting Started
        Chapter 2: Overview
      Part II: Deep Dive
        Chapter 3: Core Concepts
        Chapter 4: Advanced Topics
```

範本檔案：`templates/books-queue.example.yaml`。複製為
`templates/books-queue.yaml` 後填入書籍即可使用。

## 欄位說明

| 欄位 | 必填 | 說明 |
|------|:----:|------|
| `id` | ✓ | queue 中唯一的書籍識別碼，建議用 kebab-case |
| `status` | ✓ | `pending` / `processing` / `completed` / `error` |
| `chinese_title` | ✓ | 中文書名 |
| `english_title` | ✓ | 英文書名 |
| `author` | ✓ | 作者名稱 |
| `publication_date` | - | 出版日期 |
| `cover_url` | - | 書封面圖片直連網址（自動下載並縮放為 500px 寬） |
| `purchase_url` | - | 購書連結 |
| `table_of_contents` | ✓ | 目錄文字，用於 AI 產生 docs 結構 |

## 狀態說明

| 狀態 | 意義 |
|------|------|
| `pending` | 等待處理。CLI 會挑下一本進入 Phase 1。 |
| `processing` | AI 任務已產生，等待 Claude 寫回結果 + 等使用者再執行進入 Phase 2。 |
| `completed` | repo 已成功建立。 |
| `error` | 建立失敗。可用 `-Pid=<id> -Preset=true` 重設為 pending 後重試。 |

## table_of_contents 格式建議

目錄文字是 AI 產生 docs 結構的依據，建議：

1. **使用縮排表示層級**（頂層為「部 / Part」，縮排為「章 / Chapter」）
2. **保留原始中文標題**，AI 會自動轉成 kebab-case 的英文資料夾名
3. 若是無「部」的書，章節直接放頂層即可

```
第一部 部標題
  第一章 章標題
  第二章 章標題
第二部 部標題
  第三章 章標題
```

或：

```
第一章 章標題
第二章 章標題
第三章 章標題
```

## AI 處理

init-books 使用兩階段 AI 工作流程：

1. **Phase 1**：CLI 從 queue 取下一本 pending 書，產生 AI 任務
2. **Claude 處理**：產生 metadata 和 docs 結構，寫回 `ai-tasks/output/`
3. **Phase 2**：CLI 讀取 AI 結果，完成建立並把該書標為 `completed`

詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 相關指令

- [init-books]({{< relref "/docs/commands/init-book" >}}) - 使用此格式的指令
