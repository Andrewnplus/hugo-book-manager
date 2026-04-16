package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BatchBookInput
import com.nplus.bookmanager.model.BatchMetadataRequest
import com.nplus.bookmanager.model.BatchMetadataResponse
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Service for managing AI task files for Claude Code interactive processing.
 *
 * Workflow:
 * 1. CLI writes request file to ai-tasks/input/
 * 2. CLI pauses and prompts user to ask Claude Code to process
 * 3. Claude Code reads the request, processes it, and writes response to ai-tasks/output/
 * 4. User re-runs the CLI command
 * 5. CLI reads the response and continues
 */
class AiTaskService(
    baseDir: String = AI_TASKS_DIR,
) {
    companion object {
        private const val AI_TASKS_DIR = "ai-tasks"

        private const val BATCH_METADATA_REQUEST_FILE = "batch-metadata-request.json"
        private const val BATCH_METADATA_RESPONSE_FILE = "batch-metadata-response.json"

        private const val METADATA_PROMPT_FILE = "book-metadata.txt"
    }

    private val inputDir = "$baseDir/input"
    private val outputDir = "$baseDir/output"

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    // ==================== Write Request Methods ====================

    /**
     * Write a batch metadata request file for multiple books.
     * This generates both metadata and structure for each book in one request.
     * @param books List of books to process (each needs id, chineseTitle, englishTitle, tableOfContents)
     * @return The path to the request file
     */
    fun writeBatchMetadataRequest(books: List<BatchBookInput>): File {
        ensureDirectories()

        val promptFile = File(AppConfig.PROMPTS_DIR, METADATA_PROMPT_FILE)
        val request =
            BatchMetadataRequest(
                promptFile = promptFile.path,
                books = books,
            )

        val requestFile = File(inputDir, BATCH_METADATA_REQUEST_FILE)
        requestFile.writeText(json.encodeToString(request))

        return requestFile
    }

    // ==================== Read Response Methods ====================

    /**
     * Read the batch metadata response file.
     * @return Map of bookId to (GeneratedMetadata, DocsStructure) pairs
     */
    fun readBatchMetadataResponse(): Map<String, Pair<GeneratedMetadata, DocsStructure>>? {
        val response =
            readJsonResponse<BatchMetadataResponse>(BATCH_METADATA_RESPONSE_FILE, "batch metadata response")
                ?: return null
        return response.results.associate { result ->
            result.bookId to Pair(result.metadata, result.structure)
        }
    }

    /**
     * Get result for a specific book from batch response.
     */
    fun getBatchResultForBook(bookId: String): Pair<GeneratedMetadata, DocsStructure>? = readBatchMetadataResponse()?.get(bookId)

    // ==================== Task Status Methods ====================

    fun hasPendingBatchMetadataTask(): Boolean = isTaskPending(BATCH_METADATA_REQUEST_FILE, BATCH_METADATA_RESPONSE_FILE)

    fun hasCompletedBatchMetadataTask(): Boolean = isTaskCompleted(BATCH_METADATA_RESPONSE_FILE)

    // ==================== Cleanup Methods ====================

    fun clearBatchMetadataTasks() = clearTaskFiles(BATCH_METADATA_REQUEST_FILE, BATCH_METADATA_RESPONSE_FILE)

    // ==================== Private Helpers ====================

    private fun isTaskPending(
        requestFile: String,
        responseFile: String,
    ): Boolean = File(inputDir, requestFile).exists() && !File(outputDir, responseFile).exists()

    private fun isTaskCompleted(responseFile: String): Boolean = File(outputDir, responseFile).exists()

    private inline fun <reified T> readJsonResponse(
        responseFile: String,
        label: String,
    ): T? {
        val file = File(outputDir, responseFile)
        if (!file.exists()) return null
        return try {
            json.decodeFromString<T>(file.readText())
        } catch (e: Exception) {
            println("Error parsing $label: ${e.message}")
            null
        }
    }

    private fun clearTaskFiles(
        requestFile: String,
        responseFile: String,
    ) {
        File(inputDir, requestFile).delete()
        File(outputDir, responseFile).delete()
    }

    private fun ensureDirectories() {
        File(inputDir).mkdirs()
        File(outputDir).mkdirs()
    }
}
