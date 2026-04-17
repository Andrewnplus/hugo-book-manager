---
title: enhance-mermaid
weight: 6
---

# enhance-mermaid

掃描 Markdown 檔案，將適合以圖表呈現的內容轉換為 Mermaid 圖表。

## 用途

- 將文字描述的流程轉為流程圖
- 將概念層級轉為心智圖
- 將互動流程轉為序列圖
- 增加內容的視覺化呈現

## 使用方式

```bash
# 產生 prompt
./gradlew generatePrompt -Ptype=enhance-mermaid

# 儲存到檔案
./gradlew generatePrompt -Ptype=enhance-mermaid -Poutput=PROMPT.md
```

## 完整 Prompt

以下是完整的 Prompt 內容，可直接複製使用：

---

{{% include-prompt "enhance-markdown-mermaid.txt" %}}

---

## 適合圖表化的內容

| 內容類型 | 建議圖表 |
|----------|----------|
| 步驟流程 | 流程圖 (flowchart) |
| 對話互動 | 序列圖 (sequenceDiagram) |
| 概念層級 | 心智圖 (mindmap) |
| 時間線 | 時間軸 (timeline) |
| 物件關係 | 類別圖 (classDiagram) |

## 相關指令

- [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) - 產生此 prompt
