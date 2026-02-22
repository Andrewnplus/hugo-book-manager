# Prompt Templates

Hugo Book Manager 的 prompt 模板集合，用於書籍專案的各階段維護任務。

## 使用方式

```bash
# 列出所有可用 prompt
./gradlew generatePrompt --list

# 顯示指定 prompt 內容
./gradlew generatePrompt -Ptype=<type>

# 輸出到檔案
./gradlew generatePrompt -Ptype=<type> -Poutput=/path/to/PROMPT.md
```

產生的 prompt 可以直接貼到目標書籍專案的 Claude Code 中使用。

---

## Prompt 總覽

### 內部自動使用（init-book 流程）

| Type | 檔案 | 說明 |
|------|------|------|
| `book-metadata` | book-metadata.txt | 從書名產生 repo metadata（名稱、描述、topics、分類） |
| `book-structure` | book-structure.txt | 從目錄產生 Hugo docs 資料夾結構 |

> 這兩個 prompt 由 `init-book` / `init-books` 指令自動呼叫，通常不需要手動使用。

### 內容建立與結構

| Type | 檔案 | 說明 | 使用時機 |
|------|------|------|----------|
| `build-structure` | build-structure.txt | 根據目錄建立 Hugo Book 資料夾和 `_index.md` | 新書建立後，需要手動建立章節結構時 |
| `add-books-to-queue` | add-books-to-queue.txt | 將新書籍資訊加入 `books-queue.yaml` | 準備批次建立書籍時 |

### 內容增強

| Type | 檔案 | 說明 | 使用時機 |
|------|------|------|----------|
| `enhance-katex` | enhance-markdown-katex.txt | 將數學內容轉換為 KaTeX 公式格式 | 書籍包含數學公式時 |
| `enhance-mermaid` | enhance-markdown-mermaid.txt | 將流程、關係等內容轉換為 Mermaid 圖表 | 書籍包含流程圖、架構圖時 |
| `extract-pdf-figures` | extract-pdf-figures.txt | 從 PDF 擷取圖表並轉換為 Markdown | 有原始 PDF 需要提取圖片時 |

### 內容編輯與精煉

| Type | 檔案 | 說明 | 使用時機 |
|------|------|------|----------|
| `review-markdown` | review-markdown.txt | 審查並統一所有 `_index.md` 的格式風格 | 品質檢查，確保全書格式一致 |
| `rewrite-content` | rewrite-content.txt | 將既有文案改寫為結構嚴謹、易讀的格式 | 既有內容需要重新組織時 |
| `refine-notes` | refine-notes.txt | 改善讀書筆記的結構與可讀性 | 整合多來源筆記時 |
| `translate-content` | translate-content.txt | 將英文筆記翻譯為繁體中文 | 來源為英文需要翻譯時 |

### 內容轉換

| Type | 檔案 | 說明 | 使用時機 |
|------|------|------|----------|
| `list-to-table` | convert-list-to-table.txt | 將適合的條列式內容轉換為 Markdown 表格 | 條列內容用表格呈現更清楚時 |
| `simplify-table` | simplify-table.txt | 移除表格中的冗餘資訊，只保留核心內容 | 表格包含不必要欄位時 |
| `split-long-chapter` | split-long-chapter.txt | 將過長的 `_index.md` 拆分為子章節 | 單一章節超過建議長度時 |

### 內容分析與知識萃取

| Type | 檔案 | 說明 | 使用時機 |
|------|------|------|----------|
| `extract-insights` | extract-insights.txt | 從書籍筆記中萃取值得收藏的精華句子 | 建立「重點摘錄」章節時 |
| `generate-summary` | generate-summary.txt | 為每個章節生成導讀摘要 | 幫助讀者快速了解章節範圍 |
| `generate-glossary` | generate-glossary.txt | 從全書掃描專業術語並產出詞彙表 | 全書內容定稿後，幫助讀者理解專業術語 |
| `check-links` | check-links.txt | 檢查內部連結、圖片路徑、外部 URL | 發布前的品質檢查 |

---

## 建議工作流程

```
1. init-book / init-books     → 自動使用 book-metadata + book-structure
2. review-markdown             → 統一格式
3. extract-pdf-figures         → 提取 PDF 圖片（如適用）
4. enhance-katex / mermaid     → 增強數學公式與圖表
5. rewrite-content / refine    → 改寫或精煉內容
6. translate-content           → 翻譯（如適用）
7. generate-summary            → 生成章節摘要
8. generate-glossary           → 產出詞彙表
9. check-links                 → 最終連結檢查
```
