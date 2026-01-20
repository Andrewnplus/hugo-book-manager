package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Service for managing AI task files for Claude Code interactive processing.
 *
 * Instead of calling an API, this service writes request files to ai-tasks/input/
 * and reads response files from ai-tasks/output/. The user can then ask Claude Code
 * to process the tasks interactively.
 *
 * Workflow:
 * 1. CLI writes request file to ai-tasks/input/
 * 2. CLI pauses and prompts user to ask Claude Code to process
 * 3. Claude Code reads the request, processes it, and writes response to ai-tasks/output/
 * 4. User re-runs the CLI command
 * 5. CLI reads the response and continues
 */
class AiTaskService {
    companion object {
        private const val AI_TASKS_DIR = "ai-tasks"
        private const val INPUT_DIR = "$AI_TASKS_DIR/input"
        private const val OUTPUT_DIR = "$AI_TASKS_DIR/output"

        private const val METADATA_REQUEST_FILE = "metadata-request.json"
        private const val METADATA_RESPONSE_FILE = "metadata-response.json"
        private const val STRUCTURE_REQUEST_FILE = "structure-request.json"
        private const val STRUCTURE_RESPONSE_FILE = "structure-response.json"
        private const val CONVERT_REQUEST_FILE = "convert-request.json"
        private const val CONVERT_RESPONSE_DIR = "converted"

        private const val PROMPTS_DIR = "templates/prompts"
        private const val METADATA_PROMPT_FILE = "book-metadata.txt"
        private const val STRUCTURE_PROMPT_FILE = "book-structure.txt"
        private const val CONVERT_PROMPT_FILE = "doc-convert.txt"
    }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    // ==================== Request Models ====================

    @Serializable
    data class MetadataRequest(
        val taskType: String = "generateMetadata",
        val promptFile: String,
        val input: MetadataInput,
    )

    @Serializable
    data class MetadataInput(
        val chineseTitle: String,
        val englishTitle: String,
    )

    @Serializable
    data class StructureRequest(
        val taskType: String = "generateDocsStructure",
        val promptFile: String,
        val input: StructureInput,
    )

    @Serializable
    data class StructureInput(
        val tableOfContents: String,
    )

    @Serializable
    data class ConvertRequest(
        val taskType: String = "convertDocument",
        val promptFile: String,
        val files: List<ConvertFileEntry>,
    )

    @Serializable
    data class ConvertFileEntry(
        val inputFile: String,
        val outputFile: String,
    )

    // ==================== Write Request Methods ====================

    /**
     * Write a metadata generation request file.
     * @return The path to the request file
     */
    fun writeMetadataRequest(input: BookInput): File {
        ensureDirectories()

        val promptFile = File(PROMPTS_DIR, METADATA_PROMPT_FILE)
        val request = MetadataRequest(
            promptFile = promptFile.path,
            input = MetadataInput(
                chineseTitle = input.chineseTitle,
                englishTitle = input.englishTitle,
            ),
        )

        val requestFile = File(INPUT_DIR, METADATA_REQUEST_FILE)
        requestFile.writeText(json.encodeToString(request))

        return requestFile
    }

    /**
     * Write a docs structure generation request file.
     * @return The path to the request file
     */
    fun writeStructureRequest(tableOfContents: String): File {
        ensureDirectories()

        val promptFile = File(PROMPTS_DIR, STRUCTURE_PROMPT_FILE)
        val request = StructureRequest(
            promptFile = promptFile.path,
            input = StructureInput(
                tableOfContents = tableOfContents,
            ),
        )

        val requestFile = File(INPUT_DIR, STRUCTURE_REQUEST_FILE)
        requestFile.writeText(json.encodeToString(request))

        return requestFile
    }

    /**
     * Write a document conversion request file for batch processing.
     * @param files List of pairs (input file path, output file path)
     * @param customPromptFile Optional custom prompt file path
     * @return The path to the request file
     */
    fun writeConvertRequest(
        files: List<Pair<String, String>>,
        customPromptFile: String? = null,
    ): File {
        ensureDirectories()

        val promptFile = customPromptFile ?: File(PROMPTS_DIR, CONVERT_PROMPT_FILE).path
        val request = ConvertRequest(
            promptFile = promptFile,
            files = files.map { (input, output) ->
                ConvertFileEntry(inputFile = input, outputFile = output)
            },
        )

        val requestFile = File(INPUT_DIR, CONVERT_REQUEST_FILE)
        requestFile.writeText(json.encodeToString(request))

        return requestFile
    }

    // ==================== Read Response Methods ====================

    /**
     * Read the metadata response file.
     * @return GeneratedMetadata if response exists and is valid, null otherwise
     */
    fun readMetadataResponse(): GeneratedMetadata? {
        val responseFile = File(OUTPUT_DIR, METADATA_RESPONSE_FILE)
        if (!responseFile.exists()) return null

        return try {
            val content = responseFile.readText()
            val jsonObj = json.parseToJsonElement(content).jsonObject

            GeneratedMetadata(
                repoName = jsonObj["repoName"]?.jsonPrimitive?.content ?: "",
                englishTitle = jsonObj["englishTitle"]?.jsonPrimitive?.content ?: "",
                chineseTitle = jsonObj["chineseTitle"]?.jsonPrimitive?.content ?: "",
                description = jsonObj["description"]?.jsonPrimitive?.content ?: "",
                topics = jsonObj["topics"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                category = jsonObj["category"]?.jsonPrimitive?.content ?: "",
            )
        } catch (e: Exception) {
            println("Error parsing metadata response: ${e.message}")
            null
        }
    }

    /**
     * Read the docs structure response file.
     * @return DocsStructure if response exists and is valid, null otherwise
     */
    fun readStructureResponse(): DocsStructure? {
        val responseFile = File(OUTPUT_DIR, STRUCTURE_RESPONSE_FILE)
        if (!responseFile.exists()) return null

        return try {
            val content = responseFile.readText()
            val jsonObj = json.parseToJsonElement(content).jsonObject

            val sections = jsonObj["sections"]?.jsonArray?.map { sectionElement ->
                val section = sectionElement.jsonObject
                val chapters = section["chapters"]?.jsonArray?.map { chapterElement ->
                    val chapter = chapterElement.jsonObject
                    DocsStructure.Chapter(
                        folderName = chapter["folderName"]?.jsonPrimitive?.content ?: "",
                        title = chapter["title"]?.jsonPrimitive?.content ?: "",
                        weight = chapter["weight"]?.jsonPrimitive?.content?.toIntOrNull() ?: 1,
                    )
                } ?: emptyList()

                DocsStructure.Section(
                    folderName = section["folderName"]?.jsonPrimitive?.content ?: "",
                    title = section["title"]?.jsonPrimitive?.content ?: "",
                    weight = section["weight"]?.jsonPrimitive?.content?.toIntOrNull() ?: 1,
                    chapters = chapters,
                )
            } ?: emptyList()

            DocsStructure(sections = sections)
        } catch (e: Exception) {
            println("Error parsing structure response: ${e.message}")
            null
        }
    }

    /**
     * Check if all converted files exist in the output directory.
     * @param expectedFiles List of expected output file paths
     * @return true if all files exist
     */
    fun hasConvertedFiles(expectedFiles: List<String>): Boolean {
        return expectedFiles.all { File(it).exists() }
    }

    // ==================== Task Status Methods ====================

    /**
     * Check if there's a pending metadata task (request exists, no response yet).
     */
    fun hasPendingMetadataTask(): Boolean {
        val requestFile = File(INPUT_DIR, METADATA_REQUEST_FILE)
        val responseFile = File(OUTPUT_DIR, METADATA_RESPONSE_FILE)
        return requestFile.exists() && !responseFile.exists()
    }

    /**
     * Check if there's a completed metadata task (both request and response exist).
     */
    fun hasCompletedMetadataTask(): Boolean {
        val responseFile = File(OUTPUT_DIR, METADATA_RESPONSE_FILE)
        return responseFile.exists()
    }

    /**
     * Check if there's a pending structure task (request exists, no response yet).
     */
    fun hasPendingStructureTask(): Boolean {
        val requestFile = File(INPUT_DIR, STRUCTURE_REQUEST_FILE)
        val responseFile = File(OUTPUT_DIR, STRUCTURE_RESPONSE_FILE)
        return requestFile.exists() && !responseFile.exists()
    }

    /**
     * Check if there's a completed structure task (both request and response exist).
     */
    fun hasCompletedStructureTask(): Boolean {
        val responseFile = File(OUTPUT_DIR, STRUCTURE_RESPONSE_FILE)
        return responseFile.exists()
    }

    /**
     * Check if there's a pending convert task.
     */
    fun hasPendingConvertTask(): Boolean {
        val requestFile = File(INPUT_DIR, CONVERT_REQUEST_FILE)
        return requestFile.exists()
    }

    /**
     * Read the convert request to get list of expected files.
     */
    fun readConvertRequest(): ConvertRequest? {
        val requestFile = File(INPUT_DIR, CONVERT_REQUEST_FILE)
        if (!requestFile.exists()) return null

        return try {
            json.decodeFromString<ConvertRequest>(requestFile.readText())
        } catch (e: Exception) {
            println("Error reading convert request: ${e.message}")
            null
        }
    }

    // ==================== Cleanup Methods ====================

    /**
     * Clear all task files (both input and output).
     */
    fun clearAllTasks() {
        clearMetadataTasks()
        clearStructureTasks()
        clearConvertTasks()
    }

    /**
     * Clear metadata task files.
     */
    fun clearMetadataTasks() {
        File(INPUT_DIR, METADATA_REQUEST_FILE).delete()
        File(OUTPUT_DIR, METADATA_RESPONSE_FILE).delete()
    }

    /**
     * Clear structure task files.
     */
    fun clearStructureTasks() {
        File(INPUT_DIR, STRUCTURE_REQUEST_FILE).delete()
        File(OUTPUT_DIR, STRUCTURE_RESPONSE_FILE).delete()
    }

    /**
     * Clear convert task files.
     */
    fun clearConvertTasks() {
        File(INPUT_DIR, CONVERT_REQUEST_FILE).delete()
    }

    // ==================== Helper Methods ====================

    private fun ensureDirectories() {
        File(INPUT_DIR).mkdirs()
        File(OUTPUT_DIR).mkdirs()
    }

    /**
     * Print the standard message prompting user to ask Claude Code to process.
     */
    fun printTaskPrompt(taskFile: File, promptFile: String) {
        println()
        println("━".repeat(60))
        println("📋 AI Task Generated")
        println("━".repeat(60))
        println()
        println("Task file: ${taskFile.path}")
        println("Prompt:    $promptFile")
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to continue.")
        println()
        println("━".repeat(60))
    }

    /**
     * Print message for batch convert task.
     */
    fun printConvertTaskPrompt(taskFile: File, promptFile: String, fileCount: Int) {
        println()
        println("━".repeat(60))
        println("📋 AI Batch Convert Task Generated")
        println("━".repeat(60))
        println()
        println("Task file: ${taskFile.path}")
        println("Prompt:    $promptFile")
        println("Files:     $fileCount file(s) to convert")
        println()
        println("👉 Please tell Claude Code: \"請處理 AI 任務\"")
        println("   Then re-run this command to continue.")
        println()
        println("━".repeat(60))
    }
}
