---
title: convert-docs
weight: 5
---

# convert-docs

將 Markdown 檔案轉換為 Hugo Book 格式。這是文件處理的第二階段。

> [!NOTE]
> 此指令使用 **兩階段 AI 互動工作流程**，需要 Claude Code 處理任務。
> 詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 使用方式

```bash
# Phase 1: 產生 AI 任務
./gradlew convertDocs -PinputDir=/path/to/cleaned/md

# 對 Claude Code 說：「請處理 AI 任務」

# Phase 2: 驗證完成（AI 處理完成後）
./gradlew convertDocs -PinputDir=/path/to/cleaned/md

# 使用自訂 prompt
./gradlew convertDocs -PinputDir=/path/to/md -Pprompt=my-prompt.txt

# 預覽模式
./gradlew convertDocs -PinputDir=/path/to/md -PdryRun=true
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-PinputDir` | ✓ | Markdown 檔案目錄 |
| `-Pprompt` | - | 自訂 prompt 檔案（預設使用 `templates/prompts/doc-convert.txt`） |
| `-PdryRun` | - | 預覽模式 |

## 執行流程

### Phase 1: 產生 AI 任務

1. 掃描輸入目錄的 Markdown 檔案
2. 產生 `ai-tasks/input/convert-request.json`，包含檔案清單
3. 提示使用者請 Claude Code 處理

### Claude Code 處理

1. 讀取 `templates/prompts/doc-convert.txt` 中的 prompt
2. 逐一讀取每個 Markdown 檔案
3. 依照 prompt 規則轉換格式
4. 直接寫入指定的輸出路徑

### Phase 2: 驗證完成

1. 確認所有輸出檔案都已建立
2. 清理任務檔案
3. 報告完成狀態

## 轉換內容

依照預設 prompt，轉換包含：

| 項目 | 處理方式 |
|------|----------|
| **標題結構** | 使用 `##` 和 `###` 建立層級 |
| **列表** | 統一使用 `-` 作為無序列表符號 |
| **重點摘要** | 使用 Markdown Alert 格式（NOTE、TIP、IMPORTANT、WARNING、CAUTION） |
| **摺疊內容** | 使用 Hugo details shortcode |
| **程式碼** | 保留語言標記 |
| **潤飾** | 將散亂語句轉為通順書面語 |

詳細轉換規則請參閱 [doc-convert Prompt]({{< relref "/docs/prompts/doc-convert" >}})。

## 輸出範例

### Phase 1

```
Converting documents from: /path/to/cleaned/md
Found 10 markdown files

Generating AI task...
  ✓ Created: ai-tasks/input/convert-request.json

Please ask Claude Code to process AI tasks:
  「請處理 AI 任務」

After Claude Code completes, run this command again to verify.
```

### Phase 2

```
Converting documents from: /path/to/cleaned/md
Checking AI task completion...

Verifying output files:
  ✓ chapter01.md - converted
  ✓ chapter02.md - converted
  ✓ chapter03.md - converted
  ...

Cleaning up task files...
  ✓ Removed: ai-tasks/input/convert-request.json

Done! Converted 10 documents.
```

## 工作流程

convert-docs 是文件處理的第二步：

```
原始文件 (PDF/HTML/MHTML)
    ↓
[clean-docs] 清理為 Markdown
    ↓
清理後的 Markdown
    ↓
[convert-docs] 轉換為 Hugo Book 格式
    ↓
Hugo Book 格式的文件
```

## 相關指令

- [clean-docs]({{< relref "/docs/commands/clean-docs" >}}) - 第一階段：清理原始文件
- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生自訂 prompt
