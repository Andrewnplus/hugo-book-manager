package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput
import java.io.File

class BookRepoService(
    private val ghService: GitHubClient = GitHubCliService(),
    private val templateService: TemplateWriter = TemplateService(),
    private val imageService: CoverImageFetcher = ImageService(),
    private val docsStructureService: DocsStructureWriter = DocsStructureService(),
    private val gitService: GitOperations = GitService(),
    private val config: BookRepoConfig = BookRepoConfig(),
    private val confirm: (String) -> Boolean = UserInput::confirm,
) {
    data class CreateResult(
        val success: Boolean,
        val repoDir: File? = null,
    )

    fun createBookRepository(
        metadata: GeneratedMetadata,
        bookInput: BookInput,
        structure: DocsStructure,
    ): CreateResult {
        val username =
            config.githubUsername.takeIf { it.isNotBlank() }
                ?: ghService.getUsername()
        if (username == null) {
            println("Error: Could not get GitHub username")
            return CreateResult(false)
        }

        if (!createGitHubRepo(username, metadata)) return CreateResult(false)

        val repoDir = cloneRepository(username, metadata)
        if (repoDir == null) {
            println("Error: Failed to clone repository")
            return CreateResult(false)
        }

        gitService.setHooksPath(repoDir)

        println("\n  Updating template files...")
        templateService.updateTemplateFiles(repoDir, metadata, bookInput)

        if (bookInput.coverUrl.isNotBlank()) {
            println("\n  Processing cover image...")
            val coverFile = File(repoDir, TemplateService.COVER_IMAGE_PATH)
            if (!imageService.downloadAndResize(bookInput.coverUrl, coverFile)) {
                println("  Warning: Failed to download cover image")
            }
        } else {
            println("\n  Skipping cover image (no URL provided)")
        }

        println("\n  Creating docs folder structure...")
        val createdCount = docsStructureService.createDocsStructure(repoDir, structure)
        println("  Created $createdCount items")

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
            if (!confirm("  Continue with cloning and updating?")) {
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

        val homepageUrl = "${config.homepageBaseUrl}/${metadata.repoName}/"
        reportConfiguration(ghService.configureRepository(username, metadata.repoName, homepageUrl, metadata.topics))

        return true
    }

    private fun reportConfiguration(result: ConfigureResult) {
        if (result.allSucceeded) {
            println("  Repository configured (homepage, topics, pages)")
            return
        }
        println("  Warning: repository only partly configured")
        if (!result.homepageSet) println("    - homepage not set")
        if (result.topicsSet < result.topicsRequested) {
            println("    - topics: only ${result.topicsSet} of ${result.topicsRequested} applied")
        }
        if (!result.pagesEnabled) println("    - GitHub Pages not enabled")
    }

    private fun cloneRepository(
        username: String,
        metadata: GeneratedMetadata,
    ): File? {
        val workDir = config.workDir
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
        println("  Website: ${config.homepageBaseUrl}/${metadata.repoName}")
        println("  Local Path: ${repoDir.absolutePath}")
        println()
        println("Next steps:")
        println("  1. cd ${repoDir.absolutePath}")
        println("  2. Review and edit the generated content")
        println()
    }
}
