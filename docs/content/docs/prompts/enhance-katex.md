---
title: enhance-katex
weight: 5
---

# enhance-katex

掃描 Markdown 檔案，將數學公式相關內容轉換為 KaTeX 格式。

## 用途

- 為書籍內容加入數學公式支援
- 將純文字數學表達式轉為 LaTeX 語法
- 適用於數學、物理、統計等書籍

## 使用方式

```bash
# 產生 prompt
./gradlew generatePrompt -Ptype=enhance-katex

# 儲存到檔案
./gradlew generatePrompt -Ptype=enhance-katex -Poutput=PROMPT.md
```

## 完整 Prompt

以下是完整的 Prompt 內容，可直接複製使用：

---

````markdown
# 任務：Markdown KaTeX 數學公式增強

## 目標
掃描 Hugo Book 專案中的 Markdown 檔案，將數學公式相關內容轉換為 KaTeX 格式。

## KaTeX 語法說明

### 行內公式
使用 `$...$` 包裹：
```markdown
愛因斯坦的質能方程式 $E = mc^2$ 說明了...
```

### 區塊公式
使用 `$$...$$` 包裹：
```markdown
二次方程式的求根公式：

$$
x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}
$$
```

## 執行步驟

1. **掃描檔案**
   - 找到 `site/content/` 下所有 `.md` 檔案
   - 識別包含數學內容的段落

2. **轉換內容**
   - 將純文字數學表達式轉為 KaTeX 語法
   - 常見轉換：
     - `x^2` → `$x^2$`
     - `a/b` → `$\frac{a}{b}$`
     - `sqrt(x)` → `$\sqrt{x}$`
     - `sum` → `$\sum$`
     - `integral` → `$\int$`

3. **常用 KaTeX 符號**
   ```
   上標：x^2, x^{10}
   下標：x_1, x_{ij}
   分數：\frac{a}{b}
   根號：\sqrt{x}, \sqrt[n]{x}
   求和：\sum_{i=1}^{n}
   積分：\int_{a}^{b}
   極限：\lim_{x \to \infty}
   希臘字母：\alpha, \beta, \gamma, \theta, \pi
   箭頭：\rightarrow, \leftarrow, \Rightarrow
   運算符：\times, \div, \pm, \cdot
   比較：\leq, \geq, \neq, \approx
   集合：\in, \notin, \subset, \cup, \cap
   ```

## Hugo 設定確認

確保 `site/hugo.toml` 或 `site/config.toml` 中有啟用 KaTeX：
```toml
[params]
  BookToC = true

[markup.goldmark.extensions]
  passthrough = true  # 允許 KaTeX 語法通過

[markup.goldmark.extensions.passthrough.delimiters]
  block = [['\[', '\]'], ['$$', '$$']]
  inline = [['\(', '\)'], ['$', '$']]
```

## 開始執行

請先掃描專案，列出需要轉換的檔案和識別到的數學內容，讓我確認後再執行轉換。
````

---

## KaTeX 常用語法速查

| 語法 | 顯示 | 說明 |
|------|------|------|
| `$x^2$` | x² | 上標 |
| `$x_1$` | x₁ | 下標 |
| `$\frac{a}{b}$` | a/b | 分數 |
| `$\sqrt{x}$` | √x | 根號 |
| `$\sum_{i=1}^{n}$` | Σ | 求和 |
| `$\int_{a}^{b}$` | ∫ | 積分 |
| `$\alpha, \beta$` | α, β | 希臘字母 |
| `$\rightarrow$` | → | 箭頭 |

## 相關指令

- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生此 prompt
