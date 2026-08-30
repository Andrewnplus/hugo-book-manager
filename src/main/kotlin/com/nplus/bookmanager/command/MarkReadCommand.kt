package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.BookChapterService
import java.io.File
import java.time.LocalDate

class MarkReadCommand : CliktCommand(name = "mark-read") {
    override fun help(context: Context) = "Mark a book chapter as read in its frontmatter (omit --chapter to list)"

    private val repo by option("--repo", help = "Book repo name (dir name under books-done)").required()
    private val chapter by option("--chapter", help = "Chapter dir name or suffix (e.g. 4-storage-and-retrieval)")

    override fun run() {
        val booksDir = File(AppConfig.booksDir)
        if (!booksDir.isDirectory) {
            println("Error: BOOKS_DIR not found: ${booksDir.path} (set it in local.properties)")
            return
        }
        val service = BookChapterService(booksDir)
        val repoDir = service.findRepoDir(repo)
        if (repoDir == null) {
            println("Error: repo '$repo' not found under ${booksDir.path}")
            return
        }
        val chapters = service.chapterFiles(repoDir)
        if (chapters.isEmpty()) {
            println("Error: no chapters found in ${repoDir.path}/site/content/docs")
            return
        }

        val target = chapter
        if (target == null) {
            println("Chapters in $repo:")
            for (file in chapters) {
                val read = Regex("^read:\\s*true", RegexOption.MULTILINE).containsMatchIn(file.readText())
                println("  ${if (read) "✅" else "⬜"} ${service.chapterKey(repoDir, file)}")
            }
            return
        }

        val matches =
            chapters.filter {
                it.parentFile.name == target || it.parentFile.name.endsWith(target)
            }
        when {
            matches.isEmpty() -> {
                println("Error: no chapter matching '$target' in $repo")
            }

            matches.size > 1 -> {
                println("Error: '$target' is ambiguous: ${matches.joinToString { service.chapterKey(repoDir, it) }}")
            }

            else -> {
                service.markRead(matches.single(), LocalDate.now())
                println("✅ Marked read: $repo/${service.chapterKey(repoDir, matches.single())}")
                println("→ Run ./gradlew refreshGoalProgress to refresh the dashboard data.")
            }
        }
    }
}
