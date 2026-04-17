---
title: book-structure
weight: 3
---

# book-structure

從書籍目錄文字產生 Hugo docs 資料夾結構。

## 用途

- 由 [init-book]({{< relref "/docs/commands/init-book" >}}) 和 [rebuild-docs]({{< relref "/docs/commands/rebuild-docs" >}}) 指令內部使用
- 產生一致的資料夾命名和結構

> [!NOTE]
> 此 Prompt 通常不需要手動使用，會由 AI 任務系統自動載入。

## 完整 Prompt

以下是完整的 Prompt 內容：

---

{{% include-prompt "book-structure.txt" %}}

---

## 輸入範例

```
第一部 基本原理
  第一章 原子習慣的驚人力量
  第二章 習慣如何塑造身分認同
第二部 讓習慣變得明顯
  第三章 培養習慣的最佳方式
```

## 輸出範例

```json
{
  "sections": [
    {
      "folderName": "01-fundamentals",
      "title": "第一部 基本原理",
      "weight": 1,
      "chapters": [
        {
          "folderName": "01-power-of-habits",
          "title": "第一章 原子習慣的驚人力量",
          "weight": 1
        },
        {
          "folderName": "02-identity",
          "title": "第二章 習慣如何塑造身分認同",
          "weight": 2
        }
      ]
    },
    {
      "folderName": "02-make-it-obvious",
      "title": "第二部 讓習慣變得明顯",
      "weight": 2,
      "chapters": [
        {
          "folderName": "01-habit-formation",
          "title": "第三章 培養習慣的最佳方式",
          "weight": 1
        }
      ]
    }
  ]
}
```

## 產生的結構

```
site/content/docs/
├── 01-fundamentals/
│   ├── _index.md
│   ├── 01-power-of-habits/
│   │   └── _index.md
│   └── 02-identity/
│       └── _index.md
└── 02-make-it-obvious/
    ├── _index.md
    └── 01-habit-formation/
        └── _index.md
```

## 相關文件

- [init-book]({{< relref "/docs/commands/init-book" >}}) - 使用此 prompt 的指令
- [rebuild-docs]({{< relref "/docs/commands/rebuild-docs" >}}) - 使用此 prompt 的指令
- [任務類型]({{< relref "/docs/ai-workflow/task-types" >}}) - AI 任務格式說明
