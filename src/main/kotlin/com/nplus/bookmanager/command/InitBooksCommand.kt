package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookStatus
import com.nplus.bookmanager.model.BooksQueue
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.model.QueuedBook
import com.nplus.bookmanager.service.AiTaskService
import com.nplus.bookmanager.service.BookInputService
import com.nplus.bookmanager.service.DocsStructureService
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.ImageService
import java.io.File

/**
 * Command to initialize multiple book repositories from a queue file.
 *
 * This command uses a queue-based workflow:
 *
 * 1. Read books from queue file (books-queue.yaml)
 * 2. Find first pending book
 * 3. Generate AI task for metadata + structure
 * 4. Mark book as "processing" and wait for Claude
 * 5. On re-run, complete the book creation
 * 6. Mark as "completed" and move to next pending book
 */
class InitBooksCommand : CliktCommand(name = "init-books") {
    override fun help(context: Context) = "Initialize multiple book repositories from a queue file"

    private val queueFile by option("--queue", "-q", help = "Path to books queue YAML file")
        .default("templates/books-queue.yaml")

    private val bookId by option("--id", help = "Process only this specific book ID")

    private val showStatus by option("--status", "-s", help = "Show queue status and exit")
        .flag(default = false)

    private val reset by option("--reset", help = "Reset specified book status to pending")
        .flag(default = false)

    private val dryRun by option("--dry-run", "-n", help = "Simulate without making changes")
        .flag(default = false)

    private val bookInputService = BookInputService()
    private val aiTaskService = AiTaskService()
    private val ghService = GitHubCliService()
    private val imageService = ImageService()
    private val docsStructureService = DocsStructureService()

    override fun run() {
        printHeader()

        // Load queue
        val queue = bookInputService.loadBooksQueue(File(queueFile))
        if (queue == null) {
            println("Error: Failed to load queue file: $queueFile")
            return
        }

        // Handle --status flag
        if (showStatus) {
            printQueueStatus(queue)
            return
        }

        // Handle --reset flag
        if (reset && bookId != null) {
            resetBook(queue, bookId!!)
            return
        }

        // Check prerequisites
        if (!checkPrerequisites()) return

        // Determine which book to process
        val targetBook =
            when {
                bookId != null -> {
                    val book = queue.getById(bookId!!)
                    if (book == null) {
                        println("Error: Book with ID '$bookId' not found in queue")
                        return
                    }
                    book
                }
                else -> {
                    // Check if there's a book being processed
                    val processingBook = queue.getProcessing()
                    if (processingBook != null) {
                        processingBook
                    } else {
                        // Get next pending book
                        val pendingBook = queue.getNextPending()
                        if (pendingBook == null) {
                            println("\n✅ All books in queue have been processed!")
                            printQueueStatus(queue)
                            return
                        }
                        pendingBook
                    }
                }
            }

        println("\n📖 Processing: ${targetBook.chineseTitle} (${targetBook.id})")

        // Validate the book
        val errors = bookInputService.validateQueuedBook(targetBook)
        if (errors.isNotEmpty()) {
            println("Error: Invalid book entry:")
            errors.forEach { println("  - $it") }
            return
        }

        // Check current status and proceed accordingly
        when (targetBook.status) {
            BookStatus.PENDING -> runPhase1(queue, targetBook)
            BookStatus.PROCESSING -> runPhase2(queue, targetBook)
            BookStatus.COMPLETED -> {
                println("  This book is already completed.")
                if (bookId == null) {
                    // Find next pending
                    val next = queue.getNextPending()
                    if (next != null) {
                        println("\n  Moving to next pending book...")
                        runPhase1(queue, next)
                    } else {
                        println("\n✅ All books in queue have been processed!")
                    }
                }
            }
            BookStatus.ERROR -> {
                println("  This book previously had an error: ${targetBook.errorMessage}")
                println("  Use --reset --id=${targetBook.id} to retry")
            }
        }
    }

    /**
     * Phase 1: Generate AI tasks for the book
     */
    private fun runPhase1(
        queue: BooksQueue,
        book: QueuedBook,
    ) {
        println("\n[Phase 1] Generating AI tasks...")

        // Check if batch task already pending
        if (aiTaskService.hasPendingBatchMetadataTask()) {
            println()
            println("⚠️  AI task is already pending. Please ask Claude Code to process it.")
            println()
            println("👉 Tell Claude Code: \"請處理 AI 任務\"")
            println("   Then re-run this command.")
            return
        }

        // Write batch metadata request (for single book)
        val batchInput =
            AiTaskService.BatchBookInput(
                bookId = book.id,
                chineseTitle = book.chineseTitle,
                englishTitle = book.englishTitle,
                tableOfContents = book.tableOfContents,
            )

        val taskFile = aiTaskService.writeBatchMetadataRequest(listOf(batchInput))
        println("  Created: ${taskFile.path}")

        // Update status to processing
        if (!dryRun) {
            val updatedQueue = queue.updateStatus(book.id, BookStatus.PROCESSING)
            bookInputService.saveBooksQueue(File(queueFile), updatedQueue)
            println("  Updated status: ${book.id} → processing")
        }

        // Prompt user
        aiTaskService.printBatchMetadataTaskPrompt(
            taskFile = taskFile,
            promptFile = "templates/prompts/book-metadata.txt",
            bookCount = 1,
            bookTitles = listOf(book.chineseTitle),
        )
    }

    /**
     * Phase 2: Read AI results and create the repository
     */
    private fun runPhase2(
        queue: BooksQueue,
        book: QueuedBook,
    ) {
        println("\n[Phase 2] Creating repository...")

        // Check if AI task is completed
        if (!aiTaskService.hasCompletedBatchMetadataTask()) {
            println()
            println("⚠️  AI task not yet completed. Please ask Claude Code to process it.")
            println()
            println("👉 Tell Claude Code: \"請處理 AI 任務\"")
            println("   Then re-run this command.")
            return
        }

        // Read AI results
        val result = aiTaskService.getBatchResultForBook(book.id)
        if (result == null) {
            println("Error: No AI result found for book: ${book.id}")
            markAsError(queue, book, "No AI result found")
            return
        }

        val (metadata, structure) = result

        println("\n  Generated metadata:")
        println("    Repo Name: ${metadata.repoName}")
        println("    Description: ${metadata.description}")
        println("    Category: ${metadata.category}")
        println("    Topics: ${metadata.topics.joinToString(", ")}")

        println("\n  Generated structure:")
        docsStructureService.printDocsStructure(structure)

        if (dryRun) {
            printDryRunSummary(book, metadata, structure)
            return
        }

        // Confirm before proceeding
        print("\nProceed with creating repository? (yes/no): ")
        val confirm = readLine()?.lowercase()
        if (confirm != "yes") {
            println("Cancelled. Book remains in 'processing' status.")
            return
        }

        // Create repository
        val success = createRepository(book, metadata, structure)

        if (success) {
            // Update status to completed
            val updatedQueue = queue.updateStatus(book.id, BookStatus.COMPLETED)
            bookInputService.saveBooksQueue(File(queueFile), updatedQueue)
            println("\n✅ Book '${book.id}' completed!")

            // Clean up AI task files
            aiTaskService.clearBatchMetadataTasks()

            // Check for next book
            val nextBook = updatedQueue.getNextPending()
            if (nextBook != null) {
                println("\n📚 Next pending book: ${nextBook.chineseTitle} (${nextBook.id})")
                println("   Re-run this command to process it.")
            } else {
                println("\n🎉 All books in queue have been processed!")
            }
        } else {
            markAsError(queue, book, "Repository creation failed")
        }
    }

    /**
     * Create the GitHub repository and set up the book
     */
    private fun createRepository(
        book: QueuedBook,
        metadata: GeneratedMetadata,
        structure: DocsStructure,
    ): Boolean {
        val username = ghService.getUsername()
        if (username == null) {
            println("Error: Could not get GitHub username")
            return false
        }

        // Step 1: Create GitHub repo
        println("\nStep 1: Creating GitHub repository...")
        if (ghService.repoExists(username, metadata.repoName)) {
            println("  Repository already exists: ${metadata.repoName}")
            print("  Continue with cloning and updating? (yes/no): ")
            val confirm = readLine()?.lowercase()
            if (confirm != "yes") {
                return false
            }
        } else {
            if (!ghService.createRepo(metadata.repoName, metadata.description)) {
                println("Error: Failed to create repository")
                return false
            }
            println("  Repository created: ${metadata.repoName}")
            Thread.sleep(2000)
        }

        // Configure repository
        val homepageUrl = "${AppConfig.homepageBaseUrl}/${metadata.repoName}"
        ghService.setHomepage(username, metadata.repoName, homepageUrl)
        println("  Homepage set: $homepageUrl")

        ghService.enableGitHubPages(username, metadata.repoName)
        println("  GitHub Pages enabled")

        ghService.addTopics(username, metadata.repoName, metadata.topics)
        println("  Topics added")

        ghService.starRepo(username, metadata.repoName)
        println("  Repository starred")

        // Step 2: Clone repository
        println("\nStep 2: Cloning repository...")
        val categoryDir = File(AppConfig.defaultWorkDir, metadata.category)
        if (!categoryDir.exists()) {
            categoryDir.mkdirs()
        }

        val repoDir = File(categoryDir, metadata.repoName)
        if (!repoDir.exists()) {
            if (!ghService.cloneRepo(username, metadata.repoName, repoDir.absolutePath)) {
                println("Error: Failed to clone repository")
                return false
            }
        }
        println("  Cloned to: ${repoDir.absolutePath}")

        // Step 3: Update template files
        println("\nStep 3: Updating template files...")
        updateTemplateFiles(repoDir, metadata, book)

        // Step 4: Download cover image
        println("\nStep 4: Processing cover image...")
        val coverFile = File(repoDir, "site/content/cover.png")
        if (!imageService.downloadAndResize(book.coverUrl, coverFile)) {
            println("  Warning: Failed to download cover image")
        }

        // Step 5: Create docs structure
        println("\nStep 5: Creating docs folder structure...")
        val createdCount = docsStructureService.createDocsStructure(repoDir, structure)
        println("  Created $createdCount items")

        // Print success summary
        printSuccessSummary(username, metadata, repoDir)

        return true
    }

    private fun updateTemplateFiles(
        repoDir: File,
        metadata: GeneratedMetadata,
        book: QueuedBook,
    ) {
        val oldSlug = "hugo-book-template"
        val oldEnTitle = "Hugo Book Template"
        val oldZhTitle = "讀書筆記模版"

        // Update README.md
        updateFile(
            File(repoDir, "README.md"),
            mapOf(
                oldSlug to metadata.repoName,
                oldEnTitle to metadata.englishTitle,
                oldZhTitle to metadata.chineseTitle,
            ),
        )

        // Update settings.gradle.kts
        updateFile(
            File(repoDir, "settings.gradle.kts"),
            mapOf(oldSlug to metadata.repoName),
        )

        // Update site/hugo.toml
        updateFile(
            File(repoDir, "site/hugo.toml"),
            mapOf(
                oldZhTitle to metadata.chineseTitle,
                oldSlug to metadata.repoName,
            ),
        )

        // Update site/content/_index.md
        updateIndexFile(repoDir, metadata, book)

        // Update site/go.mod
        updateGoMod(repoDir, metadata.repoName)
    }

    private fun updateFile(
        file: File,
        replacements: Map<String, String>,
    ) {
        if (!file.exists()) {
            println("  Warning: File not found: ${file.path}")
            return
        }

        var content = file.readText()
        for ((old, new) in replacements) {
            content = content.replace(old, new)
        }
        file.writeText(content)
        println("  Updated: ${file.name}")
    }

    private fun updateIndexFile(
        repoDir: File,
        metadata: GeneratedMetadata,
        book: QueuedBook,
    ) {
        val indexFile = File(repoDir, "site/content/_index.md")
        if (!indexFile.exists()) {
            println("  Warning: _index.md not found")
            return
        }

        val oldZhTitle = "讀書筆記模版"

        var content = indexFile.readText()
        content =
            content
                .replace("title: \"$oldZhTitle\"", "title: \"${metadata.chineseTitle}\"")
                .replace("title=\"$oldZhTitle\"", "title=\"${metadata.chineseTitle}\"")
                .replace("author=\"待填寫作者\"", "author=\"${book.author}\"")
                .replace("date=\"待填寫日期\"", "date=\"${book.publicationDate}\"")
                .replace("link=\"https://www.amazon.com/\"", "link=\"${book.purchaseUrl}\"")

        indexFile.writeText(content)
        println("  Updated: _index.md")
    }

    private fun updateGoMod(
        repoDir: File,
        repoName: String,
    ) {
        val goModFile = File(repoDir, "site/go.mod")
        if (!goModFile.exists()) {
            println("  Warning: go.mod not found")
            return
        }

        var content = goModFile.readText()
        content =
            content.replace(
                Regex("module github\\.com/[^/]+/[^\\s]+"),
                "module github.com/${AppConfig.githubUsername}/$repoName",
            )
        goModFile.writeText(content)
        println("  Updated: go.mod")
    }

    private fun markAsError(
        queue: BooksQueue,
        book: QueuedBook,
        message: String,
    ) {
        val updatedQueue = queue.updateStatus(book.id, BookStatus.ERROR, message)
        bookInputService.saveBooksQueue(File(queueFile), updatedQueue)
        println("  Marked as error: $message")
    }

    private fun resetBook(
        queue: BooksQueue,
        id: String,
    ) {
        val book = queue.getById(id)
        if (book == null) {
            println("Error: Book with ID '$id' not found in queue")
            return
        }

        val updatedQueue = queue.updateStatus(id, BookStatus.PENDING)
        bookInputService.saveBooksQueue(File(queueFile), updatedQueue)

        // Also clear any pending AI tasks
        aiTaskService.clearBatchMetadataTasks()

        println("✅ Reset book '$id' to pending status")
    }

    private fun printHeader() {
        println("=".repeat(70))
        println("Hugo Book Manager - Initialize Books from Queue")
        println("=".repeat(70))
    }

    private fun checkPrerequisites(): Boolean {
        println("\nChecking prerequisites...")

        print("  GitHub CLI... ")
        if (!ghService.isGhInstalled()) {
            println("NOT FOUND")
            return false
        }
        println("OK")

        print("  GitHub authentication... ")
        if (!ghService.isAuthenticated()) {
            println("NOT AUTHENTICATED")
            return false
        }
        println("OK")

        return true
    }

    private fun printQueueStatus(queue: BooksQueue) {
        println("\n📊 Queue Status")
        println("─".repeat(50))

        val summary = queue.summary()
        println("  Total:      ${queue.books.size} book(s)")
        println("  Pending:    ${summary[BookStatus.PENDING] ?: 0}")
        println("  Processing: ${summary[BookStatus.PROCESSING] ?: 0}")
        println("  Completed:  ${summary[BookStatus.COMPLETED] ?: 0}")
        println("  Error:      ${summary[BookStatus.ERROR] ?: 0}")

        println("\n📚 Books:")
        queue.books.forEach { book ->
            val statusIcon =
                when (book.status) {
                    BookStatus.PENDING -> "⏳"
                    BookStatus.PROCESSING -> "🔄"
                    BookStatus.COMPLETED -> "✅"
                    BookStatus.ERROR -> "❌"
                }
            println("  $statusIcon ${book.id}: ${book.chineseTitle}")
            if (book.errorMessage != null) {
                println("     Error: ${book.errorMessage}")
            }
        }
    }

    private fun printDryRunSummary(
        book: QueuedBook,
        metadata: GeneratedMetadata,
        structure: DocsStructure,
    ) {
        println("\n[DRY RUN] Would perform the following steps:")
        println("  1. Create GitHub repo: ${metadata.repoName}")
        println("  2. Clone to: ${AppConfig.defaultWorkDir}/${metadata.category}/${metadata.repoName}")
        println("  3. Update template files")
        println("  4. Download cover from: ${book.coverUrl}")
        println("  5. Create ${structure.sections.size} doc sections")
    }

    private fun printSuccessSummary(
        username: String,
        metadata: GeneratedMetadata,
        repoDir: File,
    ) {
        println()
        println("=".repeat(70))
        println("Book Repository Created Successfully!")
        println("=".repeat(70))
        println()
        println("  Book: ${metadata.chineseTitle}")
        println("  Repository: https://github.com/$username/${metadata.repoName}")
        println("  Website: ${AppConfig.homepageBaseUrl}/${metadata.repoName}")
        println("  Local Path: ${repoDir.absolutePath}")
        println()
        println("Next steps:")
        println("  1. cd ${repoDir.absolutePath}")
        println("  2. Review and edit the generated content")
        println("  3. git add . && git commit -m 'Initial content' && git push")
        println()
    }
}
