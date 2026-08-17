package com.nplus.bookmanager.service

import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookHealthServiceTest {
    @TempDir
    lateinit var root: File

    private val books get() = File(root, "books").apply { mkdirs() }
    private val portal get() = File(root, "portal").apply { mkdirs() }

    private fun book(
        slug: String,
        vararg pages: Pair<String, String>,
        top: String = "craft",
        sub: String = "engineering",
        leaf: String = "coding-practice",
    ) {
        for ((path, body) in pages) {
            File(books, "$top/$sub/$leaf/$slug/site/content/docs/$path").apply {
                parentFile.mkdirs()
                writeText("---\ntitle: \"x\"\nweight: 1\n---\n$body")
            }
        }
    }

    private fun service() = BookHealthService(books, portal)

    // ==================== bodyChars() ====================

    @Test
    fun `frontmatter is excluded and whitespace inside the body is kept`() {
        val f = File(root, "a.md").apply { writeText("---\ntitle: \"x\"\n---\nab cd\nef\n") }
        // "ab cd" + newline, "ef" + newline
        assertEquals(9, BookHealthService.bodyChars(f))
    }

    @Test
    fun `a horizontal rule in the body is consumed, never counted`() {
        // The reference implementation treats every `---` line as a fence, so a
        // body rule is skipped rather than measured; matching that is what keeps
        // the numbers comparable with the existing re-summary list.
        val f = File(root, "b.md").apply { writeText("---\ntitle: \"x\"\n---\nabc\n---\ndef\n") }
        assertEquals(8, BookHealthService.bodyChars(f))
    }

    @Test
    fun `a file with no body counts zero`() {
        val f = File(root, "c.md").apply { writeText("---\ntitle: \"x\"\nweight: 1\n---\n") }
        assertEquals(0, BookHealthService.bodyChars(f))
    }

    // ==================== scan() ====================

    @Test
    fun `pages are counted across nested sections and the home page is ignored`() {
        book("deep-work", "01-intro/_index.md" to "aaa", "02-part/01-ch/_index.md" to "bbbb")
        File(books, "craft/engineering/coding-practice/deep-work/site/content/_index.md")
            .writeText("---\ntitle: \"x\"\n---\nthis home page must not be counted\n")

        val found = service().scan().single()
        assertEquals(2, found.pages)
        assertEquals(4 + 5, found.chars)
        assertEquals("deep-work", found.slug)
        assertEquals("craft", found.top)
    }

    @Test
    fun `density rounds rather than truncates`() {
        // The published list shows 1,250 chars over 3 pages as 417, not 416.
        val b = BookHealthService.Book("x", "t", "s", "l", chars = 1250, pages = 3)
        assertEquals(417, b.density)
    }

    @Test
    fun `a book with no docs directory is skipped entirely`() {
        File(books, "craft/engineering/coding-practice/empty/site/content").mkdirs()
        assertTrue(service().scan().isEmpty())
    }

    // ==================== tier ====================

    @Test
    fun `low density outranks a healthy total when assigning a tier`() {
        // 57 chapters averaging 351 chars was the real case: 20k total looks
        // fine until it is spread over the chapters that actually hold it.
        val b = BookHealthService.Book("x", "t", "s", "l", chars = 20_000, pages = 100)
        assertEquals("near-empty", b.tier)
    }

    @Test
    fun `tiers step through thin, watch and ok by total characters`() {
        fun tierOf(chars: Int) = BookHealthService.Book("x", "t", "s", "l", chars, pages = 1).tier
        assertEquals("thin", tierOf(7_999))
        assertEquals("watch", tierOf(8_000))
        assertEquals("watch", tierOf(14_999))
        assertEquals("ok", tierOf(15_000))
    }

    // ==================== save() ====================

    @Test
    fun `the artifact is marked derived and lists books in a stable order`() {
        book("zeta", "01/_index.md" to "aaaa")
        book("alpha", "01/_index.md" to "bbbb")

        val svc = service()
        svc.save(svc.scan())

        val json = svc.healthFile.readText()
        assertTrue(json.contains("DERIVED"), "consumers must be able to tell this is generated")
        assertTrue(json.indexOf("\"alpha\"") < json.indexOf("\"zeta\""), "stable order keeps git diffs readable")
        assertTrue(json.contains("\"thresholds\""))
    }
}
