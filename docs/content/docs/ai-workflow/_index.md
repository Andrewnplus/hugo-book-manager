---
title: AI 工作流程
weight: 3
bookCollapseSection: true
---

# AI 工作流程

Hugo Book Manager 的部分指令使用 **兩階段 AI 互動工作流程**，透過 Claude Code 處理需要智慧判斷的任務。

## 使用 AI 的指令

| 指令 | AI 處理內容 |
|------|-------------|
| [init-book]({{< relref "/docs/commands/init-book" >}}) | 產生 metadata、docs 結構 |

## 設計理念

這個工作流程的設計理念是：

1. **不需要 API Key**：透過 Claude Code 互動處理，無需設定 API Token
2. **人機協作**：AI 產生建議，使用者可以檢視並確認
3. **任務明確**：每個任務都有清晰的輸入和預期輸出
4. **可追蹤**：任務檔案保存在 `ai-tasks/` 目錄，便於檢視和除錯

## 章節內容

- [運作方式]({{< relref "/docs/ai-workflow/how-it-works" >}}) - 兩階段工作流程詳解
- [任務類型]({{< relref "/docs/ai-workflow/task-types" >}}) - 各種 AI 任務的輸入輸出格式
