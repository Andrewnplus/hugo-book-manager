package com.nplus.bookmanager.util

object CliFormatter {
    const val DEFAULT_WIDTH = 60

    fun printHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("=".repeat(width))
        println(title)
        println("=".repeat(width))
    }

    fun printSectionHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("─".repeat(width))
        println(title)
        println("─".repeat(width))
    }

    fun printTaskHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("━".repeat(width))
        println("📋 $title")
        println("━".repeat(width))
    }

    fun printTaskFooter(width: Int = DEFAULT_WIDTH) {
        println("━".repeat(width))
    }

    fun printDivider(width: Int = DEFAULT_WIDTH) {
        println("─".repeat(width))
    }

    fun printPendingTaskWarning(
        message: String,
        taskFiles: List<String> = emptyList(),
        details: List<String> = emptyList(),
    ) {
        println()
        println("⚠️  $message")
        println()
        if (taskFiles.isNotEmpty()) {
            if (taskFiles.size == 1) {
                println("   Task file: ${taskFiles[0]}")
            } else {
                println("   Task files:")
                taskFiles.forEach { println("   - $it") }
            }
            println()
        }
        for (detail in details) {
            println(detail)
        }
        println("👉 Tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command.")
    }

    fun printTaskPrompt(
        title: String = "AI Task Generated",
        taskFilePath: String,
        promptFile: String,
        details: List<String> = emptyList(),
    ) {
        println()
        printTaskHeader(title)
        println()
        println("Task file: $taskFilePath")
        println("Prompt:    $promptFile")
        for (detail in details) {
            println(detail)
        }
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to continue.")
        println()
        printTaskFooter()
    }

    fun printBatchMetadataTaskPrompt(
        taskFilePath: String,
        promptFile: String,
        bookCount: Int,
        bookTitles: List<String>,
    ) {
        val details =
            buildList {
                add("Books:     $bookCount book(s) to process")
                bookTitles.forEachIndexed { index, title ->
                    add("           ${index + 1}. $title")
                }
            }
        printTaskPrompt(
            title = "AI Batch Book Metadata Task Generated",
            taskFilePath = taskFilePath,
            promptFile = promptFile,
            details = details,
        )
    }
}
