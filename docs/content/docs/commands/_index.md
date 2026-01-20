---
title: 指令文件
weight: 2
bookCollapseSection: true
---

# 指令文件

Hugo Book Manager 提供多個 CLI 指令，涵蓋從環境檢查到批次處理的各種功能。

## 指令列表

| 指令 | 說明 | AI 互動 |
|------|------|:-------:|
| [check-env]({{< relref "/docs/commands/check-env" >}}) | 檢查環境設定 | - |
| [create-repos]({{< relref "/docs/commands/create-repos" >}}) | 從 CSV 批次建立 GitHub repo | - |
| [init-book]({{< relref "/docs/commands/init-book" >}}) | 初始化新書籍專案 | ✓ |
| [clean-docs]({{< relref "/docs/commands/clean-docs" >}}) | 清理 PDF/HTML/MHTML 為 Markdown | - |
| [convert-docs]({{< relref "/docs/commands/convert-docs" >}}) | 轉換 Markdown 為 Hugo Book 格式 | ✓ |
| [rebuild-docs]({{< relref "/docs/commands/rebuild-docs" >}}) | 重建 docs 資料夾結構 | ✓ |
| [update-renovate]({{< relref "/docs/commands/update-renovate" >}}) | 批次更新 renovate.json | - |
| [merge-prs]({{< relref "/docs/commands/merge-prs" >}}) | 批次合併 Renovate PR | - |
| [generate-prompt]({{< relref "/docs/commands/generate-prompt" >}}) | 產生 Prompt 範本 | - |

## 使用方式

每個指令都支援兩種呼叫方式：

### Gradle Tasks（建議）

```bash
./gradlew <task-name> [-P參數名=參數值]
```

### CLI 模式

```bash
./gradlew run --args="<command> [options]"
```

## 共通選項

大部分指令支援以下共通選項：

| 選項 | 說明 |
|------|------|
| `--dry-run` / `-PdryRun=true` | 預覽模式，不實際執行 |
| `--help` | 顯示指令說明 |
