# Hugo Book Manager

Kotlin + Gradle 工具，自動建立和管理 Hugo Book 筆記專案的 GitHub Repositories。

## 功能特色

- **AI 互動式初始化** - 使用 Claude Code 生成書籍 metadata 和目錄結構
- **批次建立 Repositories** - 從 queue YAML 批次建立 GitHub repos

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

# 從 queue 批次初始化書籍專案（兩階段 AI 互動）
./gradlew initBooks                          # 處理下一本待辦
./gradlew initBooks -Pid=<book-id>           # 處理指定 ID
./gradlew initBooks -Pstatus=true            # 顯示 queue 狀態
```

## 詳細文件

完整的功能說明、指令參數和架構說明請參考 [CLAUDE.md](CLAUDE.md)。

## AI 任務處理

本工具使用 **Claude Code 互動式工作流程** 取代傳統 API 呼叫：

1. 執行 CLI 指令（Phase 1）→ 生成 AI 任務檔案
2. 告訴 Claude Code：「請處理 AI 任務」
3. 再次執行相同指令（Phase 2）→ 讀取 AI 結果並繼續

詳見 `templates/ai-task-guide.md`。

## 授權

MIT License
