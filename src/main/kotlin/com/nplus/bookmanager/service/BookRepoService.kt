package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput
import java.io.File

/**
 * Service that encapsulates the book-repository creation workflow
 * used by InitBooksCommand.
 */
class BookRepoService(
    private val ghService: GitHubCliService = GitHubCliService(),
    private val templateService: TemplateService = TemplateService(),
    private val imageService: ImageService = ImageService(),
    private val docsStructureService: DocsStructureService = DocsStructureService(),
    private val gitService: GitService = GitService(),
) {
    data class CreateResult(
        val success: Boolean,
        val repoDir: File? = null,
    )

    /**
     * Full book-repository creation flow:
     * 1. Create (or reuse) the GitHub repo
     * 2. Clone locally under the category folder
     * 3. Update template files with metadata + book input
     * 4. Download & resize cover image
     * 5. Create docs folder structure
     *
     * @return CreateResult with the local repo directory on success
     */
    fun createBookRepository(
        metadata: GeneratedMetadata,
        bookInput: BookInput,
        structure: DocsStructure,
    ): CreateResult {
        val username =
            AppConfig.githubUsername.takeIf { it.isNotBlank() }
                ?: ghService.getUsername()
        if (username == null) {
            println("Error: Could not get GitHub username")
            return CreateResult(false)
        }

        // Step: Create GitHub repo
        if (!createGitHubRepo(username, metadata)) return CreateResult(false)

        // Step: Clone repository
        val repoDir = cloneRepository(username, metadata)
        if (repoDir == null) {
            println("Error: Failed to clone repository")
            return CreateResult(false)
        }

        // Step: Update template files
        println("\n  Updating template files...")
        templateService.updateTemplateFiles(repoDir, metadata, bookInput)

        // Step: Download and resize cover image
        if (bookInput.coverUrl.isNotBlank()) {
            println("\n  Processing cover image...")
            val coverFile = File(repoDir, TemplateService.COVER_IMAGE_PATH)
            if (!imageService.downloadAndResize(bookInput.coverUrl, coverFile)) {
                println("  Warning: Failed to download cover image")
            }
        } else {
            println("\n  Skipping cover image (no URL provided)")
        }

        // Step: Create docs structure
        println("\n  Creating docs folder structure...")
        val createdCount = docsStructureService.createDocsStructure(repoDir, structure)
        println("  Created $createdCount items")

        // Step: Commit and push initial content
        println("\n  Committing and pushing initial content...")
        if (!gitService.commitAndPush(repoDir, "feat: add initial book content")) {
            println("  Warning: Failed to commit and push initial content")
        } else {
            println("  Pushed to remote")
        }

        printSuccessSummary(username, metadata, repoDir)

        return CreateResult(true, repoDir)
    }

    private fun createGitHubRepo(
        username: String,
        metadata: GeneratedMetadata,
    ): Boolean {
        if (ghService.repoExists(username, metadata.repoName)) {
            println("  Repository already exists: ${metadata.repoName}")
            if (!UserInput.confirm("  Continue with cloning and updating?")) {
                println("Cancelled")
                return false
            }
        } else {
            if (!ghService.createRepo(username, metadata.repoName, metadata.description)) {
                println("Error: Failed to create repository")
                return false
            }
            println("  Repository created: ${metadata.repoName}")
            Thread.sleep(AppConfig.POST_CREATION_DELAY_MS)
        }

        // Configure repository
        val homepageUrl = "${AppConfig.homepageBaseUrl}/${metadata.repoName}/"
        ghService.configureRepository(username, metadata.repoName, homepageUrl, metadata.topics)
        println("  Repository configured (homepage, topics)")

        return true
    }

    private fun cloneRepository(
        username: String,
        metadata: GeneratedMetadata,
    ): File? {
        // new-books is intentionally kept flat: <workDir>/<repoName>.
        // The 3-tier (top/sub/leaf) layout only applies to books-done after
        // migrate-topic-tiers, not to in-progress repos under new-books.
        val workDir = File(AppConfig.defaultWorkDir)
        if (!workDir.exists()) {
            workDir.mkdirs()
        }

        val repoDir = File(workDir, metadata.repoName)

        if (repoDir.exists()) {
            println("  Directory already exists: ${repoDir.absolutePath}")
            return repoDir
        }

        if (!ghService.cloneRepo(username, metadata.repoName, repoDir.absolutePath)) {
            return null
        }

        println("  Cloned to: ${repoDir.absolutePath}")
        return repoDir
    }

    /**
     * Print docs structure to console (delegates to DocsStructureService).
     */
    fun printDocsStructure(structure: DocsStructure) {
        docsStructureService.printDocsStructure(structure)
    }

    private fun printSuccessSummary(
        username: String,
        metadata: GeneratedMetadata,
        repoDir: File,
    ) {
        println()
        CliFormatter.printSectionHeader("Book Repository Created Successfully!")
        println()
        println("  Book: ${metadata.chineseTitle}")
        println("  Repository: https://github.com/$username/${metadata.repoName}")
        println("  Website: ${AppConfig.homepageBaseUrl}/${metadata.repoName}")
        println("  Local Path: ${repoDir.absolutePath}")
        println()
        println("Next steps:")
        println("  1. cd ${repoDir.absolutePath}")
        println("  2. Review and edit the generated content")
        println()
    }
}
