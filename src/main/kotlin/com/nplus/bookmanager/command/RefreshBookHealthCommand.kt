package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.BookHealthService
import java.io.File

class RefreshBookHealthCommand(
    private val serviceFactory: (File, File, File?) -> BookHealthService = ::BookHealthService,
) : CliktCommand(name = "refresh-book-health") {
    override fun help(context: Context) = "Scan local book clones and refresh the portal's content-health artifact"

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
        val books = service.scan()
        if (books.isEmpty()) {
            println("No books found under ${booksDir.path} — nothing written.")
            return
        }
        service.save(books)

        val byTier = books.groupingBy { it.tier }.eachCount()
        println("✅ Scanned ${books.size} book(s) → ${service.healthFile.path}")
        for (tier in listOf("near-empty", "thin", "watch", "ok")) {
            println("  ${tier.padEnd(10)} ${byTier[tier] ?: 0}")
        }
        println("\n→ Remember to commit + push the portal repo to publish the dashboard.")
    }
}
