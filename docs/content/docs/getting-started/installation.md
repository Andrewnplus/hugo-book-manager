---
title: 安裝與設定
weight: 1
---

# 安裝與設定

## 環境需求

Hugo Book Manager 需要以下環境：

| 工具 | 版本需求 | 用途 |
|------|----------|------|
| **Java** | 17+ | 執行環境 |
| **Kotlin** | 1.9+ | 核心語言 |
| **Gradle** | 8+ | 建構系統 |
| **GitHub CLI (`gh`)** | 最新版 | GitHub 操作 |

## 安裝步驟

### 1. 複製專案

```bash
git clone https://github.com/Andrewnplus/hugo-book-manager.git
cd hugo-book-manager
```

### 2. 確認 GitHub CLI 已安裝

```bash
# 檢查 GitHub CLI 版本
gh --version

# 如果尚未安裝，請參考官方文件：
# https://cli.github.com/manual/installation
```

### 3. 登入 GitHub

```bash
# 檢查登入狀態
gh auth status

# 如果尚未登入
gh auth login
```

### 4. 建立設定檔

在專案根目錄建立 `local.properties`：

```properties
GITHUB_USERNAME=你的GitHub帳號
DEFAULT_WORK_DIR=/path/to/your/books
TEMPLATE_REPO=你的帳號/hugo-book-template
HOMEPAGE_BASE_URL=https://你的domain.com
```

| 設定項目 | 說明 |
|----------|------|
| `GITHUB_USERNAME` | 你的 GitHub 帳號名稱 |
| `DEFAULT_WORK_DIR` | 書籍專案的預設存放目錄 |
| `TEMPLATE_REPO` | Hugo Book 模板 repo（格式：`owner/repo`） |
| `HOMEPAGE_BASE_URL` | GitHub Pages 的 base URL |

### 5. 驗證安裝

```bash
./gradlew checkEnv
```

如果一切正常，你會看到：

```
✓ GitHub CLI installed
✓ GitHub CLI authenticated as: 你的帳號
✓ All checks passed!
```

## 下一步

環境設定完成後，你可以：

1. [建立 Repo]({{< relref "/docs/commands/create-repos" >}}) - 從 CSV 批次建立書籍 repo
2. [初始化書籍]({{< relref "/docs/commands/init-book" >}}) - 使用 AI 互動式建立新書籍
3. [了解 AI 工作流程]({{< relref "/docs/ai-workflow" >}}) - 學習 AI 任務處理機制

## 相關依賴

專案使用以下主要依賴：

| 依賴 | 用途 |
|------|------|
| **kotlinx-serialization** | JSON 解析 |
| **kotlin-csv** | CSV 檔案讀取 |
| **Clikt** | CLI 框架 |
| **Jsoup** | HTML 解析（文件清理） |
| **Apache PDFBox** | PDF 文字擷取 |
