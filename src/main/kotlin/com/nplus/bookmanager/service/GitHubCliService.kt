package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.json.Json

class GitHubCliService : GitHubClient {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        internal fun buildCreateRepoCommand(
            owner: String,
            repoName: String,
            description: String,
            templateRepo: String,
            isPrivate: Boolean,
        ): String {
            val visibility = if (isPrivate) "--private" else "--public"
            val escapedDesc = description.replace("'", "'\\''")
            return "gh repo create $owner/$repoName --template $templateRepo $visibility --description '$escapedDesc'"
        }
    }

    fun isGhInstalled(): Boolean = ProcessRunner.executeSuccessfully("gh --version")

    fun isAuthenticated(): Boolean = ProcessRunner.executeSuccessfully("gh auth status")

    override fun checkPrerequisites(): Boolean {
        println("\nChecking prerequisites...")

        print("  GitHub CLI... ")
        if (!isGhInstalled()) {
            println("NOT FOUND")
            return false
        }
        println("OK")

        print("  GitHub authentication... ")
        if (!isAuthenticated()) {
            println("NOT AUTHENTICATED")
            return false
        }
        println("OK")

        return true
    }

    override fun getUsername(): String? = ProcessRunner.executeForOutput("gh api user --jq '.login'")?.trim()

    override fun repoExists(
        username: String,
        repoName: String,
    ): Boolean = ProcessRunner.executeSuccessfully("gh repo view $username/$repoName --json name")

    override fun createRepo(
        owner: String,
        repoName: String,
        description: String,
        templateRepo: String,
        isPrivate: Boolean,
    ): Boolean {
        val cmd = buildCreateRepoCommand(owner, repoName, description, templateRepo, isPrivate)

        val result = ProcessRunner.execute(cmd, description = "Creating repository...")
        if (!result.success) {
            println("  Error: ${result.stderr}")
        }
        return result.success
    }

    fun setHomepage(
        username: String,
        repoName: String,
        homepageUrl: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --homepage '$homepageUrl'",
            description = "Setting homepage URL...",
        )

    fun enableGitHubPages(
        username: String,
        repoName: String,
    ): Boolean {
        val result =
            ProcessRunner.execute(
                "gh api repos/$username/$repoName/pages -X POST -F build_type=workflow",
                description = "Enabling GitHub Pages...",
            )

        return if (result.success) {
            true
        } else {
            result.stderr.contains("already enabled") || result.stderr.contains("422")
        }
    }

    fun addTopic(
        username: String,
        repoName: String,
        topic: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --add-topic $topic",
        )

    fun addTopics(
        username: String,
        repoName: String,
        topics: List<String>,
    ): Int {
        var successCount = 0
        for (topic in topics) {
            if (addTopic(username, repoName, topic)) {
                successCount++
            }
            Thread.sleep(AppConfig.TOPIC_API_DELAY_MS)
        }
        return successCount
    }

    fun removeTopic(
        username: String,
        repoName: String,
        topic: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --remove-topic $topic",
        )

    override fun cloneRepo(
        username: String,
        repoName: String,
        targetDir: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo clone $username/$repoName $targetDir",
            description = "Cloning repository...",
        )

    override fun configureRepository(
        username: String,
        repoName: String,
        homepageUrl: String,
        topics: List<String>,
    ): ConfigureResult =
        ConfigureResult(
            homepageSet = setHomepage(username, repoName, homepageUrl),
            topicsSet = addTopics(username, repoName, topics),
            topicsRequested = topics.size,
            pagesEnabled = enableGitHubPages(username, repoName),
        )
}
