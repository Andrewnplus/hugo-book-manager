# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hugo Book Manager is a Kotlin + Gradle CLI tool that creates GitHub repositories for book notes. It uses an interactive two-phase workflow with Claude Code for AI-powered metadata and structure generation.

**Features:**
- Interactive AI-powered book metadata and structure generation via Claude Code
- One-stop book creation workflow (AI Task → GitHub → Clone → Update → Push)
- Prompt generation for book project maintenance tasks

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

1. **🛑 PRE-CHECK:** Check if GitHub repo already exists → STOP if yes
2. **Generate response** (metadata + structure) → `ai-tasks/output/batch-metadata-response.json`
3. **🚀 Execute CLI:**
   ```bash
   ./gradlew installDist --quiet
   echo -e "yes\nyes" | ./build/install/hugo-book-manager/bin/hugo-book-manager init-books
   ```
4. **Report result** (repo URL, website URL, local path)

See `templates/ai-task-guide.md` for detailed processing instructions.

## Common Commands

```bash
# Check environment prerequisites
./gradlew checkEnv

# Initialize books from queue (two-phase workflow)
./gradlew initBooks                          # Process next pending book
./gradlew initBooks -Pid=<book-id>           # Process specific book
./gradlew initBooks -Pstatus=true            # Show queue status
./gradlew initBooks -Pid=<book-id> -Preset=true  # Reset book status

# Generate prompt templates
./gradlew generatePrompt                     # List available prompts
./gradlew generatePrompt -Ptype=<type>       # Display prompt
./gradlew generatePrompt -Ptype=<type> -Poutput=/path/to/file  # Save to file
```

### Available Prompt Types

| Type | Description |
|------|-------------|
| `build-structure` | Build folder structure from TOC |
| `enhance-katex` | Add KaTeX math formula support |
| `enhance-mermaid` | Add Mermaid diagram support |
| `review-markdown` | Review and fix markdown quality issues |
| `rewrite-content` | Rewrite content for better readability |
| `list-to-table` | Convert list items to markdown tables |
| `simplify-table` | Simplify tables by removing redundant info |
| `extract-insights` | Extract valuable insights from book notes |
| `refine-notes` | Improve structure and readability of book notes |
| `extract-pdf-figures` | Extract figures from PDF and convert to Markdown |
| `translate-content` | Translate English notes to Traditional Chinese |
| `generate-summary` | Generate chapter summaries |
| `check-links` | Check internal links, image paths, and external URLs |
| `generate-glossary` | Generate glossary from book terms |
| `split-long-chapter` | Split long chapters into sub-sections |
| `generate-handout` | Generate fill-in-the-blank Word handout |
| `generate-podcast-prep` | Generate podcast preparation guide |

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
│   │   └── BookInput.kt          # Queue, metadata & structure models
│   ├── service/
│   │   ├── AiTaskService.kt      # AI task file management (batch only)
│   │   ├── BookInputService.kt   # Queue YAML parsing and validation
│   │   ├── BookRepoService.kt    # Book-repo creation workflow
│   │   ├── DocsStructureService.kt # Hugo docs folder creation
│   │   ├── GitHubCliService.kt   # gh CLI wrapper
│   │   ├── GitService.kt         # Git operations
│   │   ├── ImageService.kt       # Cover image download/resize
│   │   └── TemplateService.kt    # Template file modifications
│   ├── command/
│   │   ├── CheckEnvCommand.kt    # Environment check
│   │   ├── InitBooksCommand.kt   # Batch book initialization from queue
│   │   └── GeneratePromptCommand.kt # Generate prompt templates
│   └── util/
│       ├── CliFormatter.kt       # Console output formatting
│       ├── ProcessRunner.kt      # Shell command execution
│       └── UserInput.kt          # Interactive user prompts
└── templates/
    ├── ai-task-guide.md          # Guide for Claude to process AI tasks
    ├── book-input.yaml           # Template for queue entry format
    └── prompts/                  # 20 prompt templates for book tasks
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
