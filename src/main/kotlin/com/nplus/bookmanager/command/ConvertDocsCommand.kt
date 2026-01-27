package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.service.AiTaskService
import java.io.File

/**
 * Command to convert cleaned Markdown documents to Hugo-book format using AI.
 *
 * This command uses a two-phase workflow:
 *
 * Phase 1 (First Run):
 * - Scan input directory for Markdown files
 * - Generate AI task file listing all files to convert
 * - Prompt user to ask Claude Code to process
 *
 * Phase 2 (Second Run - after Claude processes):
 * - Check if all expected output files exist
 * - Report success/failure for each file
 *
 * Stage 2 of document processing pipeline:
 * - Input: Directory containing cleaned Markdown files (from Stage 1)
 * - Output: Hugo-book formatted Markdown files with proper structure and summaries
 */
class ConvertDocsCommand : CliktCommand(name = "convert-docs") {
    override fun help(context: com.github.ajalt.clikt.core.Context) =
        """
        Convert cleaned Markdown documents to Hugo-book format using AI.

        This command takes Markdown files (typically from clean-docs Stage 1) and
        converts them to polished Hugo-book format with proper headings, formatting,
        and optional AI-generated summaries.

        The output files maintain the same names as input files.
        """.trimIndent()

    private val inputDir by option(
        "-i",
        "--input-dir",
        help = "Directory containing cleaned Markdown files",
    ).required()

    private val outputDir by option(
        "-o",
        "--output-dir",
        help = "Output directory for converted files (default: input_dir/converted)",
    )

    private val single by option(
        "-s",
        "--single",
        help = "Convert a single file instead of a directory",
    )

    private val customPrompt by option(
        "-p",
        "--prompt",
        help = "Path to custom prompt file (use {{content}} as placeholder)",
    )

    private val dryRun by option(
        "--dry-run",
        help = "Don't create task files, just show what would be done",
    ).flag()

    private val aiTaskService = AiTaskService()

    override fun run() {
        printHeader()

        // Load custom prompt path if specified
        val promptPath = getPromptPath()

        // Single file mode
        if (single != null) {
            processSingleFile(File(single!!), promptPath)
            return
        }

        // Directory mode
        val input = File(inputDir)
        val output =
            if (outputDir != null) {
                File(outputDir!!)
            } else {
                File(input, "converted")
            }

        println("Input directory:  ${input.absolutePath}")
        println("Output directory: ${output.absolutePath}")
        println("Mode: ${if (dryRun) "DRY RUN" else "ACTUAL"}")
        if (promptPath != null) {
            println("Using custom prompt: $promptPath")
        }
        println()

        processDirectory(input, output, promptPath)
    }

    private fun getPromptPath(): String? {
        if (customPrompt == null) return null

        val promptFile = File(customPrompt!!)
        return if (promptFile.exists()) {
            promptFile.absolutePath
        } else {
            println("Warning: Custom prompt file not found: ${promptFile.absolutePath}")
            println("Using default prompt template")
            null
        }
    }

    private fun processSingleFile(
        inputFile: File,
        customPromptPath: String?,
    ) {
        println("Processing single file: ${inputFile.absolutePath}")
        println("Mode: ${if (dryRun) "DRY RUN" else "ACTUAL"}")
        println()

        if (!inputFile.exists()) {
            println("Error: File not found: ${inputFile.absolutePath}")
            return
        }

        val outputFile =
            if (outputDir != null) {
                val dir = File(outputDir!!)
                File(dir, inputFile.name)
            } else {
                File(inputFile.parent, inputFile.nameWithoutExtension + "-converted.md")
            }

        // Check if already converted
        if (outputFile.exists()) {
            println("Output file already exists: ${outputFile.absolutePath}")
            println("Conversion appears complete.")

            // Check if there's a pending task to clear
            if (aiTaskService.hasPendingConvertTask()) {
                println("\nCleaning up task files...")
                aiTaskService.clearConvertTasks()
                println("  Task files cleared.")
            }
            return
        }

        // Check if task is pending
        if (aiTaskService.hasPendingConvertTask()) {
            println()
            println("⚠️  AI task is pending but output file not found.")
            println()
            println("   Task file:  ai-tasks/input/convert-request.json")
            println("   Expected:   ${outputFile.absolutePath}")
            println()
            println("👉 Please ask Claude Code to process the AI task.")
            println("   Then re-run this command.")
            return
        }

        if (dryRun) {
            println("Would create task to convert: ${inputFile.name}")
            println("Would expect output at: ${outputFile.absolutePath}")
            return
        }

        // Create convert task for single file
        val files = listOf(inputFile.absolutePath to outputFile.absolutePath)
        val taskFile = aiTaskService.writeConvertRequest(files, customPromptPath)

        // Print task prompt
        println()
        println("━".repeat(60))
        println("📋 AI Convert Task Generated")
        println("━".repeat(60))
        println()
        println("Task file: ${taskFile.path}")
        println("Prompt:    ${customPromptPath ?: "templates/prompts/doc-convert.txt"}")
        println()
        println("Input:  ${inputFile.absolutePath}")
        println("Output: ${outputFile.absolutePath}")
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to verify.")
        println()
        println("━".repeat(60))
    }

    private fun processDirectory(
        inputDir: File,
        outputDir: File,
        customPromptPath: String?,
    ) {
        if (!inputDir.exists() || !inputDir.isDirectory) {
            println("Error: Input directory not found: ${inputDir.absolutePath}")
            return
        }

        val files =
            inputDir
                .listFiles()
                ?.filter { file ->
                    file.name.lowercase().endsWith(".md")
                }?.sortedBy { it.name } ?: emptyList()

        if (files.isEmpty()) {
            println("No Markdown files found in: ${inputDir.absolutePath}")
            return
        }

        println("Found ${files.size} file(s)")
        println()

        // Build list of input/output pairs
        val filePairs =
            files.map { inputFile ->
                val outputFile = File(outputDir, inputFile.name)
                inputFile.absolutePath to outputFile.absolutePath
            }

        // Check how many are already converted
        val existingCount = filePairs.count { (_, output) -> File(output).exists() }
        val pendingCount = filePairs.size - existingCount

        if (existingCount > 0) {
            println("Already converted: $existingCount file(s)")
        }

        // If all files are converted, we're done
        if (pendingCount == 0) {
            println("\nAll files have been converted!")
            println("Output directory: ${outputDir.absolutePath}")

            // Clean up task files if any
            if (aiTaskService.hasPendingConvertTask()) {
                println("\nCleaning up task files...")
                aiTaskService.clearConvertTasks()
                println("  Task files cleared.")
            }
            return
        }

        // Filter to only pending files
        val pendingFiles = filePairs.filter { (_, output) -> !File(output).exists() }

        // Check if task is already pending
        if (aiTaskService.hasPendingConvertTask()) {
            println()
            println("⚠️  AI task is pending. $pendingCount file(s) still need to be converted.")
            println()
            println("   Task file: ai-tasks/input/convert-request.json")
            println()
            println("   Pending files:")
            pendingFiles.take(5).forEach { (input, _) ->
                println("     - ${File(input).name}")
            }
            if (pendingFiles.size > 5) {
                println("     ... and ${pendingFiles.size - 5} more")
            }
            println()
            println("👉 Please ask Claude Code to process the AI task.")
            println("   Then re-run this command.")
            return
        }

        if (dryRun) {
            println("Would create task to convert $pendingCount file(s):")
            pendingFiles.forEach { (input, output) ->
                println("  ${File(input).name} -> ${File(output).name}")
            }
            return
        }

        // Create output directory
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // Create convert task for all pending files
        val taskFile = aiTaskService.writeConvertRequest(pendingFiles, customPromptPath)

        // Print task prompt
        println()
        println("━".repeat(60))
        println("📋 AI Batch Convert Task Generated")
        println("━".repeat(60))
        println()
        println("Task file: ${taskFile.path}")
        println("Prompt:    ${customPromptPath ?: "templates/prompts/doc-convert.txt"}")
        println("Files:     ${pendingFiles.size} file(s) to convert")
        println()
        println("Files to convert:")
        pendingFiles.take(10).forEach { (input, output) ->
            println("  ${File(input).name} -> ${File(output).name}")
        }
        if (pendingFiles.size > 10) {
            println("  ... and ${pendingFiles.size - 10} more")
        }
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to verify.")
        println()
        println("━".repeat(60))
    }

    private fun printHeader() {
        println()
        println("=".repeat(60))
        println(" Convert Documents - Stage 2: MD -> Hugo-book MD")
        println("=".repeat(60))
        println()
    }
}
