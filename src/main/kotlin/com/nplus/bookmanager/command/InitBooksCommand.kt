package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BatchBookInput
import com.nplus.bookmanager.model.BookStatus
import com.nplus.bookmanager.model.BooksQueue
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.model.QueuedBook
import com.nplus.bookmanager.model.RepoIndex
import com.nplus.bookmanager.service.AiTaskService
import com.nplus.bookmanager.service.BookInputService
import com.nplus.bookmanager.service.BookRepoService
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.GitHubClient
import com.nplus.bookmanager.service.RepoIndexService
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput
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
class InitBooksCommand(
    private val bookInputService: BookInputService = BookInputService(),
    private val aiTaskService: AiTaskService = AiTaskService(),
    private val ghService: GitHubClient = GitHubCliService(),
    private val bookRepoService: BookRepoService = BookRepoService(ghService = ghService),
    private val repoIndexService: RepoIndexService = RepoIndexService(),
) : CliktCommand(name = "init-books") {
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
            handleStatusCommand(queue)
            return
        }

        // Handle --reset flag
        if (reset && bookId != null) {
            handleResetCommand(queue, bookId!!)
            return
        }

        // Check prerequisites
        if (!ghService.checkPrerequisites()) return

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
            BookStatus.PENDING -> {
                runPhase1(queue, targetBook)
            }

            BookStatus.PROCESSING -> {
                runPhase2(queue, targetBook)
            }

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

            BookStatus.DUPLICATE -> {
                println("  This book is marked as duplicate (repo already exists). Skipping.")
                if (bookId == null) {
                    val next = queue.getNextPending()
                    if (next != null) {
                        println("\n  Moving to next pending book...")
                        runPhase1(queue, next)
                    } else {
                        println("\n✅ All books in queue have been processed!")
                    }
                }
            }
        }
    }

    private fun handleStatusCommand(queue: BooksQueue) {
        printQueueStatus(queue)
    }

    private fun handleResetCommand(
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

    /**
     * Phase 1: Generate AI tasks for the book
     */
    private fun runPhase1(
        queue: BooksQueue,
        book: QueuedBook,
    ) {
        println("\n[Phase 1] Generating AI tasks...")

        // Consult cached repo index before invoking AI; saves a round-trip
        // to Claude when a matching repo already exists on the org.
        val existing = lookupRepoIndex(book.englishTitle)
        if (existing != null && handleExistingRepo(queue, book, existing)) {
            return
        }

        // Check if batch task already pending
        if (aiTaskService.hasPendingBatchMetadataTask()) {
            CliFormatter.printPendingTaskWarning(
                message = "AI task is already pending. Please ask Claude Code to process it.",
            )
            return
        }

        // Write batch metadata request (for single book)
        val batchInput =
            BatchBookInput(
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
        CliFormatter.printBatchMetadataTaskPrompt(
            taskFilePath = taskFile.path,
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
            CliFormatter.printPendingTaskWarning(
                message = "AI task not yet completed. Please ask Claude Code to process it.",
            )
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

        // Re-check index against AI-generated repoName. Catches "the-" variants
        // where the AI emitted (say) `power-of-habit` but the org already has
        // `the-power-of-habit` (or vice versa).
        val existing = lookupRepoIndex(book.englishTitle, metadata.repoName)
        if (existing != null && handleExistingRepo(queue, book, existing)) {
            return
        }

        println("\n  Generated metadata:")
        println("    Repo Name: ${metadata.repoName}")
        println("    Description: ${metadata.description}")
        println("    Top:    ${metadata.topCategory}")
        println("    Sub:    ${metadata.subCategory}")
        println("    Leaf:   ${metadata.leafCategory}")
        println("    Path:   ${metadata.repoName}")
        println("    Topics: ${metadata.topics.joinToString(", ")}")

        println("\n  Generated structure:")
        bookRepoService.printDocsStructure(structure)

        if (dryRun) {
            printDryRunSummary(book, metadata, structure)
            return
        }

        // Confirm before proceeding
        if (!UserInput.confirm("\nProceed with creating repository?")) {
            println("Cancelled. Book remains in 'processing' status.")
            return
        }

        // Create repository using shared service
        val createResult = bookRepoService.createBookRepository(metadata, book.toBookInput(), structure)

        if (createResult.success) {
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
     * Look up an existing repo in the cached index. Returns null on miss
     * or if the index file is empty / never refreshed.
     */
    private fun lookupRepoIndex(
        englishTitle: String,
        aiRepoName: String? = null,
    ): RepoIndex.RepoEntry? {
        val index = repoIndexService.load()
        if (index.repos.isEmpty()) return null
        val candidates = RepoIndexService.candidatesFor(englishTitle, aiRepoName)
        return repoIndexService.findExisting(index, candidates)
    }

    /**
     * Prompt the user when a matching repo is found in the index.
     *
     * Returns true if the situation was handled (book skipped or claimed,
     * caller should stop). Returns false if the user chose to ignore the
     * index and proceed with normal creation.
     */
    private fun handleExistingRepo(
        queue: BooksQueue,
        book: QueuedBook,
        entry: RepoIndex.RepoEntry,
    ): Boolean {
        println()
        println("⚠ Found existing repo on owner: ${entry.name}")
        println("    URL:    ${entry.url}")
        println("    Topics: ${entry.topics.joinToString(", ")}")
        println()
        println("How to proceed?")
        println("  1. skip   — mark this book completed, do nothing else")
        println("  2. claim  — clone the existing repo locally + mark completed")
        println("  3. force  — ignore index and create as a new repo")
        print("Choice [1/2/3] (default 1): ")
        val choice = readlnOrNull()?.trim().orEmpty().ifBlank { "1" }

        return when (choice) {
            "2", "claim" -> {
                claimExistingRepo(queue, book, entry)
            }

            "3", "force" -> {
                println("  Continuing with normal creation flow...")
                false
            }

            else -> {
                skipExistingRepo(queue, book, entry)
            }
        }
    }

    private fun skipExistingRepo(
        queue: BooksQueue,
        book: QueuedBook,
        entry: RepoIndex.RepoEntry,
    ): Boolean {
        if (!dryRun) {
            val updated = queue.updateStatus(book.id, BookStatus.COMPLETED)
            bookInputService.saveBooksQueue(File(queueFile), updated)
            aiTaskService.clearBatchMetadataTasks()
        }
        println("  ✅ Marked '${book.id}' as completed (existing repo: ${entry.name})")
        return true
    }

    private fun claimExistingRepo(
        queue: BooksQueue,
        book: QueuedBook,
        entry: RepoIndex.RepoEntry,
    ): Boolean {
        val owner =
            AppConfig.githubUsername.takeIf { it.isNotBlank() }
                ?: ghService.getUsername()
        if (owner.isNullOrBlank()) {
            println("  Error: cannot determine repo owner")
            return false
        }

        // new-books is flat: <workDir>/<repoName>. Three-tier layout is only
        // applied later by migrate-topic-tiers when moving to books-done.
        val targetDir = File(AppConfig.defaultWorkDir, entry.name)
        if (targetDir.exists()) {
            println("  Local folder already exists: ${targetDir.absolutePath} (skipping clone)")
        } else {
            targetDir.parentFile?.mkdirs()
            if (dryRun) {
                println("  [DRY RUN] Would clone ${entry.name} to ${targetDir.absolutePath}")
            } else if (!ghService.cloneRepo(owner, entry.name, targetDir.absolutePath)) {
                println("  ❌ Clone failed.")
                return false
            } else {
                println("  ✅ Cloned to: ${targetDir.absolutePath}")
            }
        }

        if (!dryRun) {
            val updated = queue.updateStatus(book.id, BookStatus.COMPLETED)
            bookInputService.saveBooksQueue(File(queueFile), updated)
            aiTaskService.clearBatchMetadataTasks()
        }
        println("  ✅ Marked '${book.id}' as completed (claimed existing repo)")
        return true
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

    private fun printHeader() {
        CliFormatter.printHeader("Hugo Book Manager - Initialize Books from Queue")
    }

    private fun printQueueStatus(queue: BooksQueue) {
        println("\n📊 Queue Status")
        CliFormatter.printDivider(50)

        val summary = queue.summary()
        println("  Total:      ${queue.books.size} book(s)")
        println("  Pending:    ${summary[BookStatus.PENDING] ?: 0}")
        println("  Processing: ${summary[BookStatus.PROCESSING] ?: 0}")
        println("  Completed:  ${summary[BookStatus.COMPLETED] ?: 0}")
        println("  Error:      ${summary[BookStatus.ERROR] ?: 0}")
        println("  Duplicate:  ${summary[BookStatus.DUPLICATE] ?: 0}")

        println("\n📚 Books:")
        queue.books.forEach { book ->
            val statusIcon =
                when (book.status) {
                    BookStatus.PENDING -> "⏳"
                    BookStatus.PROCESSING -> "🔄"
                    BookStatus.COMPLETED -> "✅"
                    BookStatus.ERROR -> "❌"
                    BookStatus.DUPLICATE -> "📂"
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
        println("  2. Clone to: ${AppConfig.defaultWorkDir}/${metadata.repoName}")
        println("  3. Update template files")
        println("  4. Download cover from: ${book.coverUrl}")
        println("  5. Create ${structure.sections.size} doc sections")
    }
}
