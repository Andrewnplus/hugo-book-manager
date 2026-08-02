package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import java.io.File

/**
 * The collaborators [BookRepoService] talks to, narrowed to just the calls it
 * makes.
 *
 * BookRepoService already took its collaborators through the constructor, but
 * the concrete services are final classes, so no test could ever supply a
 * stand-in — the seam looked real and was not. Each port below is the slice of
 * one service that the creation flow actually uses; the concrete services
 * implement them unchanged, and every other caller keeps using those services
 * directly.
 */
interface GitHubClient {
    fun getUsername(): String?

    fun repoExists(
        username: String,
        repoName: String,
    ): Boolean

    fun createRepo(
        owner: String,
        repoName: String,
        description: String,
        templateRepo: String = AppConfig.templateRepo,
        isPrivate: Boolean = true,
    ): Boolean

    fun cloneRepo(
        username: String,
        repoName: String,
        targetDir: String,
    ): Boolean

    fun configureRepository(
        username: String,
        repoName: String,
        homepageUrl: String,
        topics: List<String>,
    ): ConfigureResult
}

/**
 * What post-creation configuration actually managed to apply.
 *
 * Returned rather than discarded because topics are how the portal files a
 * book: `addTopics` already counted its successes, but the count went nowhere,
 * so a repo that got 2 of its 6 topics still printed "configured" and then
 * quietly failed to appear under its category.
 */
data class ConfigureResult(
    val homepageSet: Boolean,
    val topicsSet: Int,
    val topicsRequested: Int,
    val pagesEnabled: Boolean,
) {
    val allSucceeded: Boolean get() = homepageSet && topicsSet == topicsRequested && pagesEnabled
}

interface TemplateWriter {
    fun updateTemplateFiles(
        repoDir: File,
        metadata: GeneratedMetadata,
        bookInput: BookInput,
    ): Boolean
}

interface CoverImageFetcher {
    fun downloadAndResize(
        imageUrl: String,
        outputFile: File,
    ): Boolean
}

interface DocsStructureWriter {
    fun createDocsStructure(
        repoDir: File,
        structure: DocsStructure,
        clearExisting: Boolean = true,
    ): Int

    fun printDocsStructure(structure: DocsStructure)
}

interface GitOperations {
    fun setHooksPath(
        repoDir: File,
        path: String = ".githooks",
    ): Boolean

    fun commitAndPush(
        repoDir: File,
        message: String,
        vararg files: String,
    ): Boolean
}

/**
 * The `local.properties` values the creation flow reads.
 *
 * Held as constructor state rather than read from [AppConfig] at each use, so
 * a test can drive the flow without a `local.properties` on disk deciding the
 * outcome — the username in particular changes which branch runs.
 */
data class BookRepoConfig(
    val githubUsername: String = AppConfig.githubUsername,
    val homepageBaseUrl: String = AppConfig.homepageBaseUrl,
    val workDir: File = File(AppConfig.defaultWorkDir),
)
