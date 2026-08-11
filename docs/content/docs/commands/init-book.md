---
title: init-books
weight: 3
---

# init-books

從 queue 批次初始化書籍 repo 的指令。CLI 從 `templates/books-queue.yaml`
讀取待辦書籍，配合 Claude Code 的兩階段互動產生 metadata 與 docs 結構，
最後建立 GitHub repo 並 push 初始內容。

> [!NOTE]
>
> 此指令使用 **兩階段 AI 互動工作流程**，需要 Claude Code 處理任務。
> 詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 使用方式

```bash
# Phase 1: 從 queue 取下一本 pending 書，產生 AI 任務
./gradlew initBooks

# 對 Claude Code 說：「請處理 AI 任務」

# Phase 2: 再執行一次，讀取 AI 結果並建立 repo
./gradlew initBooks

# 顯示 queue 狀態
./gradlew initBooks -Pstatus=true

# 只處理指定書籍
./gradlew initBooks -Pid=<book-id>

# 將指定書籍的狀態重設為 pending
./gradlew initBooks -Pid=<book-id> -Preset=true

# 預覽模式（不實際建立 repo）
./gradlew initBooks -PdryRun=true
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-Pqueue` | - | Queue YAML 路徑（預設 `templates/books-queue.yaml`） |
| `-Pid` | - | 只處理指定書籍 ID |
| `-Pstatus` | - | 顯示 queue 狀態後結束 |
| `-Preset` | - | 搭配 `-Pid` 將該書狀態重設為 pending |
| `-PdryRun` | - | 預覽模式，不實際建立 repo |

## Queue 檔案

待辦書籍寫在 `templates/books-queue.yaml`，每筆有自己的 `status`
（pending / processing / completed / error），CLI 會自動往下推進。

詳細欄位與範例請參閱 [Queue YAML 格式]({{< relref "/docs/reference/yaml-input" >}})。

## 執行流程

### Phase 1: 產生 AI 任務

1. 從 queue 取下一本 pending 書（或 `-Pid` 指定的書）
2. 產生 `ai-tasks/input/batch-metadata-request.json`，內容含書名與目錄
3. 將該書狀態更新為 `processing`
4. 提示使用者請 Claude Code 處理

### Claude Code 處理

1. 讀取 input 任務檔
2. 依 `templates/prompts/book-metadata.txt` 產生 metadata + docs 結構
3. 寫入 `ai-tasks/output/batch-metadata-response.json`

### Phase 2: 完成建立

1. 讀取 AI 產生的 metadata 與結構
2. 從模板 repo 建立 GitHub repository
3. Clone 到本機工作目錄（依 category 分類）
4. 更新模板檔案（README、hugo.toml、go.mod、_index.md）
5. 下載並縮放封面圖片（500px 寬）
6. 建立 docs 資料夾結構
7. Commit + push 初始內容
8. 等 gh-pages branch 出現後啟用 GitHub Pages
9. 將該書狀態更新為 `completed`，清掉 ai-tasks 暫存

## 相關指令

- [check-env]({{< relref "/docs/commands/check-env" >}}) - 檢查環境
