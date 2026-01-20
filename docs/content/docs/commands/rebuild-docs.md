---
title: rebuild-docs
weight: 6
---

# rebuild-docs

根據目錄文字重新建立 Hugo Book 的 docs 資料夾結構。

> [!NOTE]
> 此指令使用 **兩階段 AI 互動工作流程**，需要 Claude Code 處理任務。
> 詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 使用方式

```bash
# Phase 1: 產生 AI 任務（從檔案讀取目錄）
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt

# 對 Claude Code 說：「請處理 AI 任務」

# Phase 2: 套用結構（AI 處理完成後）
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt

# 跳過確認提示
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt -Pyes=true

# 預覽模式
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt -PdryRun=true
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-PrepoDir` | ✓ | Hugo Book 專案目錄 |
| `-Ptoc` | ✓ | 目錄文字檔案路徑 |
| `-Pyes` | - | 跳過確認提示，直接執行 |
| `-PdryRun` | - | 預覽模式 |

## 目錄格式

目錄檔案應使用縮排表示層級：

```
第一部 基礎觀念
  第一章 微小改變的複利效應
  第二章 習慣如何塑造身分認同
  第三章 建立習慣的四步驟
第二部 讓習慣顯而易見
  第四章 行為改變的起點
  第五章 執行意圖的力量
```

## 執行流程

### Phase 1: 產生 AI 任務

1. 驗證目標目錄是有效的 Hugo Book 專案
2. 讀取目錄文字
3. 產生 `ai-tasks/input/structure-request.json`
4. 提示使用者請 Claude Code 處理

### Claude Code 處理

1. 分析目錄文字的層級結構
2. 產生資料夾命名（`NN-kebab-case`）
3. 產生 `ai-tasks/output/structure-response.json`

### Phase 2: 套用結構

1. 讀取 AI 產生的結構
2. 預覽變更（要刪除/建立的資料夾）
3. 使用者確認（可用 `--yes` 跳過）
4. 刪除現有 doc 資料夾
5. 建立新的資料夾結構，包含 `_index.md`

## 產生的結構

```
site/content/docs/
├── 01-fundamentals/
│   ├── _index.md          (title: "第一部 基礎觀念", weight: 1)
│   ├── 01-compound-effect/
│   │   └── _index.md      (title: "第一章 微小改變的複利效應", weight: 1)
│   ├── 02-identity/
│   │   └── _index.md      (title: "第二章 習慣如何塑造身分認同", weight: 2)
│   └── 03-four-steps/
│       └── _index.md      (title: "第三章 建立習慣的四步驟", weight: 3)
└── 02-make-it-obvious/
    ├── _index.md          (title: "第二部 讓習慣顯而易見", weight: 2)
    └── ...
```

## 輸出範例

### Phase 1

```
Rebuilding docs structure for: /path/to/book

Validating Hugo Book repository...
  ✓ Valid Hugo Book repository

Reading table of contents from: /path/to/toc.txt

Generating AI task...
  ✓ Created: ai-tasks/input/structure-request.json

Please ask Claude Code to process AI tasks:
  「請處理 AI 任務」

After Claude Code completes, run this command again to apply.
```

### Phase 2

```
Rebuilding docs structure for: /path/to/book

Found completed AI task...

Preview changes:
  Delete: site/content/docs/chapter-01/
  Delete: site/content/docs/chapter-02/
  Create: site/content/docs/01-fundamentals/
  Create: site/content/docs/01-fundamentals/01-compound-effect/
  Create: site/content/docs/01-fundamentals/02-identity/
  ...

Proceed? [y/N] y

Applying structure...
  ✓ Deleted: chapter-01/
  ✓ Deleted: chapter-02/
  ✓ Created: 01-fundamentals/
  ✓ Created: 01-fundamentals/01-compound-effect/
  ...

Done! Docs structure rebuilt.
```

## 相關指令

- [init-book]({{< relref "/docs/commands/init-book" >}}) - 初始化時自動建立結構
