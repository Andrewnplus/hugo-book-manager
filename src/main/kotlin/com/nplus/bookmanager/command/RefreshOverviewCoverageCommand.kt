package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.OverviewCoverageService
import java.io.File

class RefreshOverviewCoverageCommand(
    private val serviceFactory: (File, File, File?) -> OverviewCoverageService = { books, portal, new ->
        OverviewCoverageService(books, portal, new)
    },
) : CliktCommand(name = "refresh-overview-coverage") {
    override fun help(context: Context) = "Audit every local book clone's deep overview and refresh the portal artifact"

    override fun run() {
        val portalDir = File(AppConfig.portalDir)
        val booksDir = File(AppConfig.booksDir)
        if (!portalDir.isDirectory) {
            println("Error: PORTAL_DIR not found: ${portalDir.path} (set it in local.properties)")
            return
        }
        if (!booksDir.isDirectory) {
            println("Error: BOOKS_DIR not found: ${booksDir.path} (set it in local.properties)")
            return
        }

        val service = serviceFactory(booksDir, portalDir, File(AppConfig.defaultWorkDir).takeIf { it.isDirectory })
        println("  Auditing every overview — this walks 1,700+ repos and takes about a minute.")
        val books = service.scan()
        if (books == null) {
            println("Error: audit-overview.py did not run (is python3 on PATH, and is the CWD hugo-book-manager?)")
            return
        }
        if (books.isEmpty()) {
            println("No books found under ${booksDir.path} — nothing written.")
            return
        }
        service.save(books)

        val byState = books.groupingBy { it.state }.eachCount()
        val done = byState["done"] ?: 0
        val pct = done * 100.0 / books.size
        println("✅ Audited ${books.size} book(s) → ${service.overviewFile.path}")
        println("  done    %5d  (%.1f%%)".format(done, pct))
        println("  legacy  %5d".format(byState["legacy"] ?: 0))
        println("  none    %5d".format(byState["none"] ?: 0))

        val regressed = books.filter { it.state == "done" && it.fails > 0 }
        if (regressed.isNotEmpty()) {
            println("\n⚠️  ${regressed.size} rewritten book(s) no longer pass the audit:")
            for (b in regressed.take(10)) {
                println("  ${b.slug} — ${b.failed.joinToString("、")}")
            }
            if (regressed.size > 10) println("  … and ${regressed.size - 10} more")
        }
        println("\n→ Remember to commit + push the portal repo to publish the dashboard.")
    }
}
