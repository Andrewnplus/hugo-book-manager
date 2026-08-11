---
title: 運作方式
weight: 1
---

# AI 工作流程運作方式

## 兩階段互動流程

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   CLI 執行      │────▶│  產生輸入檔案   │────▶│   CLI 暫停      │
│   (Gradle)      │     │  ai-tasks/input │     │   等待處理      │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
        ┌────────────────────────────────────────────────┘
        ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  用戶告訴 Claude│────▶│  Claude 讀取    │────▶│  Claude 寫入    │
│  「請處理 AI 任務」│     │  + 處理任務     │     │  ai-tasks/output│
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
        ┌────────────────────────────────────────────────┘
        ▼
┌─────────────────┐     ┌─────────────────┐
│  用戶再次執行   │────▶│  CLI 讀取輸出   │
│  CLI (同指令)   │     │  繼續後續步驟   │
└─────────────────┘     └─────────────────┘
```

## Phase 1: CLI 產生任務

當你執行需要 AI 處理的指令時，CLI 會：

1. 收集必要資訊（書籍標題、目錄內容等）
2. 在 `ai-tasks/input/` 建立任務檔案（JSON 格式）
3. 顯示提示訊息，請你告訴 Claude Code 處理任務
4. 暫停等待

**範例輸出：**

```
Generating AI tasks...
  ✓ Created: ai-tasks/input/metadata-request.json
  ✓ Created: ai-tasks/input/structure-request.json

Please ask Claude Code to process AI tasks:
  「請處理 AI 任務」

After Claude Code completes, run this command again to continue.
```

## Claude Code 處理

當你對 Claude Code 說 **「請處理 AI 任務」** 時，Claude 會：

1. 檢查 `ai-tasks/input/` 目錄
2. 讀取任務 JSON 檔案
3. 根據任務類型讀取對應的 prompt 模板
4. 處理任務內容
5. 將結果寫入 `ai-tasks/output/`（或直接寫入指定路徑）

> [!TIP]
>
> Claude Code 會自動識別任務類型並載入對應的 prompt。你不需要手動指定。

## Phase 2: CLI 繼續執行

處理完成後，再次執行相同指令，CLI 會：

1. 偵測到 `ai-tasks/output/` 中有完成的任務
2. 讀取 AI 產生的結果
3. 繼續執行後續步驟（建立 repo、建立資料夾結構等）
4. 清理任務檔案

## 任務檔案位置

```
hugo-book-manager/
├── ai-tasks/
│   ├── input/              # CLI 產生的任務請求
│   │   ├── .gitkeep
│   │   ├── metadata-request.json
│   │   └── structure-request.json
│   └── output/             # Claude 產生的結果
│       ├── .gitkeep
│       ├── metadata-response.json
│       └── structure-response.json
```

> [!NOTE]
>
> `ai-tasks/` 目錄已加入 `.gitignore`，任務檔案不會被提交到版本控制。

## 錯誤處理

### 任務未完成

如果在 Phase 1 之後直接執行 Phase 2，但 Claude 尚未處理：

```
Error: AI tasks not completed yet.
Please ask Claude Code to process:
  「請處理 AI 任務」
```

### 任務處理失敗

如果 Claude 處理時發生錯誤，可以：

1. 刪除 `ai-tasks/input/` 中的檔案
2. 重新執行 Phase 1
3. 再次請 Claude Code 處理

## 相關文件

- [任務類型]({{< relref "/docs/ai-workflow/task-types" >}}) - 各種任務的詳細格式
