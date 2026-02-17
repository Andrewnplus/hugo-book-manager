package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.BookStatus
import com.nplus.bookmanager.model.QueuedBook
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookInputServiceTest {
    @TempDir
    lateinit var tempDir: File

    private val service = BookInputService()

    // ==================== validate() Tests ====================

    @Test
    fun `validate returns empty list for valid input`() {
        val input = createValidBookInput()
        val errors = service.validate(input)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validate returns errors for blank fields`() {
        val input =
            BookInput(
                chineseTitle = "",
                englishTitle = "",
                author = "",
                publicationDate = "",
                coverUrl = "",
                purchaseUrl = "",
                tableOfContents = "",
            )
        val errors = service.validate(input)
        assertEquals(7, errors.size)
        assertTrue(errors.any { it.contains("chinese_title") })
        assertTrue(errors.any { it.contains("english_title") })
        assertTrue(errors.any { it.contains("author") })
    }

    @Test
    fun `validate detects single blank field`() {
        val input = createValidBookInput().copy(chineseTitle = "")
        val errors = service.validate(input)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("chinese_title"))
    }

    // ==================== validateQueuedBook() Tests ====================

    @Test
    fun `validateQueuedBook returns empty list for valid book`() {
        val book = createValidQueuedBook()
        val errors = service.validateQueuedBook(book)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateQueuedBook returns error for blank id`() {
        val book = createValidQueuedBook().copy(id = "")
        val errors = service.validateQueuedBook(book)
        assertTrue(errors.any { it.contains("id") })
    }

    @Test
    fun `validateQueuedBook returns all errors for completely empty book`() {
        val book =
            QueuedBook(
                id = "",
                chineseTitle = "",
                englishTitle = "",
                author = "",
                publicationDate = "",
                coverUrl = "",
                purchaseUrl = "",
                tableOfContents = "",
            )
        val errors = service.validateQueuedBook(book)
        assertEquals(8, errors.size) // 7 common + 1 id
    }

    // ==================== loadBookInput() Tests ====================

    @Test
    fun `loadBookInput returns null for non-existent file`() {
        val result = service.loadBookInput(File(tempDir, "nonexistent.yaml"))
        assertNull(result)
    }

    @Test
    fun `loadBookInput parses valid YAML`() {
        val yamlFile = File(tempDir, "book.yaml")
        yamlFile.writeText(
            """
            chinese_title: "測試書籍"
            english_title: "Test Book"
            author: "Author Name"
            publication_date: "2024-01-01"
            cover_url: "https://example.com/cover.png"
            purchase_url: "https://example.com/buy"
            table_of_contents: |
              Chapter 1: Introduction
              Chapter 2: Basics
            """.trimIndent(),
        )

        val result = service.loadBookInput(yamlFile)
        assertNotNull(result)
        assertEquals("測試書籍", result.chineseTitle)
        assertEquals("Test Book", result.englishTitle)
        assertEquals("Author Name", result.author)
        assertEquals("2024-01-01", result.publicationDate)
    }

    @Test
    fun `loadBookInput handles missing fields gracefully`() {
        val yamlFile = File(tempDir, "partial.yaml")
        yamlFile.writeText(
            """
            chinese_title: "測試"
            english_title: "Test"
            """.trimIndent(),
        )

        val result = service.loadBookInput(yamlFile)
        assertNotNull(result)
        assertEquals("測試", result.chineseTitle)
        assertEquals("", result.author)
    }

    // ==================== loadBooksQueue() Tests ====================

    @Test
    fun `loadBooksQueue returns null for non-existent file`() {
        val result = service.loadBooksQueue(File(tempDir, "nonexistent.yaml"))
        assertNull(result)
    }

    @Test
    fun `loadBooksQueue parses valid queue YAML`() {
        val queueFile = File(tempDir, "queue.yaml")
        queueFile.writeText(
            """
            books:
              - id: book-1
                status: pending
                chinese_title: "書籍一"
                english_title: "Book One"
                author: "Author 1"
                publication_date: "2024-01-01"
                cover_url: "https://example.com/1.png"
                purchase_url: "https://example.com/1"
                table_of_contents: "Ch 1"
              - id: book-2
                status: completed
                chinese_title: "書籍二"
                english_title: "Book Two"
                author: "Author 2"
                publication_date: "2024-02-01"
                cover_url: "https://example.com/2.png"
                purchase_url: "https://example.com/2"
                table_of_contents: "Ch 1"
            """.trimIndent(),
        )

        val result = service.loadBooksQueue(queueFile)
        assertNotNull(result)
        assertEquals(2, result.books.size)
        assertEquals("book-1", result.books[0].id)
        assertEquals(BookStatus.PENDING, result.books[0].status)
        assertEquals("book-2", result.books[1].id)
        assertEquals(BookStatus.COMPLETED, result.books[1].status)
    }

    @Test
    fun `loadBooksQueue defaults status to pending for unknown values`() {
        val queueFile = File(tempDir, "queue.yaml")
        queueFile.writeText(
            """
            books:
              - id: book-1
                status: unknown_status
                chinese_title: "書籍"
                english_title: "Book"
                author: "Author"
                publication_date: "2024-01-01"
                cover_url: "https://example.com/cover.png"
                purchase_url: "https://example.com/buy"
                table_of_contents: "Ch 1"
            """.trimIndent(),
        )

        val result = service.loadBooksQueue(queueFile)
        assertNotNull(result)
        assertEquals(BookStatus.PENDING, result.books[0].status)
    }

    // ==================== Helper Methods ====================

    private fun createValidBookInput() =
        BookInput(
            chineseTitle = "測試書籍",
            englishTitle = "Test Book",
            author = "Author",
            publicationDate = "2024-01-01",
            coverUrl = "https://example.com/cover.png",
            purchaseUrl = "https://example.com/buy",
            tableOfContents = "Chapter 1",
        )

    private fun createValidQueuedBook() =
        QueuedBook(
            id = "test-book",
            chineseTitle = "測試書籍",
            englishTitle = "Test Book",
            author = "Author",
            publicationDate = "2024-01-01",
            coverUrl = "https://example.com/cover.png",
            purchaseUrl = "https://example.com/buy",
            tableOfContents = "Chapter 1",
        )
}
