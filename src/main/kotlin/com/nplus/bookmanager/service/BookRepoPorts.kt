package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import java.io.File

interface GitHubClient {
    fun checkPrerequisites(): Boolean

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

data class BookRepoConfig(
    val githubUsername: String = AppConfig.githubUsername,
    val homepageBaseUrl: String = AppConfig.homepageBaseUrl,
    val workDir: File = File(AppConfig.defaultWorkDir),
)
