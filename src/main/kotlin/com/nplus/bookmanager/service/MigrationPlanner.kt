package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.MigrateLeafResult
import com.nplus.bookmanager.model.RepoIndex
import java.io.File

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

object MigrationPlanner {
    fun needsMigration(entry: RepoIndex.RepoEntry): Boolean {
        val hasTop = entry.topics.any { it.startsWith("top-") }
        val hasSub = entry.topics.any { it.startsWith("sub-") }
        val hasLeaf = entry.topics.any { it.startsWith("leaf-") }
        return !(hasTop && hasSub && hasLeaf)
    }

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

    fun isNestedUnder(
        candidate: File,
        ancestor: File,
    ): Boolean {
        val root = ancestor.absoluteFile.normalize().path
        val target = candidate.absoluteFile.normalize().path
        return target.startsWith(root + File.separator)
    }

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
        val needsMove =
            moveFrom != null &&
                moveTo != null &&
                moveFrom.absolutePath != moveTo.absolutePath &&
                !isNestedUnder(moveTo, moveFrom)

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
