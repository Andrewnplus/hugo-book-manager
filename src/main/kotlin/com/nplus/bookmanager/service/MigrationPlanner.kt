package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.MigrateLeafResult
import com.nplus.bookmanager.model.RepoIndex
import java.io.File

/**
 * What `migrate-topic-tiers` intends to do to one repo: which topics to add
 * and remove, and where (if anywhere) the local clone should move to.
 */
data class RepoPlan(
    val name: String,
    val topicsToAdd: List<String>,
    val topicsToRemove: List<String>,
    val moveFrom: File?,
    val moveTo: File?,
    val target: MigrateLeafResult,
) {
    fun isNoOp(): Boolean = topicsToAdd.isEmpty() && topicsToRemove.isEmpty() && moveFrom == null
}

/**
 * The decision layer of `migrate-topic-tiers`, split out from the Clikt
 * command so it can be tested without gh, AppConfig or a real work tree.
 *
 * Every method here is a pure function of its arguments — the work roots are
 * passed in rather than read from AppConfig, because they decide where 1300+
 * repos get moved to and a wrong root silently relocates the whole library.
 */
object MigrationPlanner {
    /** Migrate anything not already carrying all three tiers. */
    fun needsMigration(entry: RepoIndex.RepoEntry): Boolean {
        val hasTop = entry.topics.any { it.startsWith("top-") }
        val hasSub = entry.topics.any { it.startsWith("sub-") }
        val hasLeaf = entry.topics.any { it.startsWith("leaf-") }
        return !(hasTop && hasSub && hasLeaf)
    }

    /**
     * Walk up from a clone until one of [workRoots] is reached, so the repo is
     * re-filed under the same root it currently lives in. Falls back to
     * [fallbackRoot] when the clone sits outside every configured root.
     */
    fun findWorkRoot(
        file: File,
        workRoots: List<File>,
        fallbackRoot: File,
    ): File {
        val roots = workRoots.map { it.absoluteFile.canonicalFile }.toSet()
        var cur: File? = file.absoluteFile.canonicalFile.parentFile
        while (cur != null) {
            if (cur in roots) return cur
            cur = cur.parentFile
        }
        return fallbackRoot
    }

    /**
     * Build the plan for one repo.
     *
     * Topics: the three target tiers are added when missing; existing
     * `top-`/`sub-`/`leaf-` topics and legacy `*-book-summary` topics that are
     * not part of the target are removed. `book-summary` itself is kept — it is
     * the shared kind topic, not a per-category leftover.
     *
     * Move: only when a local clone is known and its resolved destination
     * actually differs, so re-running the migration is idempotent.
     */
    fun buildPlan(
        current: RepoIndex.RepoEntry,
        target: MigrateLeafResult,
        cloneIndex: Map<String, File>,
        workRoots: List<File>,
        fallbackRoot: File,
    ): RepoPlan {
        val targetTriple =
            listOf(
                "top-${target.topCategory}",
                "sub-${target.subCategory}",
                "leaf-${target.leafCategory}",
            )

        val tieredCurrent =
            current.topics.filter {
                it.startsWith("top-") ||
                    it.startsWith("sub-") ||
                    it.startsWith("leaf-") ||
                    (it.endsWith("-book-summary") && it != "book-summary")
            }
        val toAdd = targetTriple.filter { it !in current.topics }
        val toRemove = tieredCurrent.filter { it !in targetTriple }

        val moveFrom = cloneIndex[current.name]
        val moveTo =
            if (moveFrom != null) {
                val workRoot = findWorkRoot(moveFrom, workRoots, fallbackRoot)
                File(workRoot, "${target.topCategory}/${target.subCategory}/${target.leafCategory}/${current.name}")
            } else {
                null
            }
        val needsMove = moveFrom != null && moveTo != null && moveFrom.absolutePath != moveTo.absolutePath

        return RepoPlan(
            name = current.name,
            topicsToAdd = toAdd,
            topicsToRemove = toRemove,
            moveFrom = if (needsMove) moveFrom else null,
            moveTo = if (needsMove) moveTo else null,
            target = target,
        )
    }
}
