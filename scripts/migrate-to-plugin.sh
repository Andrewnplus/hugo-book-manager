#!/usr/bin/env bash
#
# Batch migrate book repos to use the com.andrewnplus.book convention plugin
# from GitHub Packages.
#
# Usage:
#   ./scripts/migrate-to-plugin.sh [--dry-run] [--push]
#
# Options:
#   --dry-run   Show what would be changed without modifying files
#   --push      Push after committing (default: commit only, no push)
#
# Prerequisites:
#   - gh auth token must have read:packages scope
#   - GITHUB_TOKEN env var set (or gh CLI authenticated)

set -euo pipefail

SCAN_DIRS=(
    "/home/andrew/workspace/andrew/books-management/books-done"
    "/home/andrew/workspace/andrew/books-management/new-books"
    "/home/andrew/workspace/andrew/notes"
)
PLUGIN_VERSION="0.1.2"
COMMIT_MSG="chore: migrate to com.andrewnplus.book convention plugin"

DRY_RUN=false
DO_PUSH=false

for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        --push) DO_PUSH=true ;;
        *) echo "Unknown option: $arg"; exit 1 ;;
    esac
done

# Skip these directories
SKIP_DIRS=("hugo-book-template" "book-gradle-conventions")

migrated=0
skipped=0
errors=0

should_skip() {
    local dir_name
    dir_name=$(basename "$1")
    for skip in "${SKIP_DIRS[@]}"; do
        if [[ "$dir_name" == "$skip" ]]; then
            return 0
        fi
    done
    return 1
}

is_already_migrated() {
    local repo_dir="$1"
    grep -q "com.andrewnplus.book" "$repo_dir/build.gradle.kts" 2>/dev/null
}

migrate_repo() {
    local repo_dir="$1"
    local repo_name
    repo_name=$(basename "$repo_dir")

    # 1. build.gradle.kts
    cat > "$repo_dir/build.gradle.kts" <<GRADLE
plugins {
    id("com.andrewnplus.book") version "$PLUGIN_VERSION"
}
GRADLE

    # 2. settings.gradle.kts
    cat > "$repo_dir/settings.gradle.kts" <<GRADLE
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

rootProject.name = "$repo_name"
GRADLE

    # 3. .github/renovate.json
    mkdir -p "$repo_dir/.github"
    cat > "$repo_dir/.github/renovate.json" <<JSON
{
  "\$schema": "https://docs.renovatebot.com/renovate-schema.json",
  "extends": ["github>Andrewnplus/renovate-config"]
}
JSON

    # 4. deploy.yml - add packages:read and GITHUB_TOKEN env
    local deploy_yml="$repo_dir/.github/workflows/deploy.yml"
    if [[ -f "$deploy_yml" ]]; then
        # Add packages: read permission if not present
        if ! grep -q "packages:" "$deploy_yml"; then
            sed -i '/id-token: write/a\  packages: read' "$deploy_yml"
        fi

        # Upgrade Java 17 → 21
        sed -i 's/java-version: 17/java-version: 21/' "$deploy_yml"

        # Add GITHUB_TOKEN env to spotlessCheck step if not present
        if ! grep -q "GITHUB_TOKEN" "$deploy_yml"; then
            sed -i '/Run Spotless Check/,/run:/{
                /run:/i\        env:\n          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}\n          GITHUB_ACTOR: ${{ github.actor }}
            }' "$deploy_yml"
        fi
    fi

    # 5. Git commit
    cd "$repo_dir"
    git add build.gradle.kts settings.gradle.kts .github/renovate.json .github/workflows/deploy.yml 2>/dev/null

    if git diff --cached --quiet; then
        echo "  (no changes to commit)"
        return 0
    fi

    git commit -m "$COMMIT_MSG" --quiet

    if $DO_PUSH; then
        git push --quiet 2>/dev/null || {
            echo "  WARNING: push failed"
            return 1
        }
    fi
}

echo "=== Book Plugin Migration ==="
echo "Plugin version: $PLUGIN_VERSION"
echo "Dry run: $DRY_RUN"
echo "Push: $DO_PUSH"
echo ""

# Find all book repos (directories containing build.gradle.kts)
while IFS= read -r build_file; do
    repo_dir=$(dirname "$build_file")

    if should_skip "$repo_dir"; then
        continue
    fi

    if is_already_migrated "$repo_dir"; then
        skipped=$((skipped + 1))
        continue
    fi

    repo_name=$(basename "$repo_dir")

    if $DRY_RUN; then
        echo "[DRY-RUN] Would migrate: $repo_name"
        migrated=$((migrated + 1))
    else
        echo "Migrating: $repo_name"
        if migrate_repo "$repo_dir"; then
            migrated=$((migrated + 1))
        else
            echo "  ERROR: Failed to migrate $repo_name"
            errors=$((errors + 1))
        fi
    fi
done < <(for dir in "${SCAN_DIRS[@]}"; do
    [[ -d "$dir" ]] && find "$dir" -maxdepth 4 -name "build.gradle.kts" \
        -not -path "*/hugo-book-template/*" \
        -not -path "*/book-gradle-conventions/*"
done | sort)

echo ""
echo "=== Summary ==="
echo "Migrated: $migrated"
echo "Skipped (already migrated): $skipped"
echo "Errors: $errors"
