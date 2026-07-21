package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.RepoIndex
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepoIndexServiceTest {
    @TempDir
    lateinit var tempDir: File

    private fun serviceWithIndex() = RepoIndexService(File(tempDir, "existing-repos.yaml"))

    // ==================== canonicalize() ====================

    @Test
    fun `canonicalize slugifies a title`() {
        assertEquals("power-of-habit", RepoIndexService.canonicalize("The Power of Habit"))
    }

    @Test
    fun `canonicalize strips a leading the- from an existing slug`() {
        assertEquals("power-of-habit", RepoIndexService.canonicalize("the-power-of-habit"))
    }

    @Test
    fun `canonicalize collapses punctuation runs and trims dashes`() {
        assertEquals("thinking-fast-and-slow", RepoIndexService.canonicalize("  Thinking, Fast and Slow!  "))
    }

    @Test
    fun `canonicalize keeps a the that is not a leading word`() {
        assertEquals("all-the-light", RepoIndexService.canonicalize("All the Light"))
    }

    // ==================== findExisting() ====================

    private fun indexOf(vararg names: String) =
        RepoIndex(
            lastUpdated = null,
            repos = names.map { RepoIndex.RepoEntry(name = it, description = "", url = "", topics = emptyList()) },
        )

    @Test
    fun `findExisting matches across the- prefix mismatch`() {
        val found = serviceWithIndex().findExisting(indexOf("the-power-of-habit"), listOf("power-of-habit"))
        assertEquals("the-power-of-habit", found?.name)
    }

    @Test
    fun `findExisting matches an english title against a slug entry`() {
        val found = serviceWithIndex().findExisting(indexOf("power-of-habit"), listOf("The Power of Habit"))
        assertNotNull(found)
    }

    @Test
    fun `findExisting returns null when nothing matches`() {
        assertNull(serviceWithIndex().findExisting(indexOf("deep-work"), listOf("The Power of Habit")))
    }

    @Test
    fun `findExisting returns null for empty or blank candidates`() {
        val service = serviceWithIndex()
        assertNull(service.findExisting(indexOf("deep-work"), emptyList()))
        assertNull(service.findExisting(indexOf("deep-work"), listOf("", "   ")))
    }

    @Test
    fun `candidatesFor includes the ai repo name and drops blanks`() {
        assertEquals(
            listOf("deep-work", "Deep Work"),
            RepoIndexService.candidatesFor("Deep Work", aiRepoName = "deep-work"),
        )
        assertEquals(listOf("Deep Work"), RepoIndexService.candidatesFor("Deep Work", aiRepoName = null))
        assertEquals(emptyList(), RepoIndexService.candidatesFor("", aiRepoName = " "))
    }

    // ==================== save() / load() ====================

    @Test
    fun `save then load round-trips entries`() {
        val service = serviceWithIndex()
        val index =
            RepoIndex(
                lastUpdated = "2026-07-21T10:00:00+02:00",
                repos =
                    listOf(
                        RepoIndex.RepoEntry("deep-work", "Cal Newport: focus", "https://x/deep-work", listOf("hugobook", "top-work")),
                    ),
            )

        service.save(index)
        val loaded = service.load()

        assertEquals("2026-07-21T10:00:00+02:00", loaded.lastUpdated)
        assertEquals(1, loaded.repos.size)
        assertEquals("deep-work", loaded.repos[0].name)
        assertEquals("Cal Newport: focus", loaded.repos[0].description)
        assertEquals(listOf("hugobook", "top-work"), loaded.repos[0].topics)
    }

    @Test
    fun `save sorts repos by name for stable diffs`() {
        val service = serviceWithIndex()
        service.save(
            RepoIndex(
                lastUpdated = null,
                repos =
                    listOf("zero-to-one", "atomic-habits", "deep-work").map {
                        RepoIndex.RepoEntry(it, "", "", emptyList())
                    },
            ),
        )

        assertEquals(listOf("atomic-habits", "deep-work", "zero-to-one"), service.load().repos.map { it.name })
    }

    @Test
    fun `load returns an empty index when the file is missing`() {
        val loaded = RepoIndexService(File(tempDir, "nope.yaml")).load()
        assertNull(loaded.lastUpdated)
        assertTrue(loaded.repos.isEmpty())
    }

    @Test
    fun `load treats malformed yaml as empty instead of throwing`() {
        val file = File(tempDir, "existing-repos.yaml")
        file.writeText("repos: [unterminated\n")

        assertTrue(RepoIndexService(file).load().repos.isEmpty())
    }
}
