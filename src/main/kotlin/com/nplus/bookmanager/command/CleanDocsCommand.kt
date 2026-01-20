package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.service.DocumentCleanerService
import java.io.File

/**
 * Command to clean HTML/MHTML documents and convert them to Markdown.
 *
 * Stage 1 of document processing pipeline:
 * - Input: Directory containing HTML/MHTML files
 * - Output: Cleaned Markdown files suitable for Gemini processing
 */
class CleanDocsCommand : CliktCommand(name = "clean-docs") {
    override fun help(context: com.github.ajalt.clikt.core.Context) =
        """
        Clean HTML/MHTML documents and convert to Markdown.

        This command takes a directory of HTML or MHTML files and converts them
        to clean Markdown format, ready for further processing with Gemini.

        Supported input formats: .html, .htm, .mhtml
        """.trimIndent()

    private val inputDir by option(
        "-i",
        "--input-dir",
        help = "Directory containing HTML/MHTML files to clean",
    ).required()

    private val outputDir by option(
        "-o",
        "--output-dir",
        help = "Output directory for cleaned Markdown files (default: input_dir/cleaned)",
    )

    private val single by option(
        "-s",
        "--single",
        help = "Clean a single file instead of a directory",
    )

    private val dryRun by option(
        "--dry-run",
        help = "Don't write files, just show what would be done",
    ).flag()

    override fun run() {
        printHeader()

        val cleanerService = DocumentCleanerService()

        // Single file mode
        if (single != null) {
            processSingleFile(cleanerService, File(single!!))
            return
        }

        // Directory mode
        val input = File(inputDir)
        val output =
            if (outputDir != null) {
                File(outputDir!!)
            } else {
                File(input, "cleaned")
            }

        println("Input directory:  ${input.absolutePath}")
        println("Output directory: ${output.absolutePath}")
        println("Mode: ${if (dryRun) "DRY RUN" else "ACTUAL"}")
        println()

        val count = cleanerService.cleanDirectory(input, output, dryRun)

        println()
        if (dryRun) {
            println("Dry run complete. Would process $count file(s).")
        } else {
            println("Done! Processed $count file(s).")
        }
    }

    private fun processSingleFile(
        cleanerService: DocumentCleanerService,
        inputFile: File,
    ) {
        println("Processing single file: ${inputFile.absolutePath}")
        println("Mode: ${if (dryRun) "DRY RUN" else "ACTUAL"}")
        println()

        val output =
            if (outputDir != null) {
                File(outputDir!!, inputFile.nameWithoutExtension + ".md")
            } else {
                File(inputFile.parent, inputFile.nameWithoutExtension + ".md")
            }

        if (dryRun) {
            println("Would create: ${output.absolutePath}")
            return
        }

        val markdown = cleanerService.cleanDocument(inputFile)
        if (markdown != null) {
            output.writeText(markdown)
            println("Created: ${output.absolutePath}")
            println()
            println("Preview (first 500 chars):")
            println("-".repeat(40))
            println(markdown.take(500))
            if (markdown.length > 500) println("...")
        } else {
            println("Error: Failed to clean document")
        }
    }

    private fun printHeader() {
        println()
        println("=".repeat(50))
        println(" Clean Documents - Stage 1: HTML/MHTML -> Markdown")
        println("=".repeat(50))
        println()
    }
}
