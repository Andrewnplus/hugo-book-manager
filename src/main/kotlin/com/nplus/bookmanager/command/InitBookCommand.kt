package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.service.AiTaskService
import com.nplus.bookmanager.service.BookInputService
import com.nplus.bookmanager.service.DocsStructureService
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.ImageService
import java.io.File

/**
 * Command to initialize a new book repository from YAML input file
 *
 * This command uses a two-phase workflow:
 *
 * Phase 1 (First Run):
 * 1. Read book info from YAML
 * 2. Generate AI task files for metadata and structure generation
 * 3. Prompt user to ask Claude Code to process the tasks
 *
 * Phase 2 (Second Run - after Claude processes):
 * 1. Read AI-generated metadata and structure
 * 2. Create GitHub repo from template
 * 3. Clone to local (under category folder)
 * 4. Update template files
 * 5. Download and resize cover image
 * 6. Create docs folder structure
 */
class InitBookCommand : CliktCommand(name = "init-book") {
    override fun help(context: Context) = "Initialize a new book repository from YAML input file"

    private val inputFile by option("--input", "-i", help = "Path to book input YAML file")
        .default("templates/book-input.yaml")

    private val dryRun by option("--dry-run", "-n", help = "Simulate without making changes")
        .flag(default = false)

    private val bookInputService = BookInputService()
    private val aiTaskService = AiTaskService()
    private val ghService = GitHubCliService()
    private val imageService = ImageService()
    private val docsStructureService = DocsStructureService()

    override fun run() {
        printHeader()

        // Check prerequisites
        if (!checkPrerequisites()) return

        // Step 1: Read input file
        println("\nStep 1: Reading input file...")
        val input = bookInputService.loadBookInput(File(inputFile))
        if (input == null) {
            println("Error: Failed to read input file")
            return
        }

        val validationErrors = bookInputService.validate(input)
        if (validationErrors.isNotEmpty()) {
            println("Error: Invalid input file:")
            validationErrors.forEach { println("  - $it") }
            return
        }

        println("  Book: ${input.chineseTitle} (${input.englishTitle})")
        println("  Author: ${input.author}")

        // Check if we have completed AI tasks
        val hasMetadata = aiTaskService.hasCompletedMetadataTask()
        val hasStructure = aiTaskService.hasCompletedStructureTask()

        if (!hasMetadata || !hasStructure) {
            // Phase 1: Generate AI task files
            runPhase1(input)
            return
        }

        // Phase 2: Read AI results and continue
        runPhase2(input)
    }

    /**
     * Phase 1: Write AI task files and prompt user to process with Claude Code
     */
    private fun runPhase1(input: BookInput) {
        println("\n[Phase 1] Generating AI tasks...")

        // Check if tasks already pending
        if (aiTaskService.hasPendingMetadataTask() || aiTaskService.hasPendingStructureTask()) {
            println()
            println("⚠️  AI tasks are already pending. Please ask Claude Code to process them.")
            println()
            println("   Task files:")
            println("   - ai-tasks/input/metadata-request.json")
            println("   - ai-tasks/input/structure-request.json")
            println()
            println("👉 Tell Claude Code: \"請處理 AI 任務\"")
            println("   Then re-run this command.")
            return
        }

        // Write metadata request
        println("\nStep 2: Creating metadata generation task...")
        val metadataFile = aiTaskService.writeMetadataRequest(input)
        println("  Created: ${metadataFile.path}")

        // Write structure request
        println("\nStep 3: Creating docs structure generation task...")
        val structureFile = aiTaskService.writeStructureRequest(input.tableOfContents)
        println("  Created: ${structureFile.path}")

        // Prompt user
        println()
        println("━".repeat(70))
        println("📋 AI Tasks Generated")
        println("━".repeat(70))
        println()
        println("Two AI tasks have been created:")
        println()
        println("  1. Metadata Generation")
        println("     Input:  ${metadataFile.path}")
        println("     Output: ai-tasks/output/metadata-response.json")
        println()
        println("  2. Docs Structure Generation")
        println("     Input:  ${structureFile.path}")
        println("     Output: ai-tasks/output/structure-response.json")
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to continue.")
        println()
        println("━".repeat(70))
    }

    /**
     * Phase 2: Read AI results and continue with repository creation
     */
    private fun runPhase2(input: BookInput) {
        println("\n[Phase 2] Reading AI results and creating repository...")

        // Read metadata
        println("\nStep 2: Reading generated metadata...")
        val metadata = aiTaskService.readMetadataResponse()
        if (metadata == null) {
            println("Error: Failed to read metadata response")
            println("  Please check: ai-tasks/output/metadata-response.json")
            return
        }
        printMetadata(metadata)

        // Read structure
        println("\nStep 3: Reading generated docs structure...")
        val docsStructure = aiTaskService.readStructureResponse()
        if (docsStructure == null) {
            println("Error: Failed to read structure response")
            println("  Please check: ai-tasks/output/structure-response.json")
            return
        }
        docsStructureService.printDocsStructure(docsStructure)

        if (dryRun) {
            printDryRunSummary(input, metadata, docsStructure)
            // Clean up task files in dry-run
            println("\n[DRY RUN] Cleaning up task files...")
            aiTaskService.clearMetadataTasks()
            aiTaskService.clearStructureTasks()
            println("  Task files cleared.")
            return
        }

        // Confirm before proceeding
        print("\nProceed with creating repository? (yes/no): ")
        val confirm = readLine()?.lowercase()
        if (confirm != "yes") {
            println("Cancelled")
            return
        }

        // Step 4: Create GitHub repository
        println("\nStep 4: Creating GitHub repository...")
        val username = ghService.getUsername()
        if (username == null) {
            println("Error: Could not get GitHub username")
            return
        }

        if (!createGitHubRepo(username, metadata)) return

        // Step 5: Clone repository
        println("\nStep 5: Cloning repository...")
        val repoDir = cloneRepository(username, metadata)
        if (repoDir == null) {
            println("Error: Failed to clone repository")
            return
        }

        // Step 6: Update template files
        println("\nStep 6: Updating template files...")
        updateTemplateFiles(repoDir, metadata, input)

        // Step 7: Download and resize cover image
        println("\nStep 7: Processing cover image...")
        val coverFile = File(repoDir, "site/static/cover.png")
        if (!imageService.downloadAndResize(input.coverUrl, coverFile)) {
            println("Warning: Failed to download cover image")
        }

        // Step 8: Create docs structure
        println("\nStep 8: Creating docs folder structure...")
        val createdCount = docsStructureService.createDocsStructure(repoDir, docsStructure)
        println("  Created $createdCount items")

        // Clean up task files
        println("\nCleaning up AI task files...")
        aiTaskService.clearMetadataTasks()
        aiTaskService.clearStructureTasks()
        println("  Task files cleared.")

        // Done!
        printSuccessSummary(username, metadata, repoDir)
    }

    private fun printHeader() {
        println("=".repeat(70))
        println("Hugo Book Manager - Initialize New Book")
        println("=".repeat(70))
    }

    private fun checkPrerequisites(): Boolean {
        println("\nChecking prerequisites...")

        print("  GitHub CLI... ")
        if (!ghService.isGhInstalled()) {
            println("NOT FOUND")
            return false
        }
        println("OK")

        print("  GitHub authentication... ")
        if (!ghService.isAuthenticated()) {
            println("NOT AUTHENTICATED")
            return false
        }
        println("OK")

        return true
    }

    private fun printMetadata(metadata: GeneratedMetadata) {
        println("  Generated metadata:")
        println("    Repo Name: ${metadata.repoName}")
        println("    Description: ${metadata.description}")
        println("    Category: ${metadata.category}")
        println("    Topics: ${metadata.topics.joinToString(", ")}")
    }

    private fun printDryRunSummary(
        input: BookInput,
        metadata: GeneratedMetadata,
        structure: DocsStructure,
    ) {
        println("\n[DRY RUN] Would perform the following steps:")
        println("  4. Create GitHub repo: ${metadata.repoName}")
        println("  5. Clone to: ${AppConfig.defaultWorkDir}/${metadata.category}/${metadata.repoName}")
        println("  6. Update template files (README.md, hugo.toml, etc.)")
        println("  7. Download cover from: ${input.coverUrl}")
        println("  8. Create ${structure.sections.size} doc sections")
    }

    private fun createGitHubRepo(
        username: String,
        metadata: GeneratedMetadata,
    ): Boolean {
        if (ghService.repoExists(username, metadata.repoName)) {
            println("  Repository already exists: ${metadata.repoName}")
            print("  Continue with cloning and updating? (yes/no): ")
            val confirm = readLine()?.lowercase()
            if (confirm != "yes") {
                println("Cancelled")
                return false
            }
        } else {
            if (!ghService.createRepo(metadata.repoName, metadata.description)) {
                println("Error: Failed to create repository")
                return false
            }
            println("  Repository created: ${metadata.repoName}")
            Thread.sleep(2000) // Wait for GitHub to process
        }

        // Configure repository
        val homepageUrl = "${AppConfig.homepageBaseUrl}/${metadata.repoName}"
        ghService.setHomepage(username, metadata.repoName, homepageUrl)
        println("  Homepage set: $homepageUrl")

        ghService.enableGitHubPages(username, metadata.repoName)
        println("  GitHub Pages enabled")

        val addedTopics = ghService.addTopics(username, metadata.repoName, metadata.topics)
        println("  Added $addedTopics topics")

        ghService.starRepo(username, metadata.repoName)
        println("  Repository starred")

        return true
    }

    private fun cloneRepository(
        username: String,
        metadata: GeneratedMetadata,
    ): File? {
        // Create category subfolder
        val categoryDir = File(AppConfig.defaultWorkDir, metadata.category)
        if (!categoryDir.exists()) {
            categoryDir.mkdirs()
        }

        val repoDir = File(categoryDir, metadata.repoName)

        if (repoDir.exists()) {
            println("  Directory already exists: ${repoDir.absolutePath}")
            return repoDir
        }

        if (!ghService.cloneRepo(username, metadata.repoName, repoDir.absolutePath)) {
            return null
        }

        println("  Cloned to: ${repoDir.absolutePath}")
        return repoDir
    }

    private fun updateTemplateFiles(
        repoDir: File,
        metadata: GeneratedMetadata,
        input: BookInput,
    ) {
        val oldSlug = "hugo-book-template"
        val oldEnTitle = "Hugo Book Template"
        val oldZhTitle = "讀書筆記模版"

        // Update README.md
        updateFile(
            File(repoDir, "README.md"),
            mapOf(
                oldSlug to metadata.repoName,
                oldEnTitle to metadata.englishTitle,
                oldZhTitle to metadata.chineseTitle,
            ),
        )

        // Update settings.gradle.kts
        updateFile(
            File(repoDir, "settings.gradle.kts"),
            mapOf(oldSlug to metadata.repoName),
        )

        // Update site/hugo.toml
        updateFile(
            File(repoDir, "site/hugo.toml"),
            mapOf(
                oldZhTitle to metadata.chineseTitle,
                oldSlug to metadata.repoName,
            ),
        )

        // Update site/content/_index.md with book info
        updateIndexFile(repoDir, input)

        // Update site/go.mod
        updateGoMod(repoDir, metadata.repoName)
    }

    private fun updateFile(
        file: File,
        replacements: Map<String, String>,
    ) {
        if (!file.exists()) {
            println("  Warning: File not found: ${file.path}")
            return
        }

        var content = file.readText()
        for ((old, new) in replacements) {
            content = content.replace(old, new)
        }
        file.writeText(content)
        println("  Updated: ${file.name}")
    }

    private fun updateIndexFile(
        repoDir: File,
        input: BookInput,
    ) {
        val indexFile = File(repoDir, "site/content/_index.md")
        if (!indexFile.exists()) {
            println("  Warning: _index.md not found")
            return
        }

        var content = indexFile.readText()

        // Replace placeholders
        content =
            content
                .replace("src=\"cover.png\"", "src=\"cover.png\"") // Keep as is, we download the image
                .replace("author=\"待填寫作者\"", "author=\"${input.author}\"")
                .replace("date=\"待填寫日期\"", "date=\"${input.publicationDate}\"")
                .replace("link=\"https://www.amazon.com/\"", "link=\"${input.purchaseUrl}\"")

        indexFile.writeText(content)
        println("  Updated: _index.md (author, date, purchase link)")
    }

    private fun updateGoMod(
        repoDir: File,
        repoName: String,
    ) {
        val goModFile = File(repoDir, "site/go.mod")
        if (!goModFile.exists()) {
            println("  Warning: go.mod not found")
            return
        }

        var content = goModFile.readText()
        content =
            content.replace(
                Regex("module github\\.com/[^/]+/[^\\s]+"),
                "module github.com/${AppConfig.githubUsername}/$repoName",
            )
        goModFile.writeText(content)
        println("  Updated: go.mod")
    }

    private fun printSuccessSummary(
        username: String,
        metadata: GeneratedMetadata,
        repoDir: File,
    ) {
        println()
        println("=".repeat(70))
        println("Book Initialized Successfully!")
        println("=".repeat(70))
        println()
        println("  Book: ${metadata.chineseTitle}")
        println("  Repository: https://github.com/$username/${metadata.repoName}")
        println("  Website: ${AppConfig.homepageBaseUrl}/${metadata.repoName}")
        println("  Local Path: ${repoDir.absolutePath}")
        println()
        println("Next steps:")
        println("  1. Open in IDE: ${repoDir.absolutePath}")
        println("  2. Review and edit the generated content")
        println("  3. Commit and push when ready")
        println()
    }
}
