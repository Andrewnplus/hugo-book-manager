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

    private val newBooks get() = File(root, "new-books").apply { mkdirs() }

    private fun service() = BookHealthService(books, portal, newBooks)

    @Test
    fun `frontmatter is excluded and whitespace inside the body is kept`() {
        val f = File(root, "a.md").apply { writeText("---\ntitle: \"x\"\n---\nab cd\nef\n") }
        assertEquals(9, BookHealthService.bodyChars(f))
    }

    @Test
    fun `a horizontal rule in the body is consumed, never counted`() {
        val f = File(root, "b.md").apply { writeText("---\ntitle: \"x\"\n---\nabc\n---\ndef\n") }
        assertEquals(8, BookHealthService.bodyChars(f))
    }

    @Test
    fun `a file with no body counts zero`() {
        val f = File(root, "c.md").apply { writeText("---\ntitle: \"x\"\nweight: 1\n---\n") }
        assertEquals(0, BookHealthService.bodyChars(f))
    }

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
        val b = BookHealthService.Book("x", "t", "s", "l", chars = 1250, pages = 3)
        assertEquals(417, b.density)
    }

    @Test
    fun `a book with no docs directory is skipped entirely`() {
        File(books, "craft/engineering/coding-practice/empty/site/content").mkdirs()
        assertTrue(service().scan().isEmpty())
    }

    @Test
    fun `draft chapters are excluded, matching what Hugo actually builds`() {
        book("with-draft", "01/_index.md" to "aaa")
        File(books, "craft/engineering/coding-practice/with-draft/site/content/docs/02/_index.md").apply {
            parentFile.mkdirs()
            writeText("---\ntitle: \"x\"\ndraft: true\n---\nthis chapter is not published\n")
        }

        val found = service().scan().single()
        assertEquals(1, found.pages, "a drafted chapter is absent from the deployed site")
        assertEquals(4, found.chars)
    }

    @Test
    fun `a body line reading draft true does not make the page a draft`() {
        val f = File(root, "d.md").apply { writeText("---\ntitle: \"x\"\n---\ndraft: true\n") }
        assertTrue(!BookHealthService.isDraft(f))
    }

    @Test
    fun `flat in-progress books are scanned too, with taxonomy left blank`() {
        book("filed", "01/_index.md" to "aaa")
        File(newBooks, "in-progress/site/content/docs/01/_index.md").apply {
            parentFile.mkdirs()
            writeText("---\ntitle: \"x\"\n---\nbbbb\n")
        }

        val found = service().scan()
        assertEquals(listOf("filed", "in-progress"), found.map { it.slug })
        val wip = found.single { it.slug == "in-progress" }
        assertEquals("", wip.top)
        assertEquals(5, wip.chars)
    }

    @Test
    fun `low density outranks a healthy total when assigning a tier`() {
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

    @Test
    fun `lastWritten reflects the last commit touching chapters, not the repo`() {
        book("dated", "01/_index.md" to "aaa")
        val repo = File(books, "craft/engineering/coding-practice/dated")

        fun git(vararg args: String) =
            ProcessBuilder(listOf("git") + args)
                .directory(repo)
                .redirectErrorStream(true)
                .start()
                .waitFor()

        git("init", "-q")
        git("config", "user.email", "t@example.com")
        git("config", "user.name", "t")
        git("config", "commit.gpgsign", "false")
        git("add", "-A")
        git(
            "-c",
            "core.hooksPath=/dev/null",
            "commit",
            "-q",
            "-m",
            "chapters",
            "--date",
            "2024-03-05T10:00:00+08:00",
        )
        File(repo, "README.md").writeText("fleet-wide chore\n")
        git("add", "-A")
        git("-c", "core.hooksPath=/dev/null", "commit", "-q", "-m", "chore")

        assertEquals("2024-03-05", service().scan().single().lastWritten)
    }

    @Test
    fun `a repo without git history reports no lastWritten`() {
        book("no-git", "01/_index.md" to "aaa")
        assertEquals(null, service().scan().single().lastWritten)
    }

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
