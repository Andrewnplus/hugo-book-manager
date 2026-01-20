# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hugo Book Manager is a Kotlin + Gradle tool that automatically creates GitHub repositories for books from CSV data. It reads book metadata from CSV files and creates private GitHub repositories using the GitHub CLI, complete with descriptions, topics, and GitHub Pages configuration.

**Features:**
- Interactive AI-powered book metadata generation via Claude Code
- Batch renovate.json configuration updates
- One-stop book creation workflow (AI Task → GitHub → Clone → Update → Push)
- Document cleaning: Convert PDF/HTML/MHTML to clean Markdown
- Document conversion: Transform Markdown to Hugo-book format using AI
- Rebuild docs structure: Regenerate Hugo-book docs folder from table of contents
- Prompt generation: Generate reusable prompts for book project maintenance tasks

## AI Task Processing Workflow

Commands that require AI processing (`init-book`, `rebuild-docs`, `convert-docs`) use a **two-phase interactive workflow**:

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

1. Check `ai-tasks/input/` for pending task files
2. Read the task JSON and corresponding prompt template
3. Process according to task type (see `templates/ai-task-guide.md` for details)
4. Write output to `ai-tasks/output/` (or directly to specified paths for convert tasks)

### Task Types

| Task File | Purpose | Output |
|-----------|---------|--------|
| `metadata-request.json` | Generate book metadata from title | `metadata-response.json` |
| `structure-request.json` | Generate docs structure from TOC | `structure-response.json` |
| `convert-request.json` | Convert MD to Hugo-book format | Direct to specified output paths |

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

# Clean HTML/MHTML documents to Markdown (Stage 1)
./gradlew cleanDocs -PinputDir=/path/to/html/files -PdryRun=true
./gradlew cleanDocs -PinputDir=/path/to/html/files
./gradlew cleanDocs -PinputDir=/path/to/html/files -PoutputDir=/path/to/output

# Clean a single file
./gradlew cleanDocs -PinputDir=. -Psingle=sampleFile.mhtml

# Convert Markdown to Hugo-book format (Stage 2) - Two-phase workflow
# Phase 1: Generate AI task
./gradlew convertDocs -PinputDir=/path/to/cleaned/md
# Phase 2: After Claude processes, re-run to verify
./gradlew convertDocs -PinputDir=/path/to/cleaned/md

# Convert with custom prompt
./gradlew convertDocs -PinputDir=/path/to/md -Pprompt=my-prompt.txt

# Initialize a new book from YAML input - Two-phase workflow
# Phase 1: Generate AI tasks
./gradlew initBook -Pinput=templates/book-input.yaml
# Phase 2: After Claude processes, re-run to continue
./gradlew initBook -Pinput=templates/book-input.yaml

# Rebuild docs structure - Two-phase workflow
# Phase 1: Generate AI task
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt
# Phase 2: After Claude processes, re-run to apply
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt

# Rebuild docs (skip confirmation prompt)
./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt -Pyes=true

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
./gradlew generatePrompt -Ptype=doc-convert                   # Document conversion prompt
./gradlew generatePrompt -Ptype=list-to-table                 # Convert lists to markdown tables

# Save prompt to file (for use in other projects)
./gradlew generatePrompt -Ptype=restructure-folders -Poutput=/path/to/book/PROMPT.md
```

### CLI Usage (Alternative)

```bash
# Build and run directly
./gradlew run --args="check-env"
./gradlew run --args="create-repos --csv data/test-3-books.csv --dry-run"
./gradlew run --args="update-renovate --parent-dir /path/to/books --dry-run"
./gradlew run --args="clean-docs --input-dir /path/to/html --dry-run"
./gradlew run --args="convert-docs --input-dir /path/to/md --dry-run"
./gradlew run --args="init-book --input templates/book-input.yaml --dry-run"
./gradlew run --args="rebuild-docs --repo-dir /path/to/book --toc /path/to/toc.txt --dry-run"
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
│   │   ├── Book.kt               # CSV data model
│   │   ├── BookInput.kt          # YAML input model for init-book
│   │   ├── DocsStructure.kt      # AI-generated docs structure model
│   │   └── GeneratedMetadata.kt  # AI response model
│   ├── service/
│   │   ├── AiTaskService.kt          # AI task file management
│   │   ├── BookInputService.kt       # YAML input file parsing
│   │   ├── CsvService.kt             # CSV file reading
│   │   ├── DocumentCleanerService.kt # PDF/HTML/MHTML to Markdown
│   │   ├── DocsStructureService.kt   # Hugo docs folder creation
│   │   ├── GitHubCliService.kt       # gh CLI wrapper
│   │   ├── GitService.kt             # Git operations
│   │   ├── ImageService.kt           # Cover image download/resize
│   │   └── TemplateService.kt        # Template file modifications
│   ├── command/
│   │   ├── CheckEnvCommand.kt        # Environment check
│   │   ├── CleanDocsCommand.kt       # Stage 1: Clean PDF/HTML/MHTML
│   │   ├── ConvertDocsCommand.kt     # Stage 2: Convert to Hugo-book
│   │   ├── CreateReposCommand.kt     # Batch CSV creation
│   │   ├── GeneratePromptCommand.kt  # Generate prompt templates
│   │   ├── InitBookCommand.kt        # One-stop book initialization
│   │   ├── MergePrsCommand.kt        # Batch merge Renovate PRs
│   │   ├── RebuildDocsCommand.kt     # Rebuild docs structure
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
- `isValidHugoBookRepo()`: Validate Hugo Book repository structure
- `printDocsStructure()`: Display structure to console

**`DocumentCleanerService`** (`service/DocumentCleanerService.kt`):
- `cleanDocument()`: Clean a single PDF/HTML/MHTML file to Markdown
- `cleanDirectory()`: Batch clean all documents in a directory
- Handles PDF text extraction, MHTML multipart parsing, quoted-printable decoding

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

#### 3. Clean Documents (`clean-docs`) - Stage 1
1. Scan input directory for PDF/HTML/MHTML files
2. For each file:
   - **PDF**: Extract text using Apache PDFBox, detect headings/lists
   - **MHTML**: Parse multipart format, decode quoted-printable
   - **HTML**: Parse with Jsoup, extract main content
   - Convert to clean Markdown with headings, lists, bold/italic
3. Write cleaned Markdown files to output directory

#### 4. Convert Documents (`convert-docs`) - Stage 2 (Two-Phase)
**Phase 1:**
1. Scan input directory for Markdown files
2. Create `ai-tasks/input/convert-request.json` with file list
3. Prompt user to ask Claude Code to process

**Claude Processing:**
1. Read prompt from `templates/prompts/doc-convert.txt`
2. For each file, read content and generate Hugo-book formatted Markdown
3. Write converted files to specified output paths

**Phase 2:**
1. Check if all output files exist
2. Clean up task files
3. Report completion

#### 5. Initialize Book (`init-book`) (Two-Phase)
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

#### 6. Rebuild Docs Structure (`rebuild-docs`) (Two-Phase)
**Phase 1:**
1. Validate target directory is a Hugo Book repository
2. Read table of contents (from file, text argument, or interactive input)
3. Create `ai-tasks/input/structure-request.json`
4. Prompt user to ask Claude Code to process

**Claude Processing:**
1. Generate docs structure from TOC
2. Write response to `ai-tasks/output/structure-response.json`

**Phase 2:**
1. Read AI-generated structure
2. Preview changes (folders to delete/create)
3. User prompted to confirm (can skip with --yes flag)
4. Delete existing doc folders
5. Create new folder structure with proper _index.md files

#### 7. Merge Pull Requests (`merge-prs`)
1. Scan parent directory for git repositories
2. For each repository:
   - Find open Renovate PRs
   - Check if CI checks pass
   - Merge passing PRs with specified method (merge/squash/rebase)
3. Summary of merged/skipped PRs

#### 8. Generate Prompt (`generate-prompt`)
Generate reusable prompt templates for book project maintenance:

| Type | Description |
|------|-------------|
| `restructure-folders` | Rename folders based on `_index.md` titles |
| `enhance-katex` | Add KaTeX math formula support |
| `enhance-mermaid` | Add Mermaid diagram support |
| `review-markdown` | Review and fix markdown quality issues |
| `doc-convert` | Convert documents to Hugo-book format |
| `list-to-table` | Convert list items to markdown tables |

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
- **Jsoup**: HTML parsing for document cleaning
- **Apache PDFBox**: PDF text extraction

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

### Document Processing
- Document cleaning supports PDF, MHTML (saved web pages), HTML, and HTM formats
- PDF parsing extracts text and attempts to detect headings, lists, and paragraphs
- MHTML parsing handles quoted-printable encoding and multipart MIME structure
- Slate.js editor format (used by 極客時間) is properly converted to Markdown
- Custom prompts can be used for document conversion via `--prompt` option
- The default conversion prompt is stored in `templates/prompts/doc-convert.txt`

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
