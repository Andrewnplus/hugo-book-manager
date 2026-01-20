---
title: merge-prs
weight: 8
---

# merge-prs

批次合併通過 CI 的 Renovate Pull Request。

## 使用方式

```bash
# 基本使用
./gradlew mergePrs -PparentDir=/path/to/books

# 指定合併方式
./gradlew mergePrs -PparentDir=/path/to/books -PmergeMethod=squash

# CLI 模式
./gradlew run --args="merge-prs --parent-dir /path/to/books"
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-PparentDir` | ✓ | 包含多個 git repo 的父目錄 |
| `-PmergeMethod` | - | 合併方式：`merge`（預設）、`squash`、`rebase` |

## 執行流程

針對父目錄下的每個 git 儲存庫：

1. 尋找開啟的 Renovate PR
2. 檢查 CI 狀態是否通過
3. 如果通過，使用指定方式合併

## 合併方式

| 方式 | 說明 |
|------|------|
| `merge` | 建立 merge commit（預設） |
| `squash` | 將所有 commit 壓縮為一個 |
| `rebase` | 將 commit rebase 到 main |

## 輸出範例

```
Merging Renovate PRs in: /path/to/books

Found 15 git repositories

[1/15] Checking: atomic-habits
  Found 2 open Renovate PRs
    PR #123: Update hugo-book to v0.8.0
      ✓ CI passed
      ✓ Merged
    PR #124: Update actions/checkout to v4
      ✗ CI pending, skipping

[2/15] Checking: deep-work
  No open Renovate PRs

[3/15] Checking: thinking-fast-and-slow
  Found 1 open Renovate PR
    PR #45: Update hugo-book to v0.8.0
      ✓ CI passed
      ✓ Merged

...

Summary:
  Merged: 8 PRs
  Skipped (CI not passed): 3 PRs
  No PRs: 4 repos
```

## 相關指令

- [update-renovate]({{< relref "/docs/commands/update-renovate" >}}) - 更新 renovate.json 設定
