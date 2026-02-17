package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BatchBookInput
import com.nplus.bookmanager.model.BatchMetadataRequest
import com.nplus.bookmanager.model.BatchMetadataResponse
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.model.MetadataInput
import com.nplus.bookmanager.model.MetadataRequest
import com.nplus.bookmanager.model.StructureInput
import com.nplus.bookmanager.model.StructureRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
        private const val BATCH_METADATA_REQUEST_FILE = "batch-metadata-request.json"
        private const val BATCH_METADATA_RESPONSE_FILE = "batch-metadata-response.json"

        private const val PROMPTS_DIR = "templates/prompts"
        private const val METADATA_PROMPT_FILE = "book-metadata.txt"
        private const val STRUCTURE_PROMPT_FILE = "book-structure.txt"
    }

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    // ==================== Write Request Methods ====================

    /**
     * Write a metadata generation request file.
     * @return The path to the request file
     */
    fun writeMetadataRequest(input: com.nplus.bookmanager.model.BookInput): File {
        ensureDirectories()

        val promptFile = File(PROMPTS_DIR, METADATA_PROMPT_FILE)
        val request =
            MetadataRequest(
                promptFile = promptFile.path,
                input =
                    MetadataInput(
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
        val request =
            StructureRequest(
                promptFile = promptFile.path,
                input =
                    StructureInput(
                        tableOfContents = tableOfContents,
                    ),
            )

        val requestFile = File(INPUT_DIR, STRUCTURE_REQUEST_FILE)
        requestFile.writeText(json.encodeToString(request))

        return requestFile
    }

    /**
     * Write a batch metadata request file for multiple books.
     * This generates both metadata and structure for each book in one request.
     * @param books List of books to process (each needs id, chineseTitle, englishTitle, tableOfContents)
     * @return The path to the request file
     */
    fun writeBatchMetadataRequest(books: List<BatchBookInput>): File {
        ensureDirectories()

        val promptFile = File(PROMPTS_DIR, METADATA_PROMPT_FILE)
        val request =
            BatchMetadataRequest(
                promptFile = promptFile.path,
                books = books,
            )

        val requestFile = File(INPUT_DIR, BATCH_METADATA_REQUEST_FILE)
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
            json.decodeFromString<GeneratedMetadata>(responseFile.readText())
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
            json.decodeFromString<DocsStructure>(responseFile.readText())
        } catch (e: Exception) {
            println("Error parsing structure response: ${e.message}")
            null
        }
    }

    /**
     * Read the batch metadata response file.
     * @return Map of bookId to (GeneratedMetadata, DocsStructure) pairs
     */
    fun readBatchMetadataResponse(): Map<String, Pair<GeneratedMetadata, DocsStructure>>? {
        val responseFile = File(OUTPUT_DIR, BATCH_METADATA_RESPONSE_FILE)
        if (!responseFile.exists()) return null

        return try {
            val response = json.decodeFromString<BatchMetadataResponse>(responseFile.readText())
            response.results.associate { result ->
                result.bookId to Pair(result.metadata, result.structure)
            }
        } catch (e: Exception) {
            println("Error parsing batch metadata response: ${e.message}")
            null
        }
    }

    /**
     * Get result for a specific book from batch response.
     */
    fun getBatchResultForBook(bookId: String): Pair<GeneratedMetadata, DocsStructure>? = readBatchMetadataResponse()?.get(bookId)

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
     * Check if there's a pending batch metadata task.
     */
    fun hasPendingBatchMetadataTask(): Boolean {
        val requestFile = File(INPUT_DIR, BATCH_METADATA_REQUEST_FILE)
        val responseFile = File(OUTPUT_DIR, BATCH_METADATA_RESPONSE_FILE)
        return requestFile.exists() && !responseFile.exists()
    }

    /**
     * Check if there's a completed batch metadata task.
     */
    fun hasCompletedBatchMetadataTask(): Boolean {
        val responseFile = File(OUTPUT_DIR, BATCH_METADATA_RESPONSE_FILE)
        return responseFile.exists()
    }

    /**
     * Read the batch metadata request to see which books are being processed.
     */
    fun readBatchMetadataRequest(): BatchMetadataRequest? {
        val requestFile = File(INPUT_DIR, BATCH_METADATA_REQUEST_FILE)
        if (!requestFile.exists()) return null

        return try {
            json.decodeFromString<BatchMetadataRequest>(requestFile.readText())
        } catch (e: Exception) {
            println("Error reading batch metadata request: ${e.message}")
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
        clearBatchMetadataTasks()
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
     * Clear batch metadata task files.
     */
    fun clearBatchMetadataTasks() {
        File(INPUT_DIR, BATCH_METADATA_REQUEST_FILE).delete()
        File(OUTPUT_DIR, BATCH_METADATA_RESPONSE_FILE).delete()
    }

    // ==================== Helper Methods ====================

    private fun ensureDirectories() {
        File(INPUT_DIR).mkdirs()
        File(OUTPUT_DIR).mkdirs()
    }
}
