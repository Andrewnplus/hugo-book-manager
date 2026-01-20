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

````markdown
# 任務：審查並統一 Hugo Book Markdown 格式

## 角色

你現在是一位專業的知識管理與數位內容編輯，專精於確保文件格式的一致性與品質。你的目標是審查 Hugo Book 專案中所有 Markdown 檔案，確保它們遵循統一的格式規範。

## 目標

遍歷 `site/content/docs/` 下所有的 `_index.md` 檔案，審查並修正格式問題，確保所有檔案遵循一致的風格規範。

## 執行步驟

1. **掃描檔案**
   - 找出 `site/content/docs/` 下所有的 `_index.md` 檔案
   - 列出待審查的檔案清單

2. **優先檢查 Hint Shortcodes**
   - 使用 grep 搜尋所有包含 `{{</* hint` 的檔案
   - 這是最重要的轉換項目，必須優先處理
   - 將所有 hint shortcodes 轉換為 Markdown Alert 格式

3. **逐一審查**
   - 讀取每個 `_index.md` 的內容
   - 依照下方規範檢查格式問題
   - 列出需要修正的項目

4. **執行修正**
   - 確認要修正的項目後執行
   - 保留原有的 frontmatter（`---` 區塊）
   - 修正 frontmatter 之後的內容格式

5. **完成報告**
   - 列出已處理的檔案
   - 標註任何需要人工檢視的問題

## 統一風格規範

### 標題結構

1. **標題層級**
   - 善用二級標題 (`##`) 與三級標題 (`###`) 來區分段落
   - 建立清晰的層級關係
   - 一級標題 (`#`) 由 frontmatter 的 title 決定，內文避免使用
   - 不可跳級（h2 → h3 → h4，不能 h2 直接到 h4）

2. **標題格式**
   - 標題前後各留一行空白
   - 標題後不加標點符號

### 列表格式

1. **統一使用 `-` 作為無序列表符號**
   ```markdown
   # 正確
   - 項目一
   - 項目二
   - 項目三

   # 錯誤
   * 項目一
   - 項目二
   + 項目三
   ```

2. **巢狀列表適當縮排**（2 或 4 個空格）

3. **有序列表使用 `1.`**

### Hugo Book Hint Shortcode 轉換（重要！）

**必須主動掃描並轉換所有 Hugo Book 的 hint shortcodes！**

| 舊格式 (hint shortcode) | 新格式 (Markdown Alert) |
|------------------------|------------------------|
| `{{</* hint info */>}}...{{</* /hint */>}}` | `> [!NOTE]` 或 `> [!TIP]` |
| `{{</* hint warning */>}}...{{</* /hint */>}}` | `> [!WARNING]` |
| `{{</* hint danger */>}}...{{</* /hint */>}}` | `> [!CAUTION]` |

### Alert 格式（重點摘要、警示）

請完全捨棄舊有的 hint shortcodes，改用標準 Markdown Alert 格式：

1. **NOTE** - 補充說明、背景知識
2. **TIP** - 實用技巧、建議做法
3. **IMPORTANT** - 核心觀點、重點摘要
4. **WARNING** - 常見錯誤、需要注意的陷阱
5. **CAUTION** - 嚴重警告、不可忽視的風險

### 程式碼區塊

- 必須指定語言標記（如 ```python, ```javascript, ```bash）
- 保持程式碼格式完整

### 其他格式規範

1. **粗體與斜體**
   - 粗體使用 `**文字**`
   - 斜體使用 `*文字*`
   - 不使用底線格式（`__` 或 `_`）

2. **段落間距**
   - 段落之間使用一行空白分隔
   - 不使用多餘的空行

## 常見問題檢查清單

### 結構問題
- [ ] 標題層級是否正確（無跳級）
- [ ] 每個檔案是否有適當的 frontmatter
- [ ] 章節劃分是否清晰

### 格式問題
- [ ] 列表符號是否統一使用 `-`
- [ ] 程式碼區塊是否有指定語言
- [ ] Alert 格式是否正確
- [ ] 是否有舊的 hint shortcode 需要轉換

### 內容問題
- [ ] 是否有重複內容
- [ ] 是否有未完成的段落（TODO、待補充）
- [ ] 圖片是否有 alt 文字
- [ ] 連結是否有效

## 開始執行

請掃描 `site/content/docs/` 資料夾，找出所有 `_index.md` 檔案，逐一審查並列出需要修正的問題，確認後執行修正。
````

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
