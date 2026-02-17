# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hugo Book Manager is a Kotlin + Gradle tool that automatically creates GitHub repositories for books from CSV data. It reads book metadata from CSV files and creates private GitHub repositories using the GitHub CLI, complete with descriptions, topics, and GitHub Pages configuration.

**Features:**
- Interactive AI-powered book metadata generation via Claude Code
- Batch renovate.json configuration updates
- One-stop book creation workflow (AI Task → GitHub → Clone → Update → Push)
- Prompt generation: Generate reusable prompts for book project maintenance tasks

## AI Task Processing Workflow

Commands that require AI processing (`init-book`, `init-books`) use a **two-phase interactive workflow**:

### How It Works

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   CLI 執行      │────▶│  產生輸入檔案   │────▶│   CLI 暫停      │
│   (Gradle)      │     │  ai-tasks/input │     │   等待處理      │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
        ┌────────────────────────────────────────────────┘
        ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  用戶告訴 Claude│────▶│  Claude 讀取    │────▶│  Claude 寫入    │
│  「請處理 AI 任務」│     │  + 處理任務     │     │  ai-tasks/output│
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
        ┌────────────────────────────────────────────────┘
        ▼
┌─────────────────┐     ┌─────────────────┐
│  用戶再次執行   │────▶│  CLI 讀取輸出   │
│  CLI (同指令)   │     │  繼續後續步驟   │
└─────────────────┘     └─────────────────┘
```

### Processing AI Tasks

When user says **"請處理 AI 任務"** (please process AI task):

#### For Book Creation (batch-metadata-request.json) - 一條龍完成

1. **🛑 PRE-CHECK: GitHub repo existence**
   ```bash
   gh repo view <username>/<repo-name> --json name 2>/dev/null
   ```
   - **If repo exists → STOP IMMEDIATELY** and report

2. **Check existing response**
   - If response exists AND GitHub repo exists → STOP
   - If response exists but no GitHub repo → Skip to step 4

3. **Generate response** (metadata + structure)
   - Write to `ai-tasks/output/batch-metadata-response.json`
   - Must include BOTH `metadata` AND `structure` fields

4. **🚀 Execute CLI (一條龍完成建立)**
   ```bash
   ./gradlew installDist --quiet
   echo -e "yes\nyes" | ./build/install/hugo-book-manager/bin/hugo-book-manager init-books
   ```

5. **Report result** (repo URL, website URL, local path)

#### For Other Tasks (convert, structure, etc.)

1. Check `ai-tasks/input/` for pending task files
2. Read task JSON and prompt template
3. Process and write output
4. Report completion

### Task Types

| Task File | Purpose | Output |
|-----------|---------|--------|
| `metadata-request.json` | Generate book metadata from title | `metadata-response.json` |
| `structure-request.json` | Generate docs structure from TOC | `structure-response.json` |

## Common Commands

### Gradle Tasks

```bash
# Check environment prerequisites
./gradlew checkEnv

# Create repos from CSV (dry-run)
./gradlew createRepos -Pcsv=data/test-3-books.csv -PdryRun=true

# Create repos from CSV (actual)
./gradlew createRepos -Pcsv=data/test-3-books.csv

# Resume from a specific book number
./gradlew createRepos -Pcsv=data/test-3-books.csv -PstartFrom=5

# Batch update renovate.json (dry-run)
./gradlew updateRenovate -PparentDir=/path/to/books -PdryRun=true

# Batch update renovate.json (actual)
./gradlew updateRenovate -PparentDir=/path/to/books

# Initialize a new book from YAML input - Two-phase workflow
# Phase 1: Generate AI tasks
./gradlew initBook -Pinput=templates/book-input.yaml
# Phase 2: After Claude processes, re-run to continue
./gradlew initBook -Pinput=templates/book-input.yaml

# Batch merge Renovate PRs with passing CI
./gradlew mergePrs -PparentDir=/path/to/books

# Merge PRs with specific merge method (merge, squash, rebase)
./gradlew mergePrs -PparentDir=/path/to/books -PmergeMethod=squash

# Generate prompt templates for book project tasks
./gradlew generatePrompt                                      # List available prompts
./gradlew generatePrompt -Ptype=restructure-folders           # Rename folders by _index.md title
./gradlew generatePrompt -Ptype=enhance-katex                 # Add KaTeX math formula support
./gradlew generatePrompt -Ptype=enhance-mermaid               # Add Mermaid diagram support
./gradlew generatePrompt -Ptype=review-markdown               # Review and fix markdown issues
./gradlew generatePrompt -Ptype=list-to-table                 # Convert lists to markdown tables
./gradlew generatePrompt -Ptype=simplify-table                # Simplify tables by removing redundant info
./gradlew generatePrompt -Ptype=extract-insights              # Extract insights from book notes

# Save prompt to file (for use in other projects)
./gradlew generatePrompt -Ptype=restructure-folders -Poutput=/path/to/book/PROMPT.md
```

### CLI Usage (Alternative)

```bash
# Build and run directly
./gradlew run --args="check-env"
./gradlew run --args="create-repos --csv data/test-3-books.csv --dry-run"
./gradlew run --args="update-renovate --parent-dir /path/to/books --dry-run"
./gradlew run --args="init-book --input templates/book-input.yaml --dry-run"
./gradlew run --args="merge-prs --parent-dir /path/to/books"
./gradlew run --args="generate-prompt --list"
./gradlew run --args="generate-prompt --type restructure-folders"
```

### Prerequisites Check

```bash
# Check GitHub CLI installation
gh --version

# Check GitHub authentication status
gh auth status

# Login to GitHub (if needed)
gh auth login
```

## Code Architecture

### Project Structure

```
hugo-book-manager/
├── build.gradle.kts              # Build script + custom Gradle tasks
├── settings.gradle.kts
├── gradle.properties
├── local.properties              # Configuration (gitignored)
├── ai-tasks/                     # AI task files (gitignored except .gitkeep)
│   ├── input/                    # Request files written by CLI
│   │   └── .gitkeep
│   └── output/                   # Response files written by Claude
│       └── .gitkeep
├── src/main/kotlin/com/nplus/bookmanager/
│   ├── Main.kt                   # CLI entry point (Clikt)
│   ├── config/
│   │   └── AppConfig.kt          # Configuration from local.properties
│   ├── model/
│   │   ├── AiTaskModels.kt        # AI task request/response models
│   │   ├── Book.kt               # CSV data model
│   │   └── BookInput.kt          # YAML input, queue, metadata & structure models
│   ├── service/
│   │   ├── AiTaskService.kt          # AI task file management
│   │   ├── BookInputService.kt       # YAML input file parsing
│   │   ├── BookRepoService.kt        # Shared book-repo creation workflow
│   │   ├── CsvService.kt             # CSV file reading
│   │   ├── DocsStructureService.kt   # Hugo docs folder creation
│   │   ├── GitHubCliService.kt       # gh CLI wrapper
│   │   ├── GitService.kt             # Git operations
│   │   ├── ImageService.kt           # Cover image download/resize
│   │   └── TemplateService.kt        # Template file modifications
│   ├── command/
│   │   ├── CheckEnvCommand.kt        # Environment check
│   │   ├── CreateReposCommand.kt     # Batch CSV creation
│   │   ├── GeneratePromptCommand.kt  # Generate prompt templates
│   │   ├── InitBookCommand.kt        # Single book initialization
│   │   ├── InitBooksCommand.kt       # Batch book initialization from queue
│   │   ├── MergePrsCommand.kt        # Batch merge Renovate PRs
│   │   └── UpdateRenovateCommand.kt  # Renovate config update
│   └── util/
│       └── ProcessRunner.kt      # Shell command execution
├── data/
│   └── *.csv                     # Book CSV files
└── templates/
    ├── ai-task-guide.md          # Guide for Claude to process AI tasks
    ├── book-input.yaml           # Template for init-book input
    ├── renovate.json             # Standard renovate config
    └── prompts/
        ├── book-metadata.txt             # Prompt for book metadata generation
        ├── book-structure.txt            # Prompt for docs structure generation
        ├── convert-list-to-table.txt     # Prompt for converting lists to tables
        ├── simplify-table.txt            # Prompt for simplifying tables
        ├── doc-convert.txt               # Prompt for document conversion
        ├── restructure-folders.txt       # Prompt for renaming folders
        ├── enhance-markdown-katex.txt    # Prompt for KaTeX support
        ├── enhance-markdown-mermaid.txt  # Prompt for Mermaid support
        └── review-markdown.txt           # Prompt for markdown review
```

### Key Services

**`AiTaskService`** (`service/AiTaskService.kt`):
- `writeMetadataRequest()`: Write metadata generation task
- `writeStructureRequest()`: Write docs structure generation task
- `writeConvertRequest()`: Write document conversion task
- `readMetadataResponse()`: Read generated metadata
- `readStructureResponse()`: Read generated docs structure
- `hasPendingTask()`, `hasCompletedTask()`: Check task status
- `clearTasks()`: Clean up task files

**`GitHubCliService`** (`service/GitHubCliService.kt`):
- `isGhInstalled()`, `isAuthenticated()`: Environment checks
- `createRepo()`: Create repository from template
- `setHomepage()`, `addTopics()`, `starRepo()`: Configure repository
- `enableGitHubPages()`: Enable GitHub Pages
- `repoExists()`, `cloneRepo()`: Repository operations

**`DocsStructureService`** (`service/DocsStructureService.kt`):
- `createDocsStructure()`: Create Hugo docs folder structure with _index.md files
- `printDocsStructure()`: Display structure to console

**`BookInputService`** (`service/BookInputService.kt`):
- `loadBookInput()`: Parse YAML input file for init-book command
- `validate()`: Validate required fields in BookInput

**`TemplateService`** (`service/TemplateService.kt`):
- `updateTemplateFiles()`: Replace template placeholders in cloned repos
- `updateRenovateJson()`: Apply standard renovate configuration

**`GitService`** (`service/GitService.kt`):
- `pull()`, `add()`, `commit()`, `push()`: Git operations
- `commitAndPush()`: Combined workflow

**`ImageService`** (`service/ImageService.kt`):
- `downloadAndResize()`: Download cover image and resize for Hugo theme

### Application Flows

#### 1. Create Repos from CSV (`create-repos`)
1. Check GitHub CLI and authentication
2. Load books from CSV file
3. For each book:
   - Create/update private repo from template
   - Set homepage, description, topics
   - Enable GitHub Pages
   - Star repository
   - Rate limit (3s delay)

#### 2. Update Renovate (`update-renovate`)
1. Scan parent directory for git repositories
2. For each repository:
   - `git pull`
   - Update `.github/renovate.json` with standard config
   - `git commit` + `git push`

#### 3. Initialize Book (`init-book`) (Two-Phase)
**Phase 1:**
1. Read book info from YAML input file
2. Create `ai-tasks/input/metadata-request.json`
3. Create `ai-tasks/input/structure-request.json`
4. Prompt user to ask Claude Code to process

**Claude Processing:**
1. Generate metadata (repo name, description, topics, category)
2. Generate docs structure from table of contents
3. Write responses to `ai-tasks/output/`

**Phase 2:**
1. Read AI-generated metadata and structure
2. Create GitHub repository from template
3. Clone to local work directory (under category folder)
4. Update template files (README, hugo.toml, go.mod, _index.md)
5. Download and resize cover image
6. Create docs folder structure with _index.md files

#### 4. Merge Pull Requests (`merge-prs`)
1. Scan parent directory for git repositories
2. For each repository:
   - Find open Renovate PRs
   - Check if CI checks pass
   - Merge passing PRs with specified method (merge/squash/rebase)
3. Summary of merged/skipped PRs

#### 5. Generate Prompt (`generate-prompt`)
Generate reusable prompt templates for book project maintenance:

| Type | Description |
|------|-------------|
| `restructure-folders` | Rename folders based on `_index.md` titles |
| `enhance-katex` | Add KaTeX math formula support |
| `enhance-mermaid` | Add Mermaid diagram support |
| `review-markdown` | Review and fix markdown quality issues |
| `list-to-table` | Convert list items to markdown tables |
| `simplify-table` | Simplify tables by removing redundant info |
| `extract-insights` | Extract valuable insights from book notes |

**Usage workflow:**
1. Run `./gradlew generatePrompt -Ptype=<type>` to display prompt
2. Copy the prompt
3. Open the target book project in Claude Code
4. Paste the prompt and follow instructions

## Configuration

### local.properties

```properties
GITHUB_USERNAME=Andrewnplus
DEFAULT_WORK_DIR=/home/andrew/workspace/andrew/books-management/books
TEMPLATE_REPO=Andrewnplus/hugo-book-template
HOMEPAGE_BASE_URL=https://nplus.wiki
```

## CSV Data Format

Expected CSV columns:
- `chinese_title`: Chinese book title
- `english_title`: English book title
- `author`: Book author
- `repo_name`: GitHub repository name (kebab-case)
- `description`: Repository description
- `topics`: Space-separated topics for repository
- `category`: Book category
- `category_topic`: Category-specific topic
- `kindle_url`: Kindle store URL
- `books_tw_url`: Books.com.tw URL
- `publication_date`: Publication date
- `notes`: Additional notes

## Dependencies

- **Kotlin 1.9+**: Core language
- **Gradle 8+**: Build system
- **Java 17+**: Runtime
- **GitHub CLI (`gh`)**: Required for repository operations
- **kotlinx-serialization**: JSON parsing
- **kotlin-csv**: CSV file reading
- **Clikt**: CLI framework

## Development Notes

### General
- All repository operations use GitHub CLI rather than direct API calls
- AI processing uses interactive Claude Code workflow (no API key required)
- Dry-run mode available for all commands
- Progress tracking with detailed console output

### AI Task Processing
- Task files are stored in `ai-tasks/input/` and `ai-tasks/output/`
- Task files are gitignored (only `.gitkeep` is tracked)
- See `templates/ai-task-guide.md` for detailed processing instructions
- Prompt templates are in `templates/prompts/`

### Prompt Generation
- Prompts are standalone instructions that can be used in any Hugo Book project
- Use `./gradlew generatePrompt --list` to see all available prompts
- Prompts can be saved to file: `./gradlew generatePrompt -Ptype=<type> -Poutput=PROMPT.md`
- When pasted to Claude Code in another project, Claude will understand and execute the task

### Hugo Book Structure
- Hugo Book uses a specific folder structure: `site/content/docs/`
- Each section/chapter needs an `_index.md` file with frontmatter (title, weight)
- The `DocsStructureService` handles creation of this folder structure
- AI generates folder names in kebab-case from chapter titles
- Weights determine navigation order in the Hugo Book theme

### YAML Input Format (for init-book)
The `templates/book-input.yaml` template contains:
- `chineseTitle`, `englishTitle`: Book titles
- `author`: Book author name
- `publicationDate`: Publication date
- `coverUrl`: URL to book cover image
- `purchaseUrl`: Link to purchase the book
- `tableOfContents`: Full table of contents text for AI processing
