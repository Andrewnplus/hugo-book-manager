---
title: book-metadata
weight: 2
---

# book-metadata

產生書籍的 GitHub repository metadata，包含 repo 名稱、描述、topics 和分類。

## 用途

- 由 [init-book]({{< relref "/docs/commands/init-book" >}}) 指令內部使用
- 產生一致的 repo 命名和分類

> [!NOTE]
> 此 Prompt 通常不需要手動使用，會由 AI 任務系統自動載入。

## 完整 Prompt

以下是完整的 Prompt 內容：

---

````markdown
You are a helpful assistant that generates metadata for book note repositories.

Given the book information:
- Chinese Title: {{chinese_title}}
- English Title: {{english_title}}

Please provide the following information in JSON format:
{
  "repoName": "kebab-case-repo-name",
  "englishTitle": "English Book Title",
  "chineseTitle": "繁體中文書名",
  "description": "English Title | Author Name | A brief description in English",
  "topics": ["topic1", "topic2", "book-summary", "category-book-summary"],
  "category": "growth-book-summary"
}

Rules:
1. repoName: lowercase, kebab-case, derived from English title (e.g., "atomic-habits")
2. chineseTitle: Traditional Chinese (繁體中文)
3. description: Format is "English Title | Author | Brief description" (max 100 chars total)
4. topics: Include relevant topics + must include "book-summary" + one category topic
5. category: Must be exactly ONE of these:
   - growth-book-summary (self-improvement, habits, productivity, mindset)
   - business-book-summary (business, entrepreneurship, management, leadership)
   - tech-book-summary (technology, programming, software, AI)
   - relation-book-summary (relationships, communication, social skills)
   - family-book-summary (parenting, family, marriage)
   - faith-book-summary (spirituality, religion, meaning of life)

Only return valid JSON, no markdown code blocks, no explanations.
````

---

## 輸出範例

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

## 分類說明

| Category | 適用範圍 |
|----------|----------|
| `growth-book-summary` | 自我成長、習慣、生產力、心態 |
| `business-book-summary` | 商業、創業、管理、領導力 |
| `tech-book-summary` | 技術、程式設計、軟體、AI |
| `relation-book-summary` | 人際關係、溝通、社交技巧 |
| `family-book-summary` | 親子教育、家庭、婚姻 |
| `faith-book-summary` | 靈性、宗教、人生意義 |

## 相關文件

- [init-book]({{< relref "/docs/commands/init-book" >}}) - 使用此 prompt 的指令
- [任務類型]({{< relref "/docs/ai-workflow/task-types" >}}) - AI 任務格式說明
