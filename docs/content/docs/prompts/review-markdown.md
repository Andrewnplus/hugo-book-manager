---
title: review-markdown
weight: 7
---

# review-markdown

審查 Markdown 檔案，確保格式一致並修正常見問題。

## 用途

- **主動掃描並轉換 Hugo Book hint shortcodes**
- 統一標題層級結構
- 統一列表符號（使用 `-`）
- 確保程式碼區塊有語言標記

## 使用方式

```bash
# 產生 prompt
./gradlew generatePrompt -Ptype=review-markdown

# 儲存到檔案
./gradlew generatePrompt -Ptype=review-markdown -Poutput=PROMPT.md
```

## 完整 Prompt

以下是完整的 Prompt 內容，可直接複製使用：

---

{{% include-prompt "review-markdown.txt" %}}

---

## 常見修正範例

### 標題跳級

```markdown
# 錯誤
## 標題
#### 小標題  ← 跳過了 h3

# 正確
## 標題
### 小標題
```

### 列表符號

```markdown
# 錯誤
* 項目一
- 項目二
+ 項目三

# 正確
- 項目一
- 項目二
- 項目三
```

### Hint Shortcode 轉換

```markdown
# 舊格式（錯誤）
{{</* hint info */>}}
這是提示內容
{{</* /hint */>}}

# 新格式（正確）
> [!NOTE]
> 這是補充說明
```

## 相關指令

- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生此 prompt
