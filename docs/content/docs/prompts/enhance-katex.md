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

{{% include-prompt "enhance-markdown-katex.txt" %}}

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
