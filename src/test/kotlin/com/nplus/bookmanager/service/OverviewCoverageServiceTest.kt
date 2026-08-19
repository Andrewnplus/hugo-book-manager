package com.nplus.bookmanager.service

import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OverviewCoverageServiceTest {
    @TempDir
    lateinit var root: File

    private val books get() = File(root, "books").apply { mkdirs() }
    private val portal get() = File(root, "portal").apply { mkdirs() }
    private val newBooks get() = File(root, "new-books").apply { mkdirs() }

    /** Writes a home page under `books-done`'s three-folder shape. */
    private fun book(
        slug: String,
        body: String,
        top: String = "craft",
        sub: String = "engineering",
        leaf: String = "coding-practice",
    ) = File(books, "$top/$sub/$leaf/$slug/site/content/_index.md").apply {
        parentFile.mkdirs()
        writeText("---\ntitle: \"x\"\n---\n\n{{< book-cover />}}\n\n$body")
    }

    /** One row of `audit-overview.py --all --json`, trimmed to the fields we read. */
    private fun row(
        slug: String,
        fails: Int = 0,
        failed: List<String> = emptyList(),
        total: Int = 2000,
    ): String {
        val names = failed.joinToString(", ") { "\"" + it + "\"" }
        return "{\"slug\": \"" + slug + "\", \"fails\": " + fails + ", \"failed\": [" + names + "], " +
            "\"notes\": 123456, \"legacy\": false, \"total\": " + total + ", " +
            "\"sections\": {\"作者的位置\": 400, \"完整摘要\": 800, \"定位\": 300, \"這本書的限制\": 500}, " +
            "\"years\": 5, \"latin\": 7, \"filler\": 0, \"limit_sentences\": 16}"
    }

    private fun service(audit: (File, File) -> String?) =
        OverviewCoverageService(books, portal, newBooks, File("scripts/audit-overview.py"), audit)

    // ==================== stateOf() ====================

    @Test
    fun `the four-section marker is what makes a book done`() {
        book("a", "{{% book-overview %}}\n\n## 作者的位置\n\n…\n\n{{% /book-overview %}}")
        assertEquals("done", OverviewCoverageService.stateOf(File(books, "craft/engineering/coding-practice/a")))
    }

    @Test
    fun `a details block that still says 深度概覽 is legacy`() {
        book("b", "{{% details \"📘 深度概覽\" %}}\n\n## 作者背景\n\n…\n\n{{% /details %}}")
        assertEquals("legacy", OverviewCoverageService.stateOf(File(books, "craft/engineering/coding-practice/b")))
    }

    @Test
    fun `a home page with no overview at all is none, not legacy`() {
        // Every repo under new-books/ is in this state, and the audit's own
        // `legacy` flag cannot tell it apart from a rewritten one.
        book("c", "書還在寫。")
        assertEquals("none", OverviewCoverageService.stateOf(File(books, "craft/engineering/coding-practice/c")))
    }

    @Test
    fun `a missing home page is none rather than a crash`() {
        assertEquals("none", OverviewCoverageService.stateOf(File(books, "craft/engineering/coding-practice/gone")))
    }

    // ==================== parse() ====================

    @Test
    fun `taxonomy comes from the audit's relative path`() {
        book("a", "{{% book-overview %}}", top = "wisdom", sub = "science", leaf = "cognitive")
        val svc = service { _, _ -> "[]" }
        val b = svc.parse("[${row("wisdom/science/cognitive/a")}]", books, categorised = true).single()
        assertEquals(listOf("a", "wisdom", "science", "cognitive"), listOf(b.slug, b.top, b.sub, b.leaf))
    }

    @Test
    fun `new-books rows carry no taxonomy because the folders do not exist yet`() {
        File(newBooks, "d/site/content/_index.md").apply {
            parentFile.mkdirs()
            writeText("---\ntitle: x\n---\n還沒寫概覽")
        }
        val out = service { _, _ -> "[]" }.parse("[${row("d")}]", newBooks, categorised = false)
        val b = out.single()
        assertEquals(listOf("d", "", "", ""), listOf(b.slug, b.top, b.sub, b.leaf))
        assertEquals("none", b.state)
    }

    @Test
    fun `quality counters are carried across under the portal's field names`() {
        book("a", "{{% book-overview %}}")
        val svc = service { _, _ -> "[]" }
        val json = "[${row("craft/engineering/coding-practice/a", fails = 2, failed = listOf("引用年份", "無空洞讚美"))}]"
        val b = svc.parse(json, books, categorised = true).single()
        assertEquals(2, b.fails)
        assertEquals(listOf("引用年份", "無空洞讚美"), b.failed)
        assertEquals(5, b.years)
        // `latin` in the audit's output is the count of named works and people.
        assertEquals(7, b.refs)
        assertEquals(16, b.limitSentences)
        assertEquals(800, b.sections["完整摘要"])
    }

    @Test
    fun `a row with no meta - the audit found no overview block - still yields a book`() {
        // check() returns an empty meta dict when there is no overview at all, so
        // every numeric field is absent. Dropping those rows would delete exactly
        // the books that most need to appear in the queue.
        book("e", "書還在寫。")
        val bare = """[{"slug": "craft/engineering/coding-practice/e", "fails": 1, "failed": ["有深度概覽"], "notes": 9}]"""
        val b = service { _, _ -> "[]" }.parse(bare, books, categorised = true).single()
        assertEquals("none", b.state)
        assertEquals(0, b.chars)
        assertEquals(0, b.sections["定位"])
        assertEquals(9, b.notes)
    }

    // ==================== scan() ====================

    @Test
    fun `both roots are scanned and the result is sorted by slug`() {
        book("zebra", "{{% book-overview %}}")
        File(newBooks, "alpha/site/content/_index.md").apply {
            parentFile.mkdirs()
            writeText("---\ntitle: x\n---\n")
        }
        val svc =
            service { _, r ->
                if (r.name == "books") "[${row("craft/engineering/coding-practice/zebra")}]" else "[${row("alpha")}]"
            }
        assertEquals(listOf("alpha", "zebra"), svc.scan()!!.map { it.slug })
    }

    @Test
    fun `a failed audit returns null so a good artifact is never overwritten with nothing`() {
        book("a", "{{% book-overview %}}")
        assertNull(service { _, _ -> null }.scan())
    }

    // ==================== save() ====================

    @Test
    fun `the artifact is two-space indented and carries the thresholds`() {
        book("a", "{{% book-overview %}}")
        val svc = service { _, _ -> "[${row("craft/engineering/coding-practice/a")}]" }
        svc.save(svc.parse("[${row("craft/engineering/coding-practice/a")}]", books, true))
        val text = svc.overviewFile.readText()
        assertTrue(text.contains("\n  \"generatedAt\""), "expected two-space indent for prettier")
        assertTrue(text.contains("\"minYears\": 2"))
        assertTrue(text.contains("\"min\": 250") && text.contains("\"max\": 1700"))
        assertTrue(text.contains("\"state\": \"done\""))
        assertTrue(text.endsWith("\n"))
    }
}
