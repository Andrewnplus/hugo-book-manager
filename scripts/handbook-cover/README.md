# Handbook Cover Generator

產生 **統一風格的 Handbook 書封**（Style A・深色編輯風・3:4 直式，900×1200 PNG）。

同一套模板保證家族感，每本只換：**標題 + 主色 + motif**。渲染純程式化（HTML → headless Chrome → PNG），無 AICG，可重現、易維護。

## 設計規格

- 尺寸 **900×1200（3:4 直式書封）**，`object-cover` 同比例不裁切；縮圖 ~210px 仍清晰
- 字體 Noto Sans CJK TC（標題 Black 900）
- 每本一個主色 + 專屬 motif + 底部主色漸層線（系列統一元素）
- 角落系列標 `NPLUS · NN`、`HANDBOOK · nplus.wiki`

## 需求

`node`、`google-chrome`、`ImageMagick(convert)`、`Noto Sans CJK TC` 字型。

## 用法

```bash
# 單一封面（輸出 ./<key>.png）
./render.sh algorithms-data-structures

# 全部渲染並安裝到各 repo 的 site/content/cover.png
./render.sh --install ~/workspace/andrew/handbooks
```

安裝後記得把各 repo README 的封面寬度設為直式，例如：
`<img src="site/content/cover.png" width="280" alt="Book Cover">`

## 新增 / 修改一本

編輯 `cover-gen.mjs`：

- `BOOKS`：加一筆 `{ title, en, index, accent, accent2, motif }`（key = repo 資料夾名）
- `MOTIFS`：若需要新圖示，加一個 `key: (c) => '<svg …>'`（viewBox `0 0 400 400`，用 `${c}` 上色）

標題字級依字數自動縮放（`titleSize`）。
