# Hugo Book Manager

Kotlin + Gradle 工具，自動建立和管理 Hugo Book 筆記專案的 GitHub Repositories。

## 功能特色

- **批次建立 Repositories** - 從 CSV 檔案批次建立 GitHub repos
- **AI 互動式初始化** - 使用 Claude Code 生成書籍 metadata 和目錄結構
- **文件清理** - 將 PDF/HTML/MHTML 轉換為乾淨的 Markdown
- **文件轉換** - 使用 AI 將 Markdown 轉換為 Hugo-book 格式
- **重建文件結構** - 根據目錄重新生成 Hugo docs 資料夾
- **Prompt 生成** - 生成可複製貼上的 Prompt 用於書籍專案維護
- **批次更新 Renovate** - 統一更新多個 repos 的 renovate.json
- **批次合併 PRs** - 自動合併通過 CI 的 Renovate PRs

## 快速開始

### 前置需求

```bash
# 檢查 GitHub CLI
gh --version
gh auth status

# 如需登入
gh auth login
```

### 基本指令

```bash
# 檢查環境
./gradlew checkEnv

# 列出可用的 Prompt 範本
./gradlew generatePrompt

# 初始化新書籍專案（兩階段 AI 互動）
./gradlew initBook -Pinput=templates/book-input.yaml

# 清理 HTML 文件為 Markdown
./gradlew cleanDocs -PinputDir=/path/to/html

# 轉換 Markdown 為 Hugo-book 格式（兩階段 AI 互動）
./gradlew convertDocs -PinputDir=/path/to/md
```

## 詳細文件

完整的功能說明、指令參數和架構說明請參考 [CLAUDE.md](CLAUDE.md)。

## AI 任務處理

本工具使用 **Claude Code 互動式工作流程** 取代傳統 API 呼叫：

1. 執行 CLI 指令（Phase 1）→ 生成 AI 任務檔案
2. 告訴 Claude Code：「請處理 AI 任務」
3. 再次執行相同指令（Phase 2）→ 讀取 AI 結果並繼續

詳見 `templates/ai-task-guide.md`。

## Prompt 生成功能

為現有書籍專案生成維護用的 Prompt：

```bash
# 列出所有 Prompt 類型
./gradlew generatePrompt

# 生成特定 Prompt
./gradlew generatePrompt -Ptype=restructure-folders    # 重命名資料夾
./gradlew generatePrompt -Ptype=enhance-katex          # KaTeX 數學公式
./gradlew generatePrompt -Ptype=enhance-mermaid        # Mermaid 圖表
./gradlew generatePrompt -Ptype=review-markdown        # Markdown 審查

# 儲存到檔案
./gradlew generatePrompt -Ptype=restructure-folders -Poutput=PROMPT.md
```

## 授權

MIT License
