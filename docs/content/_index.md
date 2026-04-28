---
title: Hugo Book Manager
type: docs
---

# Hugo Book Manager

自動建立與管理 Hugo Book 專案的 CLI 工具。

## 功能概覽

Hugo Book Manager 是一個 Kotlin + Gradle 建構的 CLI 工具，專門用於批次建立和管理使用 [Hugo Book](https://github.com/alex-shpak/hugo-book) 主題的書籍筆記網站。

### 主要功能

| 功能 | 說明 |
|------|------|
| **書籍 Queue 批次建立** | 從 `books-queue.yaml` 批次處理待辦書籍，建立 GitHub repo |
| **AI 互動工作流程** | 透過 Claude Code 互動式產生書籍 metadata 和目錄結構 |

### 設計理念

- **GitHub CLI 整合**：所有 GitHub 操作都透過 `gh` CLI 執行，無需 API Token
- **Claude Code 互動**：AI 處理採用兩階段互動工作流程，無需 API Key

## 快速開始

```bash
# 檢查環境
./gradlew checkEnv

# 從 queue 批次初始化書籍（AI 互動模式）
./gradlew initBooks
```

詳細說明請參閱 [安裝與設定]({{< relref "/docs/getting-started/installation" >}})。

## 文件結構

{{< columns >}}

### 入門指南
- [安裝與設定]({{< relref "/docs/getting-started/installation" >}})

<--->

### 指令文件
完整的 CLI 指令說明，包含所有參數和使用範例。

<--->

### AI 工作流程
了解 AI 互動式任務處理的運作方式。

{{< /columns >}}

## 原始碼

專案原始碼託管於 GitHub：[Andrewnplus/hugo-book-manager](https://github.com/Andrewnplus/hugo-book-manager)
