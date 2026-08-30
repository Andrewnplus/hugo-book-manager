package com.nplus.bookmanager.service

import java.io.File
import java.time.LocalDate

class BookChapterService(
    private val booksDir: File,
) {
    fun findRepoDir(repoName: String): File? =
        booksDir
            .walkTopDown()
            .maxDepth(4)
            .firstOrNull { it.isDirectory && it.name == repoName && File(it, "site/content/docs").isDirectory }

    fun chapterFiles(repoDir: File): List<File> {
        val docsDir = File(repoDir, "site/content/docs")
        if (!docsDir.isDirectory) return emptyList()
        val indexFiles =
            docsDir
                .walkTopDown()
                .filter { it.isFile && it.name == "_index.md" }
                .toList()
        val dirs = indexFiles.map { it.parentFile }
        return indexFiles
            .filter { file ->
                val dir = file.parentFile
                dir != docsDir && dirs.none { it != dir && it.startsWith(dir) }
            }.sortedBy { it.path }
    }

    fun chapterKey(
        repoDir: File,
        chapterFile: File,
    ): String = chapterFile.parentFile.relativeTo(File(repoDir, "site/content/docs")).path

    fun markRead(
        chapterFile: File,
        date: LocalDate = LocalDate.now(),
    ) {
        val text = chapterFile.readText()
        require(text.startsWith("---")) { "no frontmatter in ${chapterFile.path}" }
        val end = text.indexOf("\n---", startIndex = 3)
        require(end >= 0) { "unterminated frontmatter in ${chapterFile.path}" }

        val block =
            text
                .substring(3, end)
                .lines()
                .filterNot { it.trimStart().startsWith("read:") || it.trimStart().startsWith("readAt:") }
                .joinToString("\n")
                .trimEnd()
        val updated = "---$block\nread: true\nreadAt: \"$date\"\n${text.substring(end + 1)}"
        chapterFile.writeText(updated)
    }

    private fun File.startsWith(other: File): Boolean = canonicalPath.startsWith(other.canonicalPath + File.separator)
}
