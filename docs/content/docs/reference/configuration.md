---
title: 設定檔
weight: 3
---

# 設定檔

Hugo Book Manager 使用 `local.properties` 檔案儲存設定。

## 設定檔位置

```
hugo-book-manager/
└── local.properties    # 此檔案已加入 .gitignore
```

## 設定項目

```properties
GITHUB_USERNAME=你的GitHub帳號
DEFAULT_WORK_DIR=/path/to/your/books
TEMPLATE_REPO=你的帳號/hugo-book-template
HOMEPAGE_BASE_URL=https://你的domain.com
```

| 設定項目 | 說明 | 範例 |
|----------|------|------|
| `GITHUB_USERNAME` | GitHub 帳號名稱 | `Andrewnplus` |
| `DEFAULT_WORK_DIR` | 書籍專案存放目錄 | `/home/andrew/books` |
| `TEMPLATE_REPO` | Hugo Book 模板 repo | `Andrewnplus/hugo-book-template` |
| `HOMEPAGE_BASE_URL` | GitHub Pages base URL | `https://nplus.wiki` |

## 詳細說明

### GITHUB_USERNAME

你的 GitHub 帳號名稱，用於：

- 建立儲存庫時的擁有者
- 設定 GitHub Pages URL

### DEFAULT_WORK_DIR

本機的書籍專案存放目錄。當使用 [init-book]({{< relref "/docs/commands/init-book" >}}) 時，新書籍會 clone 到此目錄下，依 category 分類：

```
DEFAULT_WORK_DIR/
├── growth-book-summary/
│   ├── atomic-habits/
│   ├── deep-work/
│   └── ...
├── business-book-summary/
│   ├── zero-to-one/
│   └── ...
└── tech-book-summary/
    └── ...
```

### TEMPLATE_REPO

Hugo Book 模板儲存庫，格式為 `owner/repo`。

建立新書籍時會從此模板複製。模板應包含：

- Hugo Book 主題設定
- 基本的資料夾結構
- 範本檔案（README.md、hugo.toml 等）

### HOMEPAGE_BASE_URL

GitHub Pages 的 base URL，用於設定儲存庫的 homepage。

最終 URL 格式：`{HOMEPAGE_BASE_URL}/{repo_name}/`

例如：`https://nplus.wiki/atomic-habits/`

## 建立設定檔

```bash
# 在專案根目錄建立設定檔
cat > local.properties << 'EOF'
GITHUB_USERNAME=你的GitHub帳號
DEFAULT_WORK_DIR=/path/to/your/books
TEMPLATE_REPO=你的帳號/hugo-book-template
HOMEPAGE_BASE_URL=https://你的domain.com
EOF
```

## 驗證設定

使用 [check-env]({{< relref "/docs/commands/check-env" >}}) 確認環境設定：

```bash
./gradlew checkEnv
```

## 注意事項

- `local.properties` 已加入 `.gitignore`，不會被提交到版本控制
- 如果缺少設定檔，部分指令可能無法正常運作
- 確保 `DEFAULT_WORK_DIR` 目錄存在且有寫入權限
