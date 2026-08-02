package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.GoalDefinition
import com.nplus.bookmanager.model.GoalScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GoalProgressServiceTest {
    @TempDir
    lateinit var root: File

    private val portalDir get() = File(root, "portal")
    private val notesDir get() = File(root, "notes")
    private val booksDir get() = File(root, "books")

    private fun service() = GoalProgressService(portalDir, notesDir, booksDir)

    private fun writeNote(
        path: String,
        frontmatter: String,
    ) {
        val file = File(notesDir, path)
        file.parentFile.mkdirs()
        file.writeText("---\n$frontmatter\n---\n\nbody\n")
    }

    private fun writeChapter(
        path: String,
        frontmatter: String,
    ) {
        val file = File(booksDir, "$path/_index.md")
        file.parentFile.mkdirs()
        file.writeText("---\n$frontmatter\n---\n\nbody\n")
    }

    private fun daysAgo(n: Long) = LocalDate.now().minusDays(n).toString()

    /** Warnings are the only signal a skipped file gives, so tests assert on them. */
    private fun capturingStdout(block: () -> Unit): String {
        val buffer = ByteArrayOutputStream()
        val original = System.out
        System.setOut(PrintStream(buffer))
        try {
            block()
        } finally {
            System.setOut(original)
        }
        return buffer.toString()
    }

    // ==================== loadGoals() ====================

    @Test
    fun `loadGoals parses id metric and scope`() {
        val goals = File(portalDir, "src/data/goals.yaml")
        goals.parentFile.mkdirs()
        goals.writeText(
            """
            - id: writing-notes
              metric: note-status-count
              label: 寫作筆記
              scope:
                station: writing-note
                statuses: [studied, reviewed]
            - id: leetcode
              metric: leetcode-count
              scope:
                statuses: [reviewed]
                categories: [array, graph]
            """.trimIndent(),
        )

        val loaded = service().loadGoals()

        assertEquals(listOf("writing-notes", "leetcode"), loaded.map { it.id })
        assertEquals("writing-note", loaded[0].scope.station)
        assertEquals(listOf("studied", "reviewed"), loaded[0].scope.statuses)
        assertEquals(listOf("array", "graph"), loaded[1].scope.categories)
    }

    // ==================== note-status-count ====================

    @Test
    fun `scanNoteStation counts done by status and groups by category and section`() {
        writeNote("writing-note/src/content/concepts/voice/tone.md", "status: reviewed")
        writeNote("writing-note/src/content/concepts/voice/rhythm.md", "status: draft")
        writeNote("writing-note/src/content/problems/blank-page.md", "status: studied")

        val goal =
            GoalDefinition(
                id = "writing",
                metric = GoalDefinition.METRIC_NOTE_STATUS,
                scope = GoalScope(station = "writing-note", statuses = listOf("studied", "reviewed")),
            )
        val (progress, _) = service().scan(listOf(goal))

        val p = progress.single()
        assertEquals(2, p.done)
        assertEquals(3, p.total)
        assertEquals("notes", p.unit)
        assertEquals(1 to 2, p.breakdown.single { it.key == "voice" }.let { it.done to it.total })
        assertEquals("concepts", p.breakdown.single { it.key == "voice" }.section)
        // A file directly under problems/ falls back to the sub-dir as its key.
        assertEquals("problems", p.breakdown.single { it.section == "problems" }.key)
    }

    @Test
    fun `scanNoteStation reports lastReviewed inside the window as recent activity`() {
        writeNote("writing-note/src/content/concepts/fresh.md", "status: reviewed\nlastReviewed: \"${daysAgo(3)}\"")
        writeNote("writing-note/src/content/concepts/stale.md", "status: reviewed\nlastReviewed: \"${daysAgo(90)}\"")

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val (_, recent) = service().scan(listOf(goal))

        assertEquals(listOf("writing-note/fresh"), recent.map { it.item })
    }

    /**
     * An unquoted date is a YAML timestamp, not a string. SnakeYAML returns a
     * Date for it and the naive text parse used to drop the activity silently.
     */
    @Test
    fun `scan accepts an unquoted yaml date for lastReviewed`() {
        writeNote("writing-note/src/content/concepts/fresh.md", "status: reviewed\nlastReviewed: ${daysAgo(3)}")

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val (_, recent) = service().scan(listOf(goal))

        assertEquals(listOf("writing-note/fresh"), recent.map { it.item })
        assertEquals(daysAgo(3), recent.single().date)
    }

    @Test
    fun `scan skips index files and tolerates bad frontmatter`() {
        writeNote("writing-note/src/content/concepts/good.md", "status: reviewed")
        File(notesDir, "writing-note/src/content/concepts/_index.md").writeText("---\ntitle: 概念\n---\n")
        File(notesDir, "writing-note/src/content/concepts/broken.md").writeText("---\nstatus: [unterminated\n---\n")

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val p = service().scan(listOf(goal)).first.single()

        // _index.md excluded; broken.md counted as not-done rather than crashing.
        assertEquals(1, p.done)
        assertEquals(2, p.total)
    }

    /**
     * A BOM or a leading blank line used to make the fence check fail, so the
     * file counted toward total but never toward done — the same shape of
     * silent progress loss as the unquoted-date bug, with no warning at all.
     */
    @Test
    fun `scan reads frontmatter behind a BOM or a leading blank line`() {
        val base = "writing-note/src/content/concepts"
        File(notesDir, base).mkdirs()
        File(notesDir, "$base/bom.md").writeText("﻿---\nstatus: reviewed\n---\n\nbody\n")
        File(notesDir, "$base/blank-first.md").writeText("\n---\nstatus: reviewed\n---\n\nbody\n")

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val p = service().scan(listOf(goal)).first.single()

        assertEquals(2, p.done)
        assertEquals(2, p.total)
    }

    @Test
    fun `scan warns instead of silently skipping a file whose frontmatter never closes`() {
        val base = "writing-note/src/content/concepts"
        File(notesDir, base).mkdirs()
        File(notesDir, "$base/no-close.md").writeText("---\nstatus: reviewed\n\nbody\n")
        File(notesDir, "$base/no-fence.md").writeText("just a body, no frontmatter at all\n")

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        var p: com.nplus.bookmanager.model.GoalProgress? = null
        val warnings = capturingStdout { p = service().scan(listOf(goal)).first.single() }

        // Still counted as not-done, but no longer invisible.
        assertEquals(0, p!!.done)
        assertEquals(2, p!!.total)
        assertContains(warnings, "unterminated frontmatter")
        assertContains(warnings, "no-close.md")
        assertContains(warnings, "no frontmatter fence")
        assertContains(warnings, "no-fence.md")
    }

    // ==================== leetcode-count ====================

    @Test
    fun `scanLeetcode filters by category and collects reviewedDates`() {
        writeNote(
            "leetcode-note/src/content/problems/two-sum.md",
            "status: reviewed\ncategory: array\nreviewedDates: [\"${daysAgo(2)}\", \"${daysAgo(60)}\"]",
        )
        writeNote("leetcode-note/src/content/problems/course-schedule.md", "status: draft\ncategory: graph")
        writeNote("leetcode-note/src/content/problems/lru-cache.md", "status: reviewed\ncategory: design")

        val goal =
            GoalDefinition(
                "leetcode",
                GoalDefinition.METRIC_LEETCODE,
                GoalScope(statuses = listOf("reviewed"), categories = listOf("array", "graph")),
            )
        val (progress, recent) = service().scan(listOf(goal))

        val p = progress.single()
        // design is outside scope.categories and must not be counted at all.
        assertEquals(1, p.done)
        assertEquals(2, p.total)
        assertEquals("problems", p.unit)
        // Only the in-window review date becomes an activity item.
        assertEquals(listOf("leetcode/two-sum"), recent.map { it.item })
    }

    // ==================== repo-completion ====================

    @Test
    fun `scanRepoCompletion counts read chapters across repos`() {
        writeChapter("top-work/deep-work/site/content/docs", "title: Docs")
        writeChapter("top-work/deep-work/site/content/docs/01-intro", "title: 導論\nread: true\nreadAt: \"${daysAgo(1)}\"")
        writeChapter("top-work/deep-work/site/content/docs/02-rules", "title: 規則")

        val goal =
            GoalDefinition("books", GoalDefinition.METRIC_REPO_COMPLETION, GoalScope(repos = listOf("deep-work")))
        val (progress, recent) = service().scan(listOf(goal))

        val p = progress.single()
        assertEquals(1, p.done)
        assertEquals(2, p.total)
        assertEquals("chapters", p.unit)
        // Single-repo goals keep the bare chapter key (no repo prefix).
        assertEquals(setOf("01-intro", "02-rules"), p.breakdown.map { it.key }.toSet())
        assertEquals(listOf("deep-work/01-intro"), recent.map { it.item })
    }

    @Test
    fun `scanRepoCompletion prefixes chapter keys when the goal spans repos`() {
        writeChapter("deep-work/site/content/docs", "title: Docs")
        writeChapter("deep-work/site/content/docs/01-intro", "title: 導論\nread: true")
        writeChapter("atomic-habits/site/content/docs", "title: Docs")
        writeChapter("atomic-habits/site/content/docs/01-intro", "title: 導論")

        val goal =
            GoalDefinition(
                "books",
                GoalDefinition.METRIC_REPO_COMPLETION,
                GoalScope(repos = listOf("deep-work", "atomic-habits")),
            )
        val p = service().scan(listOf(goal)).first.single()

        assertEquals(setOf("deep-work/01-intro", "atomic-habits/01-intro"), p.breakdown.map { it.key }.toSet())
        assertEquals(1, p.done)
        assertEquals(2, p.total)
    }

    @Test
    fun `scan skips unknown and build-time metrics without failing`() {
        val goals =
            listOf(
                GoalDefinition("articles", GoalDefinition.METRIC_ARTICLES, GoalScope()),
                GoalDefinition("podcast", GoalDefinition.METRIC_PODCAST, GoalScope()),
                GoalDefinition("mystery", "no-such-metric", GoalScope()),
            )

        assertTrue(service().scan(goals).first.isEmpty())
    }

    // ==================== save() ====================

    @Test
    fun `save writes a derived artifact with sorted breakdown and a do-not-edit marker`() {
        writeNote("writing-note/src/content/concepts/zebra/z.md", "status: reviewed")
        writeNote("writing-note/src/content/concepts/alpha/a.md", "status: draft")
        File(portalDir, "src/data").mkdirs()

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val service = service()
        val (progress, recent) = service.scan(listOf(goal))
        service.save(progress, recent)

        val json = service.progressFile.readText()
        assertContains(json, "DERIVED")
        assertContains(json, "\"writing\"")
        assertNotNull(Regex("\"generatedAt\": \"[^\"]+\"").find(json))
        // Breakdown is sorted by key so the artifact diffs cleanly in git.
        assertTrue(json.indexOf("\"alpha\"") < json.indexOf("\"zebra\""))
        assertTrue(json.endsWith("\n"))
    }

    @Test
    fun `save nests done total unit and breakdown under each goal id`() {
        // progress.json is a contract read by the portal and nplus-backend, so
        // the shape matters, not just which strings appear. Asserting on text
        // alone lets a field land at the wrong nesting level unnoticed.
        writeNote("writing-note/src/content/concepts/zebra/z.md", "status: reviewed\nlastReviewed: ${daysAgo(1)}")
        writeNote("writing-note/src/content/concepts/alpha/a.md", "status: draft")
        File(portalDir, "src/data").mkdirs()

        val goal =
            GoalDefinition(
                "writing",
                GoalDefinition.METRIC_NOTE_STATUS,
                GoalScope(station = "writing-note", statuses = listOf("reviewed")),
            )
        val service = service()
        val (progress, recent) = service.scan(listOf(goal))
        service.save(progress, recent)

        val root = Json.parseToJsonElement(service.progressFile.readText()).jsonObject
        assertEquals(setOf("\$comment", "generatedAt", "goals", "recent"), root.keys)

        val writing =
            root
                .getValue("goals")
                .jsonObject
                .getValue("writing")
                .jsonObject
        assertEquals(1, writing.getValue("done").jsonPrimitive.int())
        assertEquals(2, writing.getValue("total").jsonPrimitive.int())
        assertEquals("notes", writing.getValue("unit").jsonPrimitive.content)

        val breakdown = writing.getValue("breakdown").jsonArray
        assertEquals(2, breakdown.size)
        val alpha = breakdown[0].jsonObject
        assertEquals("alpha", alpha.getValue("key").jsonPrimitive.content)
        assertEquals(0, alpha.getValue("done").jsonPrimitive.int())
        assertEquals(1, alpha.getValue("total").jsonPrimitive.int())
        assertEquals("concepts", alpha.getValue("section").jsonPrimitive.content)

        val recentEntry =
            root
                .getValue("recent")
                .jsonArray
                .single()
                .jsonObject
        assertEquals(setOf("date", "goalId", "item"), recentEntry.keys)
        assertEquals("writing", recentEntry.getValue("goalId").jsonPrimitive.content)
    }

    private fun kotlinx.serialization.json.JsonPrimitive.int() = content.toInt()
}
