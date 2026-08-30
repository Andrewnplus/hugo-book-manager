package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookStatus
import com.nplus.bookmanager.model.BooksQueue
import com.nplus.bookmanager.model.QueuedBook
import org.yaml.snakeyaml.Yaml
import java.io.File

class BookInputService {
    private val yaml = Yaml()

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
                        isbn = bookData["isbn"]?.toString() ?: "",
                        errorMessage = bookData["error_message"]?.toString(),
                    )
                }

            BooksQueue(books = books)
        } catch (e: Exception) {
            println("Error reading queue file: ${e.message}")
            null
        }
    }

    fun saveBooksQueue(
        queueFile: File,
        queue: BooksQueue,
    ): Boolean =
        try {
            val originalContent = if (queueFile.exists()) queueFile.readText() else ""

            var updatedContent = originalContent
            for (book in queue.books) {
                val idPattern = """(\s*-\s+id:\s*${Regex.escape(book.id)}\s*\n\s*status:\s*)\w+""".toRegex()
                updatedContent =
                    updatedContent.replace(idPattern) { matchResult ->
                        "${matchResult.groupValues[1]}${book.status.name.lowercase()}"
                    }

                if (book.errorMessage != null) {
                    val removeErrorPattern =
                        """(\s*-\s+id:\s*${Regex.escape(book.id)}\s*\n\s*status:\s*\w+)\s*\n\s*error_message:\s*[^\n]*""".toRegex()
                    updatedContent =
                        updatedContent.replace(removeErrorPattern) { matchResult ->
                            matchResult.groupValues[1]
                        }
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
            "duplicate" -> BookStatus.DUPLICATE
            else -> BookStatus.PENDING
        }

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
        if (purchaseUrl.isBlank()) errors.add("purchase_url is required")
        if (tableOfContents.isBlank()) errors.add("table_of_contents is required")
        return errors
    }
}
