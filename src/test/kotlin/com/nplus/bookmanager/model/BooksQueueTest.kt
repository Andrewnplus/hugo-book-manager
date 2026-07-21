package com.nplus.bookmanager.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * The queue state machine every `init-books` run drives. InitBooksCommand is
 * interactive orchestration and not worth mocking, but these transitions decide
 * which book gets picked up next and whether a failed run can be retried — so
 * they are pinned here instead.
 */
class BooksQueueTest {
    private fun book(
        id: String,
        status: BookStatus,
    ) = QueuedBook(
        id = id,
        chineseTitle = "書 $id",
        englishTitle = "Book $id",
        author = "Author",
        publicationDate = "2026-01-01",
        coverUrl = "https://example.com/$id.jpg",
        purchaseUrl = "https://example.com/$id",
        tableOfContents = "1. Intro",
        status = status,
    )

    private val queue =
        BooksQueue(
            listOf(
                book("done", BookStatus.COMPLETED),
                book("busy", BookStatus.PROCESSING),
                book("first", BookStatus.PENDING),
                book("second", BookStatus.PENDING),
            ),
        )

    @Test
    fun `getNextPending returns the first pending book in queue order`() {
        assertEquals("first", queue.getNextPending()?.id)
    }

    @Test
    fun `getNextPending returns null when nothing is pending`() {
        val drained = BooksQueue(listOf(book("done", BookStatus.COMPLETED)))
        assertNull(drained.getNextPending())
    }

    @Test
    fun `getProcessing finds the in-flight book that phase 2 resumes`() {
        assertEquals("busy", queue.getProcessing()?.id)
    }

    @Test
    fun `getById returns null for an unknown id`() {
        assertEquals("busy", queue.getById("busy")?.id)
        assertNull(queue.getById("nope"))
    }

    @Test
    fun `updateStatus changes only the targeted book and leaves the rest alone`() {
        val updated = queue.updateStatus("first", BookStatus.PROCESSING)

        assertEquals(BookStatus.PROCESSING, updated.getById("first")?.status)
        assertEquals(BookStatus.PENDING, updated.getById("second")?.status)
        assertEquals(BookStatus.COMPLETED, updated.getById("done")?.status)
        assertEquals(queue.books.size, updated.books.size)
    }

    @Test
    fun `updateStatus does not mutate the original queue`() {
        queue.updateStatus("first", BookStatus.ERROR, "boom")

        assertEquals(BookStatus.PENDING, queue.getById("first")?.status)
    }

    @Test
    fun `updateStatus records and then clears the error message`() {
        val failed = queue.updateStatus("first", BookStatus.ERROR, "cover download failed")
        assertEquals("cover download failed", failed.getById("first")?.errorMessage)

        // A reset back to PENDING passes no message, which must clear the old one
        // — otherwise a retried book keeps reporting a failure that no longer applies.
        val reset = failed.updateStatus("first", BookStatus.PENDING)
        assertNull(reset.getById("first")?.errorMessage)
    }

    @Test
    fun `updateStatus on an unknown id is a no-op`() {
        val unchanged = queue.updateStatus("nope", BookStatus.COMPLETED)

        assertEquals(queue.books.map { it.id to it.status }, unchanged.books.map { it.id to it.status })
    }

    @Test
    fun `summary counts books per status and omits statuses with none`() {
        val summary = queue.summary()

        assertEquals(2, summary[BookStatus.PENDING])
        assertEquals(1, summary[BookStatus.PROCESSING])
        assertEquals(1, summary[BookStatus.COMPLETED])
        assertNull(summary[BookStatus.DUPLICATE])
    }
}
