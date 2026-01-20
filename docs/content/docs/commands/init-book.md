---
title: init-book
weight: 3
---

# init-book

一站式書籍初始化指令，結合 AI 處理自動產生 metadata 和目錄結構。

> [!NOTE]
> 此指令使用 **兩階段 AI 互動工作流程**，需要 Claude Code 處理任務。
> 詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 使用方式

```bash
# Phase 1: 產生 AI 任務
./gradlew initBook -Pinput=templates/book-input.yaml

# 對 Claude Code 說：「請處理 AI 任務」

# Phase 2: 繼續執行（AI 處理完成後）
./gradlew initBook -Pinput=templates/book-input.yaml

# 預覽模式
./gradlew initBook -Pinput=templates/book-input.yaml -PdryRun=true
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-Pinput` | ✓ | YAML 輸入檔路徑 |
| `-PdryRun` | - | 預覽模式 |

## YAML 輸入格式

```yaml
# 明確資訊（必填）
chinese_title: "原子習慣"
english_title: "Atomic Habits"
author: "James Clear"
publication_date: "2019-06-01"

# 書封面圖片網址（會自動下載並縮放）
cover_url: "https://example.com/atomic-habits-cover.jpg"

# 購書網址
purchase_url: "https://www.amazon.com/dp/B07D23CFGR"

# AI 處理的內容
table_of_contents: |
  導讀 為什麼原子習慣比你想像的更有用

  第一部 基本原理
    第一章 原子習慣的驚人力量
    第二章 習慣如何塑造你的身分認同
```

詳細格式請參閱 [YAML 輸入說明]({{< relref "/docs/reference/yaml-input" >}})。

## 執行流程

### Phase 1: 產生 AI 任務

1. 讀取 YAML 輸入檔
2. 產生 `ai-tasks/input/metadata-request.json`
3. 產生 `ai-tasks/input/structure-request.json`
4. 提示使用者請 Claude Code 處理

### Claude Code 處理

1. 產生書籍 metadata（repo name、description、topics、category）
2. 產生 docs 資料夾結構（從目錄文字）
3. 寫入 `ai-tasks/output/`

### Phase 2: 完成建立

1. 讀取 AI 產生的 metadata 和結構
2. 從模板建立 GitHub repo
3. Clone 到本機工作目錄（依 category 分類）
4. 更新模板檔案（README、hugo.toml、go.mod、_index.md）
5. 下載並縮放封面圖片
6. 建立 docs 資料夾結構

## 輸出範例

### Phase 1

```
Initializing book from: templates/book-input.yaml
Reading book info...
  Title: 原子習慣 (Atomic Habits)
  Author: James Clear

Generating AI tasks...
  ✓ Created: ai-tasks/input/metadata-request.json
  ✓ Created: ai-tasks/input/structure-request.json

Please ask Claude Code to process AI tasks:
  「請處理 AI 任務」

After Claude Code completes, run this command again to continue.
```

### Phase 2

```
Initializing book from: templates/book-input.yaml
Found completed AI tasks...

AI Generated Metadata:
  Repo name: atomic-habits
  Description: Atomic Habits | James Clear | A guide to building good habits
  Category: growth-book-summary
  Topics: habits, self-improvement, book-summary

Creating GitHub repository...
  ✓ Repository created: atomic-habits

Cloning to local directory...
  ✓ Cloned to: /path/to/books/growth-book-summary/atomic-habits

Updating template files...
  ✓ README.md updated
  ✓ hugo.toml updated
  ✓ go.mod updated
  ✓ _index.md updated

Downloading cover image...
  ✓ Cover saved and resized

Creating docs structure...
  ✓ Created: 01-fundamentals/
  ✓ Created: 01-fundamentals/01-power-of-habits/
  ✓ Created: 01-fundamentals/02-identity/
  ...

Done! Book initialized successfully.
```

## 相關指令

- [rebuild-docs]({{< relref "/docs/commands/rebuild-docs" >}}) - 重建 docs 結構
- [convert-docs]({{< relref "/docs/commands/convert-docs" >}}) - 轉換文件格式
