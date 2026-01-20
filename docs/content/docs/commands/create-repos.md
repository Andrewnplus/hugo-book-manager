---
title: create-repos
weight: 2
---

# create-repos

從 CSV 檔案批次建立 GitHub 儲存庫。

## 使用方式

```bash
# Gradle Task
./gradlew createRepos -Pcsv=data/books.csv

# 預覽模式
./gradlew createRepos -Pcsv=data/books.csv -PdryRun=true

# 從第 5 本開始（續傳）
./gradlew createRepos -Pcsv=data/books.csv -PstartFrom=5

# CLI 模式
./gradlew run --args="create-repos --csv data/books.csv --dry-run"
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-Pcsv` | ✓ | CSV 檔案路徑 |
| `-PdryRun` | - | 預覽模式，不實際建立 |
| `-PstartFrom` | - | 從第 N 本開始（用於續傳） |

## CSV 格式

CSV 檔案需包含以下欄位：

| 欄位 | 說明 |
|------|------|
| `chinese_title` | 中文書名 |
| `english_title` | 英文書名 |
| `author` | 作者 |
| `repo_name` | GitHub repo 名稱（kebab-case） |
| `description` | Repo 描述 |
| `topics` | 空格分隔的 topics |
| `category` | 書籍分類 |
| `category_topic` | 分類專屬 topic |

詳細格式請參閱 [CSV 格式說明]({{< relref "/docs/reference/csv-format" >}})。

## 執行流程

針對每一本書：

1. 從模板建立/更新私有 repo
2. 設定 homepage 和 description
3. 加入 topics
4. 啟用 GitHub Pages
5. 將 repo 加入 starred
6. 等待 3 秒（rate limiting）

## 輸出範例

```
Creating repos from: data/test-3-books.csv
Found 3 books to create

[1/3] Creating repo: atomic-habits
  ✓ Repository created
  ✓ Homepage set
  ✓ Topics added
  ✓ GitHub Pages enabled
  ✓ Starred

[2/3] Creating repo: deep-work
  ✓ Repository created
  ✓ Homepage set
  ✓ Topics added
  ✓ GitHub Pages enabled
  ✓ Starred

[3/3] Creating repo: thinking-fast-and-slow
  ✓ Repository created
  ✓ Homepage set
  ✓ Topics added
  ✓ GitHub Pages enabled
  ✓ Starred

Done! Created 3 repositories.
```

## 相關指令

- [check-env]({{< relref "/docs/commands/check-env" >}}) - 執行前先檢查環境
- [init-book]({{< relref "/docs/commands/init-book" >}}) - 單本書籍的完整初始化（含 AI 處理）
