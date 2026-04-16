# Plugin Migration TODO

一勞永逸解決「662 個 book repo 的 Hugo 版本升級不同步」問題的任務清單。

## 背景

- **問題**：每個 book repo 的 `build.gradle.kts` 有 35 行樣板，包含 Hugo binary 版本字串。Renovate 看不懂這行（不是標準 Gradle 依賴），所以從來不幫你升級 → 564/662 repo 卡在 Hugo `0.154.5`，theme 不相容就炸掉。
- **解法**：把樣板抽成中央 Gradle convention plugin（`com.andrewnplus.book`），每個 book repo 的 `build.gradle.kts` 縮成 3 行。Hugo 版本在 plugin 裡，Renovate 更新 plugin → 下游自動跟上。同時用 `Andrewnplus/renovate-config` 統一 Renovate 規則。
- **發佈管道**：~~Gradle Plugin Portal~~ → **GitHub Packages**（Portal 審核被拒：group ID 不符 + 被判定 trivial）

## 已完成

### Phase 0：Plugin 開發（2026-04-11）

- [x] `Andrewnplus/book-gradle-conventions` repo 建立、程式碼完成、push 到 GitHub
  - Plugin ID：`com.andrewnplus.book`
  - 內建 Hugo `0.160.1`、Spotless `8.4.0`、stage task、hugoBuild → spotlessCheck 依賴
  - 自帶 Renovate customManager：`HUGO_VERSION` 常數旁的 `// renovate:` comment，auto-bump
- [x] `Andrewnplus/renovate-config` repo 建立，內含 `default.json` 共用 Renovate preset
- [x] `hugo-book-template` 本地已更新（`build.gradle.kts` 縮成 3 行、`.github/renovate.json` extends 中央 preset）
- [x] `system-design-interview` 作為驗證專案，本地已改成 plugin 形式
- [x] 已驗證：`./gradlew hugoBuild` 在 plugin 模式下仍可正常產出 76 pages / 516 non-page files

### Phase 1：遷移至 GitHub Packages（2026-04-16）

- [x] `book-gradle-conventions/build.gradle.kts`：移除 `com.gradle.plugin-publish`，改用 `maven-publish` + GitHub Packages
- [x] `book-gradle-conventions/.github/workflows/publish.yml`：改用 `publish` task + `GITHUB_TOKEN`
- [x] `hugo-book-template/settings.gradle.kts`：加 GitHub Packages maven repo
- [x] `hugo-book-template/.github/workflows/deploy.yml`：加 `packages: read` 權限 + 傳 GITHUB_TOKEN 給 Gradle
- [x] `system-design-interview/settings.gradle.kts`：同上
- [x] `system-design-interview/.github/workflows/deploy.yml`：同上

---

## 🚧 待完成步驟

### Step 1：Push book-gradle-conventions 變更，觸發首次 GitHub Packages 發佈

```bash
cd ~/workspace/andrew/books-management/book-gradle-conventions
git add build.gradle.kts .github/workflows/publish.yml
git commit -m "chore: switch publishing from Gradle Plugin Portal to GitHub Packages"
git push
```

等 GitHub Actions 跑完，確認 package 出現：
```bash
gh api /users/Andrewnplus/packages/maven/com.andrewnplus.book.com.andrewnplus.book.gradle.plugin --jq '.name' 2>/dev/null && echo "OK" || echo "NOT YET"
```

### Step 2：更新 Renovate 共享 preset（renovate-config）

需要讓 Renovate 知道去 GitHub Packages 查新版本。Clone 並更新 `default.json`：

```bash
cd ~/workspace/andrew/books-management
gh repo clone Andrewnplus/renovate-config
cd renovate-config
```

在 `default.json` 加入：

```json
{
  "hostRules": [
    {
      "matchHost": "maven.pkg.github.com",
      "hostType": "maven",
      "username": "Andrewnplus",
      "encrypted": {
        "token": "<encrypted-github-token>"
      }
    }
  ],
  "packageRules": [
    {
      "description": "book convention plugin from GitHub Packages",
      "matchPackageNames": ["com.andrewnplus.book"],
      "registryUrls": ["https://maven.pkg.github.com/Andrewnplus/book-gradle-conventions"]
    }
  ]
}
```

> **注意**：如果使用 Mend Renovate GitHub App，它對同一 owner 的 repo 通常有 `packages:read` 權限，可能不需要額外的 `hostRules`。先不加 hostRules 試試看，失敗再補。

簡化版（先試這個）：
```json
{
  "packageRules": [
    {
      "description": "book convention plugin from GitHub Packages",
      "matchPackageNames": ["com.andrewnplus.book"],
      "registryUrls": ["https://maven.pkg.github.com/Andrewnplus/book-gradle-conventions"]
    }
  ]
}
```

```bash
git add default.json
git commit -m "chore: add GitHub Packages registry for book convention plugin"
git push
```

### Step 3：驗證 plugin 從 GitHub Packages 可拉取

```bash
cd ~/workspace/andrew/books-management/books-done/tech/interview/system-design-interview
# 確認 settings.gradle.kts 已改好（Step 1 已完成）
export GITHUB_TOKEN=$(gh auth token)
./gradlew --refresh-dependencies hugoBuild
```

Build 成功就代表 GitHub Packages 正常運作。

### Step 4：Commit 並 push system-design-interview

此 repo 的 A 組（內容翻譯、Hugo 升級、preset）已以 3 個 commit 本地提交好。B 組（plugin migration）是 uncommitted 狀態。

```bash
cd ~/workspace/andrew/books-management/books-done/tech/interview/system-design-interview
git add build.gradle.kts settings.gradle.kts .github/workflows/deploy.yml
git commit -m "chore: migrate to com.andrewnplus.book convention plugin (GitHub Packages)"
git push    # 會一次推 A 組 3 個 commit + B 組這個 commit
```

### Step 5：Commit 並 push hugo-book-template

```bash
cd ~/workspace/andrew/books-management/books-done/hugo-book-template
git add build.gradle.kts settings.gradle.kts .github/workflows/deploy.yml .github/renovate.json
git commit -m "chore: migrate to com.andrewnplus.book convention plugin (GitHub Packages)"
git push
```

新生成的 book repo 就會一開始就是 plugin 版的乾淨樣板。

### Step 6：批次更新 662 個現有 book repo（本地 commit，不 push）

對每個 `build.gradle.kts` 已有 `version.set(...)` 的 repo：

1. 覆寫 `build.gradle.kts` 為：
   ```kotlin
   plugins {
       id("com.andrewnplus.book") version "0.1.<latest>"
   }
   ```
2. 寫入 `settings.gradle.kts`（若沒有就新建）：
   ```kotlin
   pluginManagement {
       repositories {
           maven {
               url = uri("https://maven.pkg.github.com/Andrewnplus/book-gradle-conventions")
               credentials {
                   username = System.getenv("GITHUB_ACTOR") ?: "Andrewnplus"
                   password = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_TOKEN") ?: ""
               }
           }
           gradlePluginPortal()
       }
   }
   rootProject.name = "<repo-name>"
   ```
3. 覆寫 `.github/renovate.json` 為：
   ```json
   {
     "$schema": "https://docs.renovatebot.com/renovate-schema.json",
     "extends": ["github>Andrewnplus/renovate-config"]
   }
   ```
4. 更新 `.github/workflows/deploy.yml`：
   - 加 `packages: read` 到 permissions
   - 在 `./gradlew spotlessCheck` step 加 `GITHUB_TOKEN` 和 `GITHUB_ACTOR` env
5. `git add -A && git commit -m "chore: migrate to com.andrewnplus.book convention plugin"`
6. **不要 push**。每個 repo 的 main 分支會累積一個未推送的 commit，由你手動逐批推。

建議腳本位置：`hugo-book-manager/scripts/migrate-to-plugin.sh`

### Step 7：驗證整條自動化鏈

等下一次 Hugo 發新版時觀察：
1. Renovate 在 `book-gradle-conventions` 開 branch → CI 過 → automerge
2. GitHub Action 自動 publish 新版 plugin 到 GitHub Packages
3. Renovate 在隨便挑一個 book repo 開 branch → CI 過 → automerge
4. 隔天 book repo 的 `build.gradle.kts` 版本字串已更新

整條過程應完全自動、零人工介入。

---

## 本地開發注意事項

### 本機 Gradle build 需要 GitHub token

GitHub Packages 即使是公開 package 也需要認證才能讀。本機開發時：

```bash
# 方法 1：用 gh CLI 的 token（推薦）
export GITHUB_TOKEN=$(gh auth token)
./gradlew hugoBuild

# 方法 2：寫入 ~/.gradle/gradle.properties
# gpr.user=Andrewnplus
# gpr.key=ghp_xxxxxxxxxxxx
```

### Consumer repo 的 settings.gradle.kts 認證流程

```
CI (GitHub Actions)          Local Dev
─────────────────           ──────────
GITHUB_TOKEN (自動提供)      GITHUB_TOKEN=$(gh auth token)
GITHUB_ACTOR (自動提供)      fallback → "Andrewnplus"
```

---

## 🔒 安全提醒

- Gradle Plugin Portal 的 API key/secret 已不再需要，可以：
  1. 到 https://plugins.gradle.org/user/me 刪除 key
  2. 從 `~/.gradle/gradle.properties` 移除相關設定
  3. 從 `Andrewnplus/book-gradle-conventions` 的 GitHub Secrets 移除 `GRADLE_PUBLISH_KEY` / `GRADLE_PUBLISH_SECRET`
- GitHub Packages 用的是 `GITHUB_TOKEN`（自動提供），不需要額外管理 secret
