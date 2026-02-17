package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.Book
import com.nplus.bookmanager.service.CsvService
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput

/**
 * Command to create GitHub repositories from a CSV file
 */
class CreateReposCommand(
    private val csvService: CsvService = CsvService(),
    private val ghService: GitHubCliService = GitHubCliService(),
) : CliktCommand(name = "create-repos") {
    override fun help(context: com.github.ajalt.clikt.core.Context) = "Create GitHub repositories from a CSV file"

    private val csvFile by option("--csv", "-c", help = "Path to CSV file")
        .required()

    private val dryRun by option("--dry-run", "-n", help = "Simulate without creating repos")
        .flag(default = false)

    private val startFrom by option("--start-from", "-s", help = "Start from book number N")
        .int()
        .default(1)

    private val updateOnly by option("--update-only", "-u", help = "Only update existing repos")
        .flag(default = false)

    private var successCount = 0
    private val failedBooks = mutableListOf<String>()

    override fun run() {
        printHeader()

        // Check prerequisites
        if (!ghService.checkPrerequisites()) return

        // Load books from CSV
        val books = loadBooks() ?: return

        // Confirm execution
        if (!confirmExecution(books.size)) return

        // Create repositories
        createAllRepos(books)

        // Print summary
        printSummary(books.size)
    }

    private fun printHeader() {
        CliFormatter.printHeader("Hugo Book Manager - Create Repos from CSV")
    }

    private fun loadBooks(): List<Book>? {
        println("\n  3. Loading CSV: $csvFile")

        return try {
            val books = csvService.loadBooks(csvFile)
            println("     Loaded ${books.size} books")
            books
        } catch (e: Exception) {
            println("     Error: ${e.message}")
            null
        }
    }

    private fun confirmExecution(bookCount: Int): Boolean {
        if (dryRun) {
            println("\n[DRY RUN MODE] - No actual changes will be made")
            return true
        }

        return UserInput.confirm("\nAbout to create $bookCount repositories. Continue?")
    }

    private fun createAllRepos(books: List<Book>) {
        println()
        CliFormatter.printSectionHeader("Creating Repositories")

        val username = ghService.getUsername()
        if (username == null) {
            println("Error: Could not get GitHub username")
            return
        }

        for ((index, book) in books.withIndex()) {
            val bookNum = index + 1

            if (bookNum < startFrom) {
                println("\nSkipping #$bookNum: ${book.chineseTitle}")
                continue
            }

            println("\nProgress: $bookNum/${books.size}")

            try {
                val success = createSingleRepo(username, book)
                if (success) {
                    successCount++
                } else {
                    failedBooks.add(book.chineseTitle)
                }
            } catch (e: Exception) {
                println("  Error: ${e.message}")
                failedBooks.add(book.chineseTitle)
            }

            // Rate limiting
            if (bookNum < books.size && !dryRun) {
                println("  Waiting ${AppConfig.REPO_CREATION_DELAY_MS / 1000} seconds...")
                Thread.sleep(AppConfig.REPO_CREATION_DELAY_MS)
            }
        }
    }

    private fun createSingleRepo(
        username: String,
        book: Book,
    ): Boolean {
        val repoName = book.repoName
        val description = book.description
        val topics = book.topics

        println()
        CliFormatter.printSectionHeader("Repo: $repoName\nTitle: ${book.chineseTitle}")

        if (dryRun) {
            println("[DRY RUN] Would perform:")
            println("  - Create private repo: $repoName")
            println("  - Description: ${description.take(50)}...")
            println("  - Topics: ${topics.take(5).joinToString(", ")}...")
            return true
        }

        // Check if repo exists
        val repoExists = ghService.repoExists(username, repoName)

        if (repoExists) {
            println("  Repository already exists, checking settings...")
            val repoInfo = ghService.getRepoInfo(username, repoName)

            // Update missing settings
            updateRepoSettings(username, repoName, book, repoInfo)
        } else if (updateOnly) {
            println("  Repository doesn't exist (update-only mode), skipping")
            return true
        } else {
            // Create new repository
            if (!ghService.createRepo(repoName, description)) {
                return false
            }
            println("  Repository created")
            Thread.sleep(AppConfig.API_CALL_DELAY_MS)

            // Configure the new repo
            val homepageUrl = "${AppConfig.homepageBaseUrl}/$repoName"
            ghService.configureRepository(username, repoName, homepageUrl, book.topics)
            println("  Repository configured")
        }

        return true
    }

    private fun updateRepoSettings(
        username: String,
        repoName: String,
        book: Book,
        existingInfo: GitHubCliService.RepoInfo?,
    ) {
        val homepageUrl = "${AppConfig.homepageBaseUrl}/$repoName"

        // Set homepage if different
        val currentHomepage = existingInfo?.homepageUrl ?: ""
        if (currentHomepage != homepageUrl) {
            if (ghService.setHomepage(username, repoName, homepageUrl)) {
                println("  Homepage set")
            }
        } else {
            println("  Homepage already configured")
        }

        // Enable GitHub Pages
        if (ghService.enableGitHubPages(username, repoName)) {
            println("  GitHub Pages enabled")
        } else {
            println("  GitHub Pages already enabled or skipped")
        }

        // Add missing topics
        val currentTopics = existingInfo?.topics ?: emptyList()
        val missingTopics = book.topics.filter { it !in currentTopics }

        if (missingTopics.isNotEmpty()) {
            println("  Adding ${missingTopics.size} topics...")
            val added = ghService.addTopics(username, repoName, missingTopics)
            println("  Added $added/${missingTopics.size} topics")
        } else {
            println("  All topics already set")
        }

        // Star the repo
        if (!ghService.isStarred(username, repoName)) {
            if (ghService.starRepo(username, repoName)) {
                println("  Repository starred")
            }
        } else {
            println("  Already starred")
        }
    }

    private fun printSummary(totalBooks: Int) {
        println()
        CliFormatter.printSectionHeader("Complete!")

        if (dryRun) {
            println("\n[DRY RUN] Would have processed $totalBooks books")
        } else {
            println("\nSuccess: $successCount/$totalBooks repositories")

            if (failedBooks.isNotEmpty()) {
                println("\nFailed books:")
                failedBooks.forEach { println("  - $it") }
            }
        }
    }
}
