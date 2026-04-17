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

{{% include-prompt "book-metadata.txt" %}}

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
