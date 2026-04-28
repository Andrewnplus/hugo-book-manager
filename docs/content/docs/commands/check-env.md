---
title: check-env
weight: 1
---

# check-env

檢查環境是否符合 Hugo Book Manager 的執行需求。

## 使用方式

```bash
# Gradle Task
./gradlew checkEnv

# CLI 模式
./gradlew run --args="check-env"
```

## 檢查項目

| 項目 | 說明 |
|------|------|
| GitHub CLI | 確認 `gh` 指令已安裝 |
| GitHub 認證 | 確認已登入 GitHub |

## 輸出範例

### 成功

```
Checking environment...
✓ GitHub CLI installed
✓ GitHub CLI authenticated as: Andrewnplus
✓ All checks passed!
```

### 失敗

```
Checking environment...
✓ GitHub CLI installed
✗ GitHub CLI not authenticated
  Run 'gh auth login' to authenticate
```

## 相關指令

- [init-book]({{< relref "/docs/commands/init-book" >}}) - 需要 GitHub CLI 認證
