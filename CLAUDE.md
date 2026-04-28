# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hugo Book Manager is a Kotlin + Gradle CLI tool that creates GitHub repositories for book notes. It uses an interactive two-phase workflow with Claude Code for AI-powered metadata and structure generation.

**Features:**
- Interactive AI-powered book metadata and structure generation via Claude Code
- One-stop book creation workflow (AI Task → GitHub → Clone → Update → Push)

## AI Task Processing Workflow

The `init-books` command uses a **two-phase interactive workflow**:

```
CLI 執行 → 產生 ai-tasks/input → CLI 暫停等待
   ↓
Claude 處理 AI 任務 → 寫入 ai-tasks/output
   ↓
CLI 再次執行 → 讀取輸出 → 建立 GitHub repo → 完成
```

### Processing AI Tasks

When user says **"請處理 AI 任務"** (please process AI task):

1. **Generate response** (metadata + structure) → `ai-tasks/output/batch-metadata-response.json`
2. **🚀 Execute CLI:**
   ```bash
   ./gradlew installDist --quiet
   echo -e "yes\nyes" | ./build/install/hugo-book-manager/bin/hugo-book-manager init-books
   ```
3. **Report result** (repo URL, website URL, local path)

> Duplicate detection is handled by the CLI: it loads
> `templates/existing-repos.yaml` and matches against the queue book before
> AI generation. Refresh that index with `./gradlew refreshRepoIndex` before
> running `init-books`.

See `templates/ai-task-guide.md` for detailed processing instructions.

## Common Commands

```bash
# Check environment prerequisites
./gradlew checkEnv

# Refresh the cached index of existing book repos on the configured owner
./gradlew refreshRepoIndex

# Initialize books from queue (two-phase workflow)
./gradlew initBooks                          # Process next pending book
./gradlew initBooks -Pid=<book-id>           # Process specific book
./gradlew initBooks -Pstatus=true            # Show queue status
./gradlew initBooks -Pid=<book-id> -Preset=true  # Reset book status
```

For book-project maintenance tasks (KaTeX, Mermaid, review-markdown, translate, etc.),
use the global `/book-*` slash commands instead of in-repo prompt templates.

## Code Architecture

### Project Structure

```
hugo-book-manager/
├── build.gradle.kts              # Build script + Gradle tasks
├── local.properties              # Configuration (gitignored)
├── ai-tasks/                     # AI task files (gitignored except .gitkeep)
│   ├── input/                    # Request files written by CLI
│   └── output/                   # Response files written by Claude
├── src/main/kotlin/com/nplus/bookmanager/
│   ├── Main.kt                   # CLI entry point (Clikt)
│   ├── config/
│   │   └── AppConfig.kt          # Configuration from local.properties
│   ├── model/
│   │   ├── AiTaskModels.kt       # Batch request/response models
│   │   ├── BookInput.kt          # Queue, metadata & structure models
│   │   └── RepoIndex.kt          # Cached index of existing book repos
│   ├── service/
│   │   ├── AiTaskService.kt      # AI task file management (batch only)
│   │   ├── BookInputService.kt   # Queue YAML parsing and validation
│   │   ├── BookRepoService.kt    # Book-repo creation workflow
│   │   ├── DocsStructureService.kt # Hugo docs folder creation
│   │   ├── GitHubCliService.kt   # gh CLI wrapper
│   │   ├── GitService.kt         # Git operations
│   │   ├── ImageService.kt       # Cover image download/resize
│   │   ├── RepoIndexService.kt   # Repo index load/save + duplicate matching
│   │   └── TemplateService.kt    # Template file modifications
│   ├── command/
│   │   ├── CheckEnvCommand.kt    # Environment check
│   │   ├── InitBooksCommand.kt   # Batch book initialization from queue
│   │   └── RefreshRepoIndexCommand.kt # Refresh cached repo index
│   └── util/
│       ├── CliFormatter.kt       # Console output formatting
│       ├── ProcessRunner.kt      # Shell command execution
│       └── UserInput.kt          # Interactive user prompts
└── templates/
    ├── ai-task-guide.md          # Guide for Claude to process AI tasks
    ├── books-queue.example.yaml  # Queue YAML template (copy to books-queue.yaml)
    ├── existing-repos.yaml       # Cached index of repos on the owner
    └── prompts/
        └── book-metadata.txt     # Prompt used internally by init-books
```

### Key Services

**`AiTaskService`** — Manages batch AI task files for Claude Code processing
- `writeBatchMetadataRequest()` / `readBatchMetadataResponse()`
- `hasPendingBatchMetadataTask()` / `hasCompletedBatchMetadataTask()`

**`BookRepoService`** — Orchestrates the full book-repo creation workflow
- Create GitHub repo → Clone → Update templates → Download cover → Create docs → Push → Enable Pages

**`GitHubCliService`** — All GitHub operations via `gh` CLI
- Repo CRUD, topics, homepage, Pages, branch waiting

**`BookInputService`** — Queue YAML I/O and validation
- `loadBooksQueue()` / `saveBooksQueue()` / `validateQueuedBook()`

### Application Flow: Initialize Books (`init-books`)

**Phase 1 (CLI):**
1. Read next pending book from queue YAML
2. Create `ai-tasks/input/batch-metadata-request.json`
3. Prompt user to ask Claude Code to process

**Claude Processing:**
1. Generate metadata (repo name, description, topics, category)
2. Generate docs structure from table of contents
3. Write response to `ai-tasks/output/batch-metadata-response.json`

**Phase 2 (CLI):**
1. Read AI-generated metadata and structure
2. Create GitHub repository from template
3. Clone to local work directory (under category folder)
4. Update template files (README, hugo.toml, go.mod, _index.md)
5. Download and resize cover image
6. Create docs folder structure with _index.md files
7. Commit and push initial content
8. Wait for gh-pages branch → Enable GitHub Pages

## Configuration

### local.properties

```properties
GITHUB_USERNAME=Andrewnplus
DEFAULT_WORK_DIR=/home/andrew/workspace/andrew/books-management/books
TEMPLATE_REPO=Andrewnplus/hugo-book-template
HOMEPAGE_BASE_URL=https://nplus.wiki
```

### Central Infrastructure

Book repos share configuration via two central repositories:

| Repo | Purpose |
|------|---------|
| `Andrewnplus/book-gradle-conventions` | Gradle convention plugin (Hugo version, Spotless) — published to GitHub Packages |
| `Andrewnplus/nplus-book-core` | Hugo theme (Go module) |
| `Andrewnplus/renovate-config` | Shared Renovate preset (monthly updates, automerge) |

## Dependencies

- **Kotlin 2.1+**: Core language
- **Gradle 9+**: Build system
- **Java 21+**: Runtime
- **GitHub CLI (`gh`)**: Required for repository operations
- **kotlinx-serialization**: JSON parsing
- **Clikt**: CLI framework
- **SnakeYAML**: YAML parsing
