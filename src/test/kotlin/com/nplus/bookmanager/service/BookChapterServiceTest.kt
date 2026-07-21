package com.nplus.bookmanager.service

import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookChapterServiceTest {
    @TempDir
    lateinit var booksDir: File

    /** Create `<booksDir>/<path>/site/content/docs` plus the given chapter dirs. */
    private fun makeRepo(
        path: String,
        vararg chapterDirs: String,
    ): File {
        val repoDir = File(booksDir, path)
        val docs = File(repoDir, "site/content/docs")
        docs.mkdirs()
        File(docs, "_index.md").writeText("---\ntitle: \"Docs\"\n---\n")
        for (dir in chapterDirs) {
            val d = File(docs, dir)
            d.mkdirs()
            File(d, "_index.md").writeText("---\ntitle: \"$dir\"\n---\n\nbody\n")
        }
        return repoDir
    }

    // ==================== findRepoDir() ====================

    @Test
    fun `findRepoDir locates a repo nested in the top-sub-leaf tree`() {
        makeRepo("top-work/sub-focus/deep-work", "01-intro")

        val found = BookChapterService(booksDir).findRepoDir("deep-work")

        assertEquals("deep-work", found?.name)
    }

    @Test
    fun `findRepoDir ignores a same-named dir without a docs tree`() {
        File(booksDir, "top-work/deep-work").mkdirs()

        assertNull(BookChapterService(booksDir).findRepoDir("deep-work"))
    }

    // ==================== chapterFiles() ====================

    @Test
    fun `chapterFiles returns flat chapters and skips the docs root index`() {
        val repo = makeRepo("deep-work", "01-intro", "02-rules")

        val files = BookChapterService(booksDir).chapterFiles(repo)

        assertEquals(listOf("01-intro", "02-rules"), files.map { it.parentFile.name })
    }

    @Test
    fun `chapterFiles returns only the deepest index in a part-chapter book`() {
        val repo = makeRepo("ddia", "01-part-one", "01-part-one/01-reliable", "01-part-one/02-models")
        val service = BookChapterService(booksDir)

        val files = service.chapterFiles(repo)

        // The part's own _index.md is a container, not a chapter.
        assertEquals(listOf("01-reliable", "02-models"), files.map { it.parentFile.name })
        assertEquals(
            listOf("01-part-one/01-reliable", "01-part-one/02-models"),
            files.map { service.chapterKey(repo, it) },
        )
    }

    @Test
    fun `chapterFiles returns empty when there is no docs tree`() {
        val repo = File(booksDir, "not-a-book").apply { mkdirs() }

        assertTrue(BookChapterService(booksDir).chapterFiles(repo).isEmpty())
    }

    // ==================== markRead() ====================

    @Test
    fun `markRead appends read and readAt while keeping the body`() {
        val repo = makeRepo("deep-work", "01-intro")
        val chapter = File(repo, "site/content/docs/01-intro/_index.md")

        BookChapterService(booksDir).markRead(chapter, LocalDate.parse("2026-07-21"))

        val text = chapter.readText()
        assertContains(text, "title: \"01-intro\"")
        assertContains(text, "read: true")
        assertContains(text, "readAt: \"2026-07-21\"")
        assertContains(text, "body")
    }

    @Test
    fun `markRead replaces existing read values instead of duplicating them`() {
        val repo = makeRepo("deep-work", "01-intro")
        val chapter = File(repo, "site/content/docs/01-intro/_index.md")
        val service = BookChapterService(booksDir)

        service.markRead(chapter, LocalDate.parse("2026-01-01"))
        service.markRead(chapter, LocalDate.parse("2026-07-21"))

        val text = chapter.readText()
        assertEquals(1, text.lines().count { it.startsWith("read:") })
        assertEquals(1, text.lines().count { it.startsWith("readAt:") })
        assertContains(text, "readAt: \"2026-07-21\"")
    }

    @Test
    fun `markRead rejects a file without frontmatter`() {
        val file = File(booksDir, "plain.md").apply { writeText("no frontmatter here\n") }

        assertFailsWith<IllegalArgumentException> {
            BookChapterService(booksDir).markRead(file)
        }
    }
}
