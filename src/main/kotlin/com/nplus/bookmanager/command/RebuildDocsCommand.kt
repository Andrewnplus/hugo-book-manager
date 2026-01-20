package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.service.AiTaskService
import com.nplus.bookmanager.service.DocsStructureService
import java.io.File

/**
 * Command to rebuild the docs structure of an existing Hugo Book project.
 *
 * This command uses a two-phase workflow:
 *
 * Phase 1 (First Run):
 * 1. Validate repo directory and read table of contents
 * 2. Generate AI task file for structure generation
 * 3. Prompt user to ask Claude Code to process
 *
 * Phase 2 (Second Run - after Claude processes):
 * 1. Read AI-generated structure
 * 2. Preview changes and ask for confirmation
 * 3. Rebuild the docs folder structure
 *
 * Usage:
 *   ./gradlew rebuildDocs -PrepoDir=/path/to/book -Ptoc=/path/to/toc.txt
 *   ./gradlew rebuildDocs -PrepoDir=/path/to/book -PtocText="第一章..."
 */
class RebuildDocsCommand : CliktCommand(name = "rebuild-docs") {
    override fun help(context: Context) =
        """
        Rebuild docs structure in an existing Hugo Book project.

        Takes a table of contents and uses AI to generate the docs
        folder structure with proper _index.md files.
        """.trimIndent()

    private val repoDir by option(
        "--repo-dir",
        "-r",
        help = "Path to the Hugo Book repository",
    ).required()

    private val tocFile by option(
        "--toc",
        "-t",
        help = "Path to file containing table of contents",
    )

    private val tocText by option(
        "--toc-text",
        help = "Table of contents as text (alternative to --toc file)",
    )

    private val dryRun by option(
        "--dry-run",
        "-n",
        help = "Simulate without making changes",
    ).flag()

    private val noConfirm by option(
        "--yes",
        "-y",
        help = "Skip confirmation prompt",
    ).flag()

    private val aiTaskService = AiTaskService()
    private val docsStructureService = DocsStructureService()

    override fun run() {
        printHeader()

        // Validate repo directory
        val repo = File(repoDir)
        if (!docsStructureService.isValidHugoBookRepo(repo)) {
            println("Error: Not a valid Hugo Book repository: ${repo.absolutePath}")
            println("  Expected structure:")
            println("    site/hugo.toml")
            println("    site/content/")
            return
        }
        println("Repository: ${repo.absolutePath}")

        // Check if we have a completed structure task
        if (aiTaskService.hasCompletedStructureTask()) {
            // Phase 2: Read AI results and rebuild
            runPhase2(repo)
            return
        }

        // Phase 1: Get TOC and generate AI task
        runPhase1(repo)
    }

    /**
     * Phase 1: Get table of contents and write AI task file
     */
    private fun runPhase1(repo: File) {
        // Check if task already pending
        if (aiTaskService.hasPendingStructureTask()) {
            println()
            println("⚠️  AI task is already pending. Please ask Claude Code to process it.")
            println()
            println("   Task file: ai-tasks/input/structure-request.json")
            println()
            println("👉 Tell Claude Code: \"請處理 AI 任務\"")
            println("   Then re-run this command.")
            return
        }

        // Get table of contents
        val tableOfContents = getTableOfContents()
        if (tableOfContents == null) {
            println("Error: No table of contents provided")
            println("  Use --toc <file> or --toc-text <text>")
            return
        }

        println("\nTable of Contents:")
        println("-".repeat(40))
        tableOfContents.lines().take(20).forEach { println("  $it") }
        if (tableOfContents.lines().size > 20) {
            println("  ... (${tableOfContents.lines().size - 20} more lines)")
        }
        println("-".repeat(40))

        // Write structure request
        println("\n[Phase 1] Creating docs structure generation task...")
        val structureFile = aiTaskService.writeStructureRequest(tableOfContents)
        println("  Created: ${structureFile.path}")

        // Prompt user
        println()
        println("━".repeat(50))
        println("📋 AI Task Generated")
        println("━".repeat(50))
        println()
        println("Task file: ${structureFile.path}")
        println("Output:    ai-tasks/output/structure-response.json")
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to continue.")
        println()
        println("━".repeat(50))
    }

    /**
     * Phase 2: Read AI results and rebuild docs structure
     */
    private fun runPhase2(repo: File) {
        println("\n[Phase 2] Reading AI results and rebuilding docs...")

        // Read structure
        val docsStructure = aiTaskService.readStructureResponse()
        if (docsStructure == null) {
            println("Error: Failed to read structure response")
            println("  Please check: ai-tasks/output/structure-response.json")
            return
        }

        println("\nGenerated Structure:")
        docsStructureService.printDocsStructure(docsStructure)

        // Show what will be done
        val docsDir = docsStructureService.getDocsDir(repo)
        val existingFolders = docsDir.listFiles()?.filter { it.isDirectory } ?: emptyList()

        println("\nChanges to be made:")
        if (existingFolders.isNotEmpty()) {
            println("  Will DELETE ${existingFolders.size} existing folder(s):")
            existingFolders.forEach { println("    - ${it.name}/") }
        }
        println("  Will CREATE ${docsStructure.sections.size} section(s)")
        val totalChapters = docsStructure.sections.sumOf { maxOf(1, it.chapters.size) }
        println("  Will CREATE $totalChapters chapter folder(s)")

        if (dryRun) {
            println("\n[DRY RUN] No changes made.")
            // Clean up task files in dry-run
            println("Cleaning up task files...")
            aiTaskService.clearStructureTasks()
            println("  Task files cleared.")
            return
        }

        // Confirm before proceeding
        if (!noConfirm) {
            print("\nProceed with rebuilding docs structure? (yes/no): ")
            val confirm = readLine()?.lowercase()
            if (confirm != "yes") {
                println("Cancelled")
                return
            }
        }

        // Rebuild docs structure
        println("\nRebuilding docs structure...")
        val createdCount = docsStructureService.createDocsStructure(repo, docsStructure, clearExisting = true)

        // Clean up task files
        println("\nCleaning up AI task files...")
        aiTaskService.clearStructureTasks()
        println("  Task files cleared.")

        // Success
        println()
        println("=".repeat(50))
        println("Docs Structure Rebuilt Successfully!")
        println("=".repeat(50))
        println()
        println("  Created: $createdCount items")
        println("  Location: ${docsDir.absolutePath}")
        println()
        println("Next steps:")
        println("  1. Review the generated structure")
        println("  2. Add your notes to each chapter's _index.md")
        println("  3. Commit and push when ready")
    }

    private fun printHeader() {
        println()
        println("=".repeat(50))
        println(" Rebuild Docs Structure")
        println("=".repeat(50))
        println()
    }

    private fun getTableOfContents(): String? {
        // Priority: --toc-text > --toc file
        if (!tocText.isNullOrBlank()) {
            return tocText
        }

        if (!tocFile.isNullOrBlank()) {
            val file = File(tocFile!!)
            if (!file.exists()) {
                println("Error: TOC file not found: ${file.absolutePath}")
                return null
            }
            return file.readText()
        }

        // Interactive mode: read from stdin
        println("Enter table of contents (end with empty line):")
        val lines = mutableListOf<String>()
        while (true) {
            val line = readLine() ?: break
            if (line.isBlank()) break
            lines.add(line)
        }

        return if (lines.isEmpty()) null else lines.joinToString("\n")
    }
}
