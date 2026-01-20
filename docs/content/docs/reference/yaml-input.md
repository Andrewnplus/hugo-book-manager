---
title: YAML 輸入
weight: 2
---

# YAML 輸入格式

[init-book]({{< relref "/docs/commands/init-book" >}}) 指令使用 YAML 檔案作為輸入，定義要初始化的書籍資訊。

## 欄位說明

| 欄位 | 必填 | 說明 |
|------|:----:|------|
| `chinese_title` | ✓ | 中文書名 |
| `english_title` | ✓ | 英文書名 |
| `author` | ✓ | 作者名稱 |
| `publication_date` | - | 出版日期（YYYY-MM-DD） |
| `cover_url` | - | 書封面圖片網址 |
| `purchase_url` | - | 購書連結 |
| `table_of_contents` | ✓ | 目錄文字（用於 AI 產生 docs 結構） |

## 範例

```yaml
# Book Input Template
# 填寫此檔案後執行: ./gradlew initBook -Pinput=book-input.yaml

# === 明確資訊（必填）===
chinese_title: "原子習慣"
english_title: "Atomic Habits"
author: "James Clear"
publication_date: "2019-06-01"

# 書封面圖片網址（會自動下載並縮放為 500px 寬）
cover_url: "https://example.com/atomic-habits-cover.jpg"

# 購書網址
purchase_url: "https://www.amazon.com/dp/B07D23CFGR"

# === AI 處理的內容 ===
# 書目錄文字（中文居多），AI 會根據這個生成 docs 資料夾結構
table_of_contents: |
  導讀 為什麼原子習慣比你想像的更有用

  第一部 基本原理
    第一章 原子習慣的驚人力量
    第二章 習慣如何塑造你的身分認同
    第三章 建立更好習慣的四個簡單步驟

  第二部 讓習慣變得明顯
    第四章 培養習慣的最佳方式
    第五章 啟動習慣的最佳方式
    第六章 改變環境的科學
```

## 欄位格式說明

### table_of_contents

目錄文字是 AI 產生 docs 結構的依據。格式建議：

1. **使用縮排表示層級**
   - 頂層：「部」（Part）或頂級章節
   - 縮排：章節或子章節

2. **保留原始中文**
   - AI 會保留中文標題
   - AI 會自動將標題翻譯為英文 kebab-case 作為資料夾名稱

3. **常見格式**
   ```
   第一部 部標題
     第一章 章標題
     第二章 章標題
   第二部 部標題
     第三章 章標題
   ```

   或（無「部」的書）：
   ```
   第一章 章標題
   第二章 章標題
   第三章 章標題
   ```

### cover_url

- 提供書封面圖片的直接連結
- 系統會自動下載並縮放為 500px 寬
- 支援常見圖片格式（JPG、PNG、WebP）

### publication_date

- 使用 ISO 格式：`YYYY-MM-DD`
- 範例：`2019-06-01`

## 範本檔案

專案中提供範本檔案：

```
templates/book-input.yaml
```

複製此檔案並修改後使用：

```bash
cp templates/book-input.yaml my-book.yaml
# 編輯 my-book.yaml
./gradlew initBook -Pinput=my-book.yaml
```

## AI 處理

init-book 使用兩階段 AI 工作流程：

1. **Phase 1**：CLI 讀取 YAML，產生 AI 任務
2. **Claude 處理**：產生 metadata 和 docs 結構
3. **Phase 2**：CLI 讀取 AI 結果，完成建立

詳見 [AI 工作流程]({{< relref "/docs/ai-workflow" >}})。

## 相關指令

- [init-book]({{< relref "/docs/commands/init-book" >}}) - 使用此格式的指令
