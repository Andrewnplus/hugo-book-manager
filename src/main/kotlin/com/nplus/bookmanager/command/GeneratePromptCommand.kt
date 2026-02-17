package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.util.CliFormatter
import java.io.File

/**
 * Command to generate/display prompt templates for book project tasks.
 *
 * Usage:
 *   ./gradlew generatePrompt --list
 *   ./gradlew generatePrompt -Ptype=restructure-folders
 *   ./gradlew generatePrompt -Ptype=enhance-katex -Poutput=/path/to/book/PROMPT.md
 */
class GeneratePromptCommand : CliktCommand(name = "generate-prompt") {
    override fun help(context: Context) = "Generate prompt templates for book project tasks"

    private val promptType by option(
        "--type",
        "-t",
        help =
            "Prompt type: restructure-folders, enhance-katex, enhance-mermaid, " +
                "review-markdown, list-to-table, simplify-table, extract-insights",
    )

    private val outputFile by option(
        "--output",
        "-o",
        help = "Output file path (default: print to console)",
    )

    private val listPrompts by option(
        "--list",
        "-l",
        help = "List available prompt types",
    ).flag()

    companion object {
        private val PROMPT_TYPES =
            mapOf(
                "restructure-folders" to
                    PromptInfo(
                        file = "restructure-folders.txt",
                        description = "建立資料夾結構：根據目錄建立 Hugo Book 資料夾和 _index.md 檔案",
                    ),
                "enhance-katex" to
                    PromptInfo(
                        file = "enhance-markdown-katex.txt",
                        description = "KaTeX 數學公式增強：將數學內容轉換為 KaTeX 格式",
                    ),
                "enhance-mermaid" to
                    PromptInfo(
                        file = "enhance-markdown-mermaid.txt",
                        description = "Mermaid 圖表增強：將流程、關係等內容轉換為 Mermaid 圖表",
                    ),
                "review-markdown" to
                    PromptInfo(
                        file = "review-markdown.txt",
                        description = "Markdown 審查：審查並統一所有 _index.md 的格式風格",
                    ),
                "list-to-table" to
                    PromptInfo(
                        file = "convert-list-to-table.txt",
                        description = "條列轉表格：分析條列式內容，將適合的轉換為 Markdown 表格",
                    ),
                "simplify-table" to
                    PromptInfo(
                        file = "simplify-table.txt",
                        description = "精簡表格：移除表格中的冗餘資訊，只保留對主題最核心的內容",
                    ),
                "extract-insights" to
                    PromptInfo(
                        file = "extract-insights.txt",
                        description = "抽取洞見：從書籍筆記中萃取值得收藏的精華句子或想法",
                    ),
                "add-books-to-queue" to
                    PromptInfo(
                        file = "add-books-to-queue.txt",
                        description = "加入書籍：將新書籍資訊加入待處理佇列",
                    ),
                "refine-notes" to
                    PromptInfo(
                        file = "refine-notes.txt",
                        description = "精煉筆記：改善讀書筆記的結構與可讀性",
                    ),
                "extract-pdf-figures" to
                    PromptInfo(
                        file = "extract-pdf-figures.txt",
                        description = "擷取圖表：從 PDF 中擷取圖表並轉換為 Markdown",
                    ),
            )

        data class PromptInfo(
            val file: String,
            val description: String,
        )
    }

    override fun run() {
        if (listPrompts || promptType == null) {
            printAvailablePrompts()
            return
        }

        val promptInfo = PROMPT_TYPES[promptType]
        if (promptInfo == null) {
            println("Error: Unknown prompt type '$promptType'")
            println()
            printAvailablePrompts()
            return
        }

        val promptFile = File(AppConfig.PROMPTS_DIR, promptInfo.file)
        if (!promptFile.exists()) {
            println("Error: Prompt file not found: ${promptFile.absolutePath}")
            return
        }

        val content = promptFile.readText()

        if (outputFile != null) {
            // Write to file
            val outFile = File(outputFile!!)
            outFile.parentFile?.mkdirs()
            outFile.writeText(content)
            println("Prompt saved to: ${outFile.absolutePath}")
            println()
            println("Usage:")
            println("  1. Open the target book project in Claude Code")
            println("  2. Copy the content of ${outFile.name} and paste to Claude")
            println("  3. Follow the instructions in the prompt")
        } else {
            // Print to console
            printPromptHeader(promptType!!, promptInfo.description)
            println()
            println(content)
            println()
            printPromptFooter()
        }
    }

    private fun printAvailablePrompts() {
        println()
        CliFormatter.printSectionHeader("Available Prompt Types")
        println()

        PROMPT_TYPES.forEach { (type, info) ->
            println("  $type")
            println("    ${info.description}")
            println()
        }

        // Scan for unregistered prompt files
        val registeredFiles = PROMPT_TYPES.values.map { it.file }.toSet()
        val promptsDir = File(AppConfig.PROMPTS_DIR)
        if (promptsDir.exists()) {
            val unregistered =
                promptsDir
                    .listFiles { f -> f.extension == "txt" && f.name !in registeredFiles }
                    ?.sorted() ?: emptyList()
            if (unregistered.isNotEmpty()) {
                println("  Unregistered prompt files:")
                unregistered.forEach { file ->
                    println("    ${file.nameWithoutExtension} (unregistered)")
                }
                println()
            }
        }

        println("Usage:")
        println("  ./gradlew generatePrompt -Ptype=<type>")
        println("  ./gradlew generatePrompt -Ptype=<type> -Poutput=/path/to/file.md")
        println()
    }

    private fun printPromptHeader(
        type: String,
        description: String,
    ) {
        println()
        CliFormatter.printTaskHeader("Prompt: $type")
        println()
        println("描述: $description")
        println()
        println("使用方式:")
        println("  1. 複製以下內容")
        println("  2. 開啟目標書籍專案的 Claude Code")
        println("  3. 貼上 Prompt 並執行")
        println()
        CliFormatter.printDivider()
    }

    private fun printPromptFooter() {
        CliFormatter.printDivider()
        println()
        println("提示：可使用 -o 參數將 Prompt 儲存到檔案：")
        println("  ./gradlew generatePrompt -Ptype=$promptType -Poutput=PROMPT.md")
        println()
    }
}
