package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.MigrateLeafResult
import com.nplus.bookmanager.model.RepoIndex
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MigrationPlannerTest {
    @TempDir
    lateinit var root: File

    private fun entry(
        name: String,
        vararg topics: String,
    ) = RepoIndex.RepoEntry(name = name, description = "", url = "", topics = topics.toList())

    private fun target(
        name: String,
        top: String = "professional",
        sub: String = "finance",
        leaf: String = "investing",
    ) = MigrateLeafResult(name = name, topCategory = top, subCategory = sub, leafCategory = leaf)

    @Test
    fun `needsMigration is false only when all three tiers are present`() {
        assertFalse(MigrationPlanner.needsMigration(entry("x", "hugobook", "top-a", "sub-b", "leaf-c")))
    }

    @Test
    fun `needsMigration is true when any tier is missing`() {
        assertTrue(MigrationPlanner.needsMigration(entry("x", "top-a", "sub-b")), "missing leaf")
        assertTrue(MigrationPlanner.needsMigration(entry("x", "top-a", "leaf-c")), "missing sub")
        assertTrue(MigrationPlanner.needsMigration(entry("x", "sub-b", "leaf-c")), "missing top")
        assertTrue(MigrationPlanner.needsMigration(entry("x", "hugobook")), "no tiers at all")
    }

    @Test
    fun `buildPlan adds the missing tiers and keeps unrelated topics`() {
        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "hugobook", "nplus-portal", "top-professional"),
                target = target("deep-work"),
                cloneIndex = emptyMap(),
                workRoots = emptyList(),
                fallbackRoot = root,
            )

        assertEquals(listOf("sub-finance", "leaf-investing"), plan.topicsToAdd)
        assertTrue(plan.topicsToRemove.isEmpty())
    }

    @Test
    fun `buildPlan removes tier topics that are not part of the target`() {
        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "hugobook", "top-personal", "sub-mindset", "leaf-growth"),
                target = target("deep-work"),
                cloneIndex = emptyMap(),
                workRoots = emptyList(),
                fallbackRoot = root,
            )

        assertEquals(listOf("top-professional", "sub-finance", "leaf-investing"), plan.topicsToAdd)
        assertEquals(listOf("top-personal", "sub-mindset", "leaf-growth"), plan.topicsToRemove)
    }

    @Test
    fun `buildPlan removes legacy per-category book-summary topics but keeps the shared one`() {
        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "book-summary", "finance-book-summary", "top-professional"),
                target = target("deep-work"),
                cloneIndex = emptyMap(),
                workRoots = emptyList(),
                fallbackRoot = root,
            )

        assertEquals(listOf("finance-book-summary"), plan.topicsToRemove)
        assertFalse("book-summary" in plan.topicsToRemove, "the shared kind topic must be kept")
    }

    @Test
    fun `buildPlan on an already-migrated repo with no clone is a no-op`() {
        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "top-professional", "sub-finance", "leaf-investing"),
                target = target("deep-work"),
                cloneIndex = emptyMap(),
                workRoots = emptyList(),
                fallbackRoot = root,
            )

        assertTrue(plan.isNoOp())
        assertNull(plan.moveFrom)
    }

    @Test
    fun `buildPlan targets the three-tier path under the clone's own work root`() {
        val booksDone = File(root, "books-done")
        val from = File(booksDone, "personal/mindset/growth/deep-work").apply { mkdirs() }

        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "top-personal", "sub-mindset", "leaf-growth"),
                target = target("deep-work"),
                cloneIndex = mapOf("deep-work" to from),
                workRoots = listOf(booksDone),
                fallbackRoot = File(root, "new-books"),
            )

        assertEquals(from, plan.moveFrom)
        assertEquals(
            File(booksDone, "professional/finance/investing/deep-work").path,
            plan.moveTo?.path,
        )
    }

    @Test
    fun `buildPlan does not move a clone that is already in the right place`() {
        val booksDone = File(root, "books-done")
        val from = File(booksDone, "professional/finance/investing/deep-work").apply { mkdirs() }

        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work", "top-professional", "sub-finance", "leaf-investing"),
                target = target("deep-work"),
                cloneIndex = mapOf("deep-work" to from),
                workRoots = listOf(booksDone),
                fallbackRoot = File(root, "new-books"),
            )

        assertNull(plan.moveFrom, "re-running the migration must be idempotent")
        assertTrue(plan.isNoOp())
    }

    @Test
    fun `buildPlan keeps a clone inside whichever work root it currently lives in`() {
        val booksDone = File(root, "books-done")
        val extra = File(root, "extra-shelf")
        val from = File(extra, "old/place/deep-work").apply { mkdirs() }

        val plan =
            MigrationPlanner.buildPlan(
                current = entry("deep-work"),
                target = target("deep-work"),
                cloneIndex = mapOf("deep-work" to from),
                workRoots = listOf(booksDone, extra),
                fallbackRoot = File(root, "new-books"),
            )

        assertEquals(
            File(extra, "professional/finance/investing/deep-work").path,
            plan.moveTo?.path,
            "a repo under the extra root must not be relocated into books-done",
        )
    }

    @Test
    fun `buildPlan refuses to move a directory into its own subtree`() {
        val booksDone = File(root, "books-done")
        val from = File(booksDone, "professional/finance/investing").apply { mkdirs() }

        val plan =
            MigrationPlanner.buildPlan(
                current = entry("investing"),
                target = target("investing"),
                cloneIndex = mapOf("investing" to from),
                workRoots = listOf(booksDone),
                fallbackRoot = File(root, "new-books"),
            )

        assertNull(plan.moveFrom, "a move whose destination is inside the source must be dropped")
        assertNull(plan.moveTo)
    }

    @Test
    fun `isNestedUnder only flags real descendants`() {
        val base = File(root, "books-done/professional/finance/investing")

        assertTrue(MigrationPlanner.isNestedUnder(File(base, "investing"), base))
        assertFalse(MigrationPlanner.isNestedUnder(base, base), "a path is not nested under itself")
        assertFalse(
            MigrationPlanner.isNestedUnder(File(root, "books-done/professional/finance/investing-notes"), base),
            "a sibling sharing a name prefix is not a descendant",
        )
        assertFalse(MigrationPlanner.isNestedUnder(File(root, "new-books/deep-work"), base))
    }

    @Test
    fun `findWorkRoot falls back when the clone sits outside every configured root`() {
        val stray = File(root, "somewhere/else/deep-work").apply { mkdirs() }
        val fallback = File(root, "new-books")

        val resolved = MigrationPlanner.findWorkRoot(stray, listOf(File(root, "books-done")), fallback)

        assertEquals(fallback, resolved)
    }

    @Test
    fun `findWorkRoot picks the containing root even when nested deeply`() {
        val booksDone = File(root, "books-done")
        val deep = File(booksDone, "a/b/c/d/deep-work").apply { mkdirs() }

        val resolved = MigrationPlanner.findWorkRoot(deep, listOf(booksDone), File(root, "new-books"))

        assertEquals(booksDone.canonicalFile, resolved)
    }
}
