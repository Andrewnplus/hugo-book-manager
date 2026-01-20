---
title: 任務類型
weight: 2
---

# AI 任務類型

Hugo Book Manager 定義了三種 AI 任務類型，每種都有特定的輸入輸出格式。

## 任務總覽

| 任務檔案 | 用途 | 輸出 |
|----------|------|------|
| `metadata-request.json` | 從書名產生 repo metadata | `metadata-response.json` |
| `structure-request.json` | 從目錄產生 docs 結構 | `structure-response.json` |
| `convert-request.json` | 轉換 Markdown 格式 | 直接寫入指定路徑 |

---

## Metadata 任務

產生書籍的 GitHub repository metadata。

### 輸入格式

```json
{
  "type": "metadata",
  "chinese_title": "原子習慣",
  "english_title": "Atomic Habits",
  "author": "James Clear"
}
```

### 輸出格式

```json
{
  "repoName": "atomic-habits",
  "englishTitle": "Atomic Habits",
  "chineseTitle": "原子習慣",
  "description": "Atomic Habits | James Clear | A guide to building good habits",
  "topics": ["habits", "self-improvement", "book-summary", "growth-book-summary"],
  "category": "growth-book-summary"
}
```

### 欄位說明

| 欄位 | 說明 |
|------|------|
| `repoName` | kebab-case 格式的 repo 名稱 |
| `description` | 格式為 `English Title \| Author \| Brief description` |
| `topics` | 相關 topics，必須包含 `book-summary` 和 category topic |
| `category` | 分類（見下方分類列表） |

### 分類列表

| Category | 適用範圍 |
|----------|----------|
| `growth-book-summary` | 自我成長、習慣、生產力、心態 |
| `business-book-summary` | 商業、創業、管理、領導力 |
| `tech-book-summary` | 技術、程式設計、軟體、AI |
| `relation-book-summary` | 人際關係、溝通、社交技巧 |
| `family-book-summary` | 親子教育、家庭、婚姻 |
| `faith-book-summary` | 靈性、宗教、人生意義 |

---

## Structure 任務

從目錄文字產生 Hugo docs 資料夾結構。

### 輸入格式

```json
{
  "type": "structure",
  "table_of_contents": "第一部 基本原理\n  第一章 原子習慣的驚人力量\n  第二章 習慣如何塑造身分認同\n第二部 讓習慣變得明顯\n  第三章 培養習慣的最佳方式"
}
```

### 輸出格式

```json
{
  "sections": [
    {
      "folderName": "01-fundamentals",
      "title": "第一部 基本原理",
      "weight": 1,
      "chapters": [
        {
          "folderName": "01-power-of-habits",
          "title": "第一章 原子習慣的驚人力量",
          "weight": 1
        },
        {
          "folderName": "02-identity",
          "title": "第二章 習慣如何塑造身分認同",
          "weight": 2
        }
      ]
    },
    {
      "folderName": "02-make-it-obvious",
      "title": "第二部 讓習慣變得明顯",
      "weight": 2,
      "chapters": [
        {
          "folderName": "01-habit-formation",
          "title": "第三章 培養習慣的最佳方式",
          "weight": 1
        }
      ]
    }
  ]
}
```

### 欄位說明

| 欄位 | 說明 |
|------|------|
| `folderName` | 格式為 `NN-kebab-case`（如 `01-fundamentals`） |
| `title` | 保留原始中文標題 |
| `weight` | 排序權重，從 1 開始 |
| `chapters` | 該部（Part）下的章節列表 |

### 結構規則

1. 資料夾名稱：`NN-kebab-case`，NN 為兩位數序號
2. 有「部」的書：建立父資料夾，章節為子資料夾
3. 無「部」的書：章節為頂層資料夾
4. 導讀/序言：weight 設為 0 或放在最前面

---

## Convert 任務

將 Markdown 檔案轉換為 Hugo Book 格式。

### 輸入格式

```json
{
  "type": "convert",
  "prompt": "templates/prompts/doc-convert.txt",
  "files": [
    {
      "input": "/path/to/chapter01.md",
      "output": "/path/to/site/content/docs/01-chapter/_index.md"
    },
    {
      "input": "/path/to/chapter02.md",
      "output": "/path/to/site/content/docs/02-chapter/_index.md"
    }
  ]
}
```

### 輸出

沒有 JSON 輸出檔案。Claude 會直接讀取每個輸入檔案，轉換後寫入指定的輸出路徑。

### 轉換內容

依照 prompt 規則，通常包含：

- 保留 frontmatter
- 使用適當的標題層級
- 整理列表格式
- 使用 Markdown Alert 格式標記重點
- 潤飾文字使其通順

詳細規則請參閱 [doc-convert Prompt]({{< relref "/docs/prompts/doc-convert" >}})。

---

## 處理 AI 任務

當任務檔案建立後，對 Claude Code 說：

```
請處理 AI 任務
```

Claude 會自動：

1. 讀取 `ai-tasks/input/` 中的任務
2. 載入對應的 prompt 模板
3. 處理任務並產生輸出

## 相關文件

- [運作方式]({{< relref "/docs/ai-workflow/how-it-works" >}}) - 兩階段工作流程詳解
- [Prompt 範本]({{< relref "/docs/prompts" >}}) - 完整的 Prompt 說明
