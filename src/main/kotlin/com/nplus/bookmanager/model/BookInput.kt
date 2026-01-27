package com.nplus.bookmanager.model

/**
 * Status for books in the queue
 */
enum class BookStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    ERROR,
}

/**
 * Data class representing book input from YAML file
 */
data class BookInput(
    val chineseTitle: String,
    val englishTitle: String,
    val author: String,
    val publicationDate: String,
    val coverUrl: String,
    val purchaseUrl: String,
    val tableOfContents: String,
)

/**
 * Data class representing a book entry in the queue with status tracking
 */
data class QueuedBook(
    val id: String,
    val status: BookStatus = BookStatus.PENDING,
    val chineseTitle: String,
    val englishTitle: String,
    val author: String,
    val publicationDate: String,
    val coverUrl: String,
    val purchaseUrl: String,
    val tableOfContents: String,
    val errorMessage: String? = null,
) {
    /**
     * Convert to BookInput for compatibility with existing services
     */
    fun toBookInput(): BookInput =
        BookInput(
            chineseTitle = chineseTitle,
            englishTitle = englishTitle,
            author = author,
            publicationDate = publicationDate,
            coverUrl = coverUrl,
            purchaseUrl = purchaseUrl,
            tableOfContents = tableOfContents,
        )
}

/**
 * Data class representing the books queue file
 */
data class BooksQueue(
    val books: List<QueuedBook>,
) {
    fun getNextPending(): QueuedBook? = books.firstOrNull { it.status == BookStatus.PENDING }

    fun getProcessing(): QueuedBook? = books.firstOrNull { it.status == BookStatus.PROCESSING }

    fun getById(id: String): QueuedBook? = books.firstOrNull { it.id == id }

    fun updateStatus(
        id: String,
        newStatus: BookStatus,
        errorMessage: String? = null,
    ): BooksQueue {
        val updatedBooks =
            books.map { book ->
                if (book.id == id) {
                    book.copy(status = newStatus, errorMessage = errorMessage)
                } else {
                    book
                }
            }
        return copy(books = updatedBooks)
    }

    fun summary(): Map<BookStatus, Int> = books.groupingBy { it.status }.eachCount()
}

/**
 * Data class representing AI-generated book metadata
 */
data class GeneratedMetadata(
    val repoName: String,
    val englishTitle: String,
    val chineseTitle: String,
    val description: String,
    val topics: List<String>,
    val category: String,
)

/**
 * Data class representing AI-generated docs structure
 */
data class DocsStructure(
    val sections: List<Section>,
) {
    data class Section(
        val folderName: String,
        val title: String,
        val weight: Int,
        val chapters: List<Chapter> = emptyList(),
    )

    data class Chapter(
        val folderName: String,
        val title: String,
        val weight: Int,
    )
}
