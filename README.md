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

### 設定

設定放在專案根目錄的 `local.properties`（已 gitignore）。環境變數可作為
fallback，但只有下表這幾個 key 會被讀取。

```properties
GITHUB_USERNAME=nplus-father
DEFAULT_WORK_DIR=/home/andrew/workspace/andrew/books-management/new-books
TEMPLATE_REPO=nplus-father/hugo-book-template
HOMEPAGE_BASE_URL=https://nplus.wiki
```

| Key | 必填 | 用途 |
|---|---|---|
| `GITHUB_USERNAME` | ✅ | 建立 repo 的 owner（可以是 org），也用來組 Pages URL |
| `DEFAULT_WORK_DIR` | ✅ | 新書 clone 的落地目錄。**這層是扁平的**（`<workDir>/<repoName>`）；三層分類只在書畢業搬進 `books-done/` 時才套用 |
| `TEMPLATE_REPO` | ✅ | 建新書用的模板 repo，格式 `owner/repo` |
| `HOMEPAGE_BASE_URL` | ✅ | repo homepage 的前綴，最終為 `{base}/{repoName}/` |
| `PORTAL_DIR` | — | nplus.wiki portal 的本機 clone，`refreshGoalProgress` 需要 |
| `NOTES_DIR` | — | Astro note 站所在的工作區，`refreshGoalProgress` 需要 |
| `BOOKS_DIR` | — | `books-done` 根目錄，`refreshGoalProgress` / `migrateTopicTiers` 需要 |

### 基本指令

```bash
# 檢查環境
./gradlew checkEnv

# 更新 owner 上既有 book repo 的快照（建議在 initBooks 前先跑）
./gradlew refreshRepoIndex

# 從 queue 批次初始化書籍專案（兩階段 AI 互動）
./gradlew initBooks                          # 處理下一本待辦
./gradlew initBooks -Pid=<book-id>           # 處理指定 ID
./gradlew initBooks -Pstatus=true            # 顯示 queue 狀態
./gradlew initBooks -Pid=<book-id> -Preset=true   # 重設為 pending

# 其他
./gradlew markRead -Prepo=<repo> [-Pchapter=<dir>]
./gradlew refreshGoalProgress
./gradlew migrateTopicTiers -Papply=true -PrepoName=<repo>   # 預設 dry-run
```

> Gradle 不吃裸旗標。`--status` / `--reset` 這類要寫成 `-Pstatus=true` / `-Preset=true`。

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
