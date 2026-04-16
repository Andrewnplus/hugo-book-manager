package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.BookStatus
import com.nplus.bookmanager.model.BooksQueue
import com.nplus.bookmanager.model.QueuedBook
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Service for reading book input from YAML files
 */
class BookInputService {
    private val yaml = Yaml()

    /**
     * Load book input from a YAML file
     */
    fun loadBookInput(inputFile: File): BookInput? {
        if (!inputFile.exists()) {
            println("Error: Input file not found: ${inputFile.absolutePath}")
            return null
        }

        return try {
            val data: Map<String, Any> = inputFile.inputStream().use { yaml.load(it) }

            BookInput(
                chineseTitle = data["chinese_title"]?.toString() ?: "",
                englishTitle = data["english_title"]?.toString() ?: "",
                author = data["author"]?.toString() ?: "",
                publicationDate = data["publication_date"]?.toString() ?: "",
                coverUrl = data["cover_url"]?.toString() ?: "",
                purchaseUrl = data["purchase_url"]?.toString() ?: "",
                tableOfContents = data["table_of_contents"]?.toString() ?: "",
            )
        } catch (e: Exception) {
            println("Error reading YAML file: ${e.message}")
            null
        }
    }

    /**
     * Load books queue from a YAML file
     */
    fun loadBooksQueue(queueFile: File): BooksQueue? {
        if (!queueFile.exists()) {
            println("Error: Queue file not found: ${queueFile.absolutePath}")
            return null
        }

        return try {
            val data: Map<String, Any> = queueFile.inputStream().use { yaml.load(it) }

            @Suppress("UNCHECKED_CAST")
            val booksList = data["books"] as? List<Map<String, Any>> ?: emptyList()

            val books =
                booksList.map { bookData ->
                    QueuedBook(
                        id = bookData["id"]?.toString() ?: "",
                        status = parseStatus(bookData["status"]?.toString()),
                        chineseTitle = bookData["chinese_title"]?.toString() ?: "",
                        englishTitle = bookData["english_title"]?.toString() ?: "",
                        author = bookData["author"]?.toString() ?: "",
                        publicationDate = bookData["publication_date"]?.toString() ?: "",
                        coverUrl = bookData["cover_url"]?.toString() ?: "",
                        purchaseUrl = bookData["purchase_url"]?.toString() ?: "",
                        tableOfContents = bookData["table_of_contents"]?.toString() ?: "",
                        errorMessage = bookData["error_message"]?.toString(),
                    )
                }

            BooksQueue(books = books)
        } catch (e: Exception) {
            println("Error reading queue file: ${e.message}")
            null
        }
    }

    /**
     * Save books queue back to YAML file
     */
    fun saveBooksQueue(
        queueFile: File,
        queue: BooksQueue,
    ): Boolean =
        try {
            // Read original file to preserve comments and formatting
            val originalContent = if (queueFile.exists()) queueFile.readText() else ""

            // Update status values in the original content
            var updatedContent = originalContent
            for (book in queue.books) {
                // Find and replace the status line for each book
                // Pattern handles various YAML indentation styles: "- id:", "  - id:", "    -   id:", etc.
                val idPattern = """(\s*-\s+id:\s*${Regex.escape(book.id)}\s*\n\s*status:\s*)\w+""".toRegex()
                updatedContent =
                    updatedContent.replace(idPattern) { matchResult ->
                        "${matchResult.groupValues[1]}${book.status.name.lowercase()}"
                    }

                // Add or update error_message if present
                if (book.errorMessage != null) {
                    // First remove any existing error_message line
                    val removeErrorPattern =
                        """(\s*-\s+id:\s*${Regex.escape(book.id)}\s*\n\s*status:\s*\w+)\s*\n\s*error_message:\s*[^\n]*""".toRegex()
                    updatedContent =
                        updatedContent.replace(removeErrorPattern) { matchResult ->
                            matchResult.groupValues[1]
                        }
                    // Then inject the new error_message
                    val addErrorPattern =
                        """(\s*-\s+id:\s*${Regex.escape(book.id)}\s*\n\s*status:\s*\w+)""".toRegex()
                    updatedContent =
                        updatedContent.replace(addErrorPattern) { matchResult ->
                            "${matchResult.groupValues[1]}\n        error_message: \"${book.errorMessage}\""
                        }
                }
            }

            queueFile.writeText(updatedContent)
            true
        } catch (e: Exception) {
            println("Error saving queue file: ${e.message}")
            false
        }

    private fun parseStatus(status: String?): BookStatus =
        when (status?.lowercase()) {
            "pending" -> BookStatus.PENDING
            "processing" -> BookStatus.PROCESSING
            "completed" -> BookStatus.COMPLETED
            "error" -> BookStatus.ERROR
            else -> BookStatus.PENDING
        }

    /**
     * Validate book input has all required fields
     */
    fun validate(input: BookInput): List<String> =
        validateCommonFields(
            chineseTitle = input.chineseTitle,
            englishTitle = input.englishTitle,
            author = input.author,
            publicationDate = input.publicationDate,
            coverUrl = input.coverUrl,
            purchaseUrl = input.purchaseUrl,
            tableOfContents = input.tableOfContents,
        )

    /**
     * Validate a queued book has all required fields
     */
    fun validateQueuedBook(book: QueuedBook): List<String> {
        val errors = mutableListOf<String>()
        if (book.id.isBlank()) errors.add("id is required")
        errors.addAll(
            validateCommonFields(
                chineseTitle = book.chineseTitle,
                englishTitle = book.englishTitle,
                author = book.author,
                publicationDate = book.publicationDate,
                coverUrl = book.coverUrl,
                purchaseUrl = book.purchaseUrl,
                tableOfContents = book.tableOfContents,
            ),
        )
        return errors
    }

    private fun validateCommonFields(
        chineseTitle: String,
        englishTitle: String,
        author: String,
        publicationDate: String,
        coverUrl: String,
        purchaseUrl: String,
        tableOfContents: String,
    ): List<String> {
        val errors = mutableListOf<String>()
        if (chineseTitle.isBlank()) errors.add("chinese_title is required")
        if (englishTitle.isBlank()) errors.add("english_title is required")
        if (author.isBlank()) errors.add("author is required")
        if (publicationDate.isBlank()) errors.add("publication_date is required")
        // cover_url is optional — download will be skipped if blank
        if (purchaseUrl.isBlank()) errors.add("purchase_url is required")
        if (tableOfContents.isBlank()) errors.add("table_of_contents is required")
        return errors
    }
}
