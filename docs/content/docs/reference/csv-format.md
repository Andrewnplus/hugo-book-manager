---
title: CSV 格式
weight: 1
---

# CSV 格式

[create-repos]({{< relref "/docs/commands/create-repos" >}}) 指令使用 CSV 檔案批次建立 GitHub 儲存庫。

## 欄位說明

| 欄位 | 必填 | 說明 |
|------|:----:|------|
| `chinese_title` | ✓ | 中文書名 |
| `english_title` | ✓ | 英文書名 |
| `author` | ✓ | 作者名稱 |
| `repo_name` | ✓ | GitHub repo 名稱（kebab-case） |
| `description` | ✓ | Repo 描述 |
| `topics` | ✓ | 空格分隔的 topics |
| `category` | ✓ | 書籍分類 |
| `category_topic` | ✓ | 分類專屬 topic |
| `kindle_url` | - | Kindle 商店連結 |
| `books_tw_url` | - | 博客來連結 |
| `publication_date` | - | 出版日期 |
| `notes` | - | 備註 |

## 範例

```csv
chinese_title,english_title,author,repo_name,description,topics,category,category_topic,kindle_url,books_tw_url,publication_date,notes
原子習慣,Atomic Habits,James Clear,atomic-habits,Atomic Habits | James Clear | Building good habits,habits self-improvement,growth-book-summary,growth-book-summary,https://amazon.com/...,https://books.com.tw/...,2019-06-01,暢銷書
深度工作力,Deep Work,Cal Newport,deep-work,Deep Work | Cal Newport | Focus without distraction,productivity focus,growth-book-summary,growth-book-summary,,,2016-01-05,
```

## 欄位格式說明

### repo_name

- 使用 kebab-case（小寫字母加連字號）
- 通常從英文書名衍生
- 範例：`atomic-habits`、`thinking-fast-and-slow`

### description

建議格式：`English Title | Author | Brief description`

- 總長度不超過 100 字元
- 範例：`Atomic Habits | James Clear | A guide to building good habits`

### topics

- 使用空格分隔多個 topic
- 必須包含 `book-summary`
- 必須包含分類 topic（如 `growth-book-summary`）
- 範例：`habits self-improvement book-summary growth-book-summary`

### category

必須是以下其中之一：

| Category | 適用範圍 |
|----------|----------|
| `growth-book-summary` | 自我成長、習慣、生產力、心態 |
| `business-book-summary` | 商業、創業、管理、領導力 |
| `tech-book-summary` | 技術、程式設計、軟體、AI |
| `relation-book-summary` | 人際關係、溝通、社交技巧 |
| `family-book-summary` | 親子教育、家庭、婚姻 |
| `faith-book-summary` | 靈性、宗教、人生意義 |

### category_topic

通常與 `category` 相同，用於 GitHub topics。

## 範例檔案

專案中包含多個範例 CSV 檔案：

```
data/
├── test-3-books.csv       # 測試用（3 本書）
├── psychology-books.csv   # 心理學類
├── philosophy-books.csv   # 哲學類
├── self-management-books.csv  # 自我管理類
└── ...
```

## 相關指令

- [create-repos]({{< relref "/docs/commands/create-repos" >}}) - 使用此格式的指令
