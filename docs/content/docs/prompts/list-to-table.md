---
title: list-to-table
weight: 8
---

# list-to-table

分析條列式內容，將適合的條列式轉換為 Markdown 表格。

## 用途

- 將鍵值對列表轉為表格
- 將比較列表轉為表格
- 提升資訊的組織性和可讀性

## 使用方式

```bash
# 產生 prompt
./gradlew generatePrompt -Ptype=list-to-table

# 儲存到檔案
./gradlew generatePrompt -Ptype=list-to-table -Poutput=PROMPT.md
```

## 完整 Prompt

以下是完整的 Prompt 內容，可直接複製使用：

---

{{% include-prompt "convert-list-to-table.txt" %}}

---

## 適合轉換的範例

| 原始格式 | 建議格式 |
|----------|----------|
| 鍵值對（名稱：說明） | 兩欄表格 |
| 比較列表（多項目、多屬性） | 多欄表格 |
| 步驟列表（序號 + 說明） | 三欄表格（階段、名稱、說明） |

## 相關指令

- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生此 prompt
