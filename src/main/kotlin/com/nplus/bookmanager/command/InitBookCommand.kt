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
import com.nplus.bookmanager.service.BookRepoService
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput
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
class InitBookCommand(
    private val bookInputService: BookInputService = BookInputService(),
    private val aiTaskService: AiTaskService = AiTaskService(),
    private val ghService: GitHubCliService = GitHubCliService(),
    private val bookRepoService: BookRepoService = BookRepoService(ghService = ghService),
) : CliktCommand(name = "init-book") {
    override fun help(context: Context) = "Initialize a new book repository from YAML input file"

    private val inputFile by option("--input", "-i", help = "Path to book input YAML file")
        .default("templates/book-input.yaml")

    private val dryRun by option("--dry-run", "-n", help = "Simulate without making changes")
        .flag(default = false)

    override fun run() {
        printHeader()

        // Check prerequisites
        if (!ghService.checkPrerequisites()) return

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
            CliFormatter.printPendingTaskWarning(
                message = "AI tasks are already pending. Please ask Claude Code to process them.",
                taskFiles =
                    listOf(
                        "ai-tasks/input/metadata-request.json",
                        "ai-tasks/input/structure-request.json",
                    ),
            )
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
        CliFormatter.printTaskHeader("AI Tasks Generated")
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
        CliFormatter.printTaskFooter()
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
        bookRepoService.printDocsStructure(docsStructure)

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
        if (!UserInput.confirm("\nProceed with creating repository?")) {
            println("Cancelled")
            return
        }

        // Create repository using shared service
        println("\nStep 4: Creating repository...")
        val result = bookRepoService.createBookRepository(metadata, input, docsStructure)

        if (result.success) {
            // Clean up task files
            println("\nCleaning up AI task files...")
            aiTaskService.clearMetadataTasks()
            aiTaskService.clearStructureTasks()
            println("  Task files cleared.")
        }
    }

    private fun printHeader() {
        CliFormatter.printHeader("Hugo Book Manager - Initialize New Book")
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
}
