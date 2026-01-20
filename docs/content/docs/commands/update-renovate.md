---
title: update-renovate
weight: 7
---

# update-renovate

批次更新多個專案的 renovate.json 設定檔。

## 使用方式

```bash
# 預覽模式
./gradlew updateRenovate -PparentDir=/path/to/books -PdryRun=true

# 實際執行
./gradlew updateRenovate -PparentDir=/path/to/books

# CLI 模式
./gradlew run --args="update-renovate --parent-dir /path/to/books --dry-run"
```

## 參數

| 參數 | 必填 | 說明 |
|------|:----:|------|
| `-PparentDir` | ✓ | 包含多個 git repo 的父目錄 |
| `-PdryRun` | - | 預覽模式 |

## 執行流程

針對父目錄下的每個 git 儲存庫：

1. `git pull` 取得最新版本
2. 更新 `.github/renovate.json` 為標準設定
3. `git commit` 提交變更
4. `git push` 推送到遠端

## 標準 Renovate 設定

更新後的 renovate.json 內容：

```json
{
  "$schema": "https://docs.renovatebot.com/renovate-schema.json",
  "extends": [
    "config:recommended"
  ],
  "schedule": [
    "before 9am on monday"
  ],
  "timezone": "Asia/Taipei",
  "prHourlyLimit": 0,
  "prConcurrentLimit": 0
}
```

## 輸出範例

```
Updating renovate.json in: /path/to/books

Found 15 git repositories

[1/15] Updating: atomic-habits
  ✓ Pulled latest
  ✓ Updated renovate.json
  ✓ Committed and pushed

[2/15] Updating: deep-work
  ✓ Pulled latest
  ✓ Updated renovate.json
  ✓ Committed and pushed

[3/15] Updating: thinking-fast-and-slow
  - renovate.json already up to date, skipping

...

Done! Updated 12 repositories, 3 already up to date.
```

## 相關指令

- [merge-prs]({{< relref "/docs/commands/merge-prs" >}}) - 批次合併 Renovate PR
