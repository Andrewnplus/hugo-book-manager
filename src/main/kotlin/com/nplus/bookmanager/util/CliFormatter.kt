package com.nplus.bookmanager.util

object CliFormatter {
    const val DEFAULT_WIDTH = 60

    /** "====...====" 包圍標題（用於 command 起始 header） */
    fun printHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("=".repeat(width))
        println(title)
        println("=".repeat(width))
    }

    /** "────...────" 包圍標題（用於成功摘要/結尾 section） */
    fun printSectionHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("─".repeat(width))
        println(title)
        println("─".repeat(width))
    }

    /** "━━━━...━━━━" 包圍標題（用於 AI task prompt） */
    fun printTaskHeader(
        title: String,
        width: Int = DEFAULT_WIDTH,
    ) {
        println("━".repeat(width))
        println("📋 $title")
        println("━".repeat(width))
    }

    /** "━━━━...━━━━" 只印底線（用於 AI task prompt 尾端） */
    fun printTaskFooter(width: Int = DEFAULT_WIDTH) {
        println("━".repeat(width))
    }

    /** "────...────" 分隔線 */
    fun printDivider(width: Int = DEFAULT_WIDTH) {
        println("─".repeat(width))
    }

    // ==================== AI Task Prompt Helpers ====================

    /**
     * Print warning when AI task is pending and user needs to process it.
     */
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

    /**
     * Print the standard message prompting user to ask Claude Code to process.
     *
     * @param title Header title for the prompt section
     * @param taskFilePath The generated task file path
     * @param promptFile The prompt template file path
     * @param details Optional additional detail lines to display
     */
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

    /**
     * Print message for batch book metadata task.
     */
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
