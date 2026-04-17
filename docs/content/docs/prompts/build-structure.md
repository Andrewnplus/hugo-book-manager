---
title: build-structure
weight: 4
---

# build-structure

根據提供的中文目錄，建立 Hugo Book 資料夾結構和 `_index.md` 檔案。

## 用途

- 從書籍目錄建立對應的資料夾結構
- 將中文標題轉換為英文 kebab-case 資料夾名稱
- 自動建立含 frontmatter 的 `_index.md` 檔案

## 使用方式

```bash
# 產生 prompt
./gradlew generatePrompt -Ptype=build-structure

# 儲存到檔案
./gradlew generatePrompt -Ptype=build-structure -Poutput=PROMPT.md
```

## 完整 Prompt

以下是完整的 Prompt 內容，可直接複製使用：

---

{{% include-prompt "build-structure.txt" %}}

---

## 相關指令

- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生此 prompt
