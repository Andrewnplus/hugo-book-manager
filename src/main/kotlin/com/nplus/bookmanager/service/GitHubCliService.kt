package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Service for interacting with GitHub CLI (gh)
 */
class GitHubCliService {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Check if GitHub CLI is installed
     */
    fun isGhInstalled(): Boolean = ProcessRunner.executeSuccessfully("gh --version")

    /**
     * Check if user is authenticated with GitHub
     */
    fun isAuthenticated(): Boolean = ProcessRunner.executeSuccessfully("gh auth status")

    /**
     * Check GitHub CLI and authentication prerequisites.
     * Prints status messages and returns false if any check fails.
     */
    fun checkPrerequisites(): Boolean {
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

    /**
     * Get the current authenticated GitHub username
     */
    fun getUsername(): String? = ProcessRunner.executeForOutput("gh api user --jq '.login'")?.trim()

    /**
     * Check if a repository exists
     */
    fun repoExists(
        username: String,
        repoName: String,
    ): Boolean = ProcessRunner.executeSuccessfully("gh repo view $username/$repoName --json name")

    /**
     * Create a new repository from template
     */
    fun createRepo(
        repoName: String,
        description: String,
        templateRepo: String = AppConfig.templateRepo,
        isPrivate: Boolean = true,
    ): Boolean {
        val visibility = if (isPrivate) "--private" else "--public"
        val escapedDesc = description.replace("'", "'\\''")
        val cmd = "gh repo create $repoName --template $templateRepo $visibility --description '$escapedDesc'"

        val result = ProcessRunner.execute(cmd, description = "Creating repository...")
        if (!result.success) {
            println("  Error: ${result.stderr}")
        }
        return result.success
    }

    /**
     * Set repository homepage
     */
    fun setHomepage(
        username: String,
        repoName: String,
        homepageUrl: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --homepage '$homepageUrl'",
            description = "Setting homepage URL...",
        )

    /**
     * Enable GitHub Pages from gh-pages branch
     */
    fun enableGitHubPages(
        username: String,
        repoName: String,
    ): Boolean {
        val result =
            ProcessRunner.execute(
                "gh api repos/$username/$repoName/pages -X POST -f source[branch]=gh-pages -f source[path]=/",
                description = "Enabling GitHub Pages...",
            )

        return if (result.success) {
            true
        } else {
            // Pages might already be enabled
            result.stderr.contains("already enabled") || result.stderr.contains("422")
        }
    }

    /**
     * Add a topic to repository
     */
    fun addTopic(
        username: String,
        repoName: String,
        topic: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --add-topic $topic",
        )

    /**
     * Add multiple topics to repository
     */
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
            Thread.sleep(AppConfig.TOPIC_API_DELAY_MS) // Rate limiting
        }
        return successCount
    }

    /**
     * Clone a repository
     */
    fun cloneRepo(
        username: String,
        repoName: String,
        targetDir: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo clone $username/$repoName $targetDir",
            description = "Cloning repository...",
        )

    /**
     * Configure repository settings: homepage and topics.
     * Note: GitHub Pages is NOT enabled here because the gh-pages branch
     * does not exist until after the first push triggers GitHub Actions.
     * Use enableGitHubPages() separately after pushing and waiting for the branch.
     */
    fun configureRepository(
        username: String,
        repoName: String,
        homepageUrl: String,
        topics: List<String>,
    ) {
        setHomepage(username, repoName, homepageUrl)
        addTopics(username, repoName, topics)
    }

    /**
     * Wait for a branch to appear on the remote repository.
     * Useful for waiting for gh-pages branch after GitHub Actions build.
     *
     * @return true if the branch appeared within the timeout
     */
    fun waitForBranch(
        username: String,
        repoName: String,
        branch: String,
        maxWaitSeconds: Int = 120,
        pollIntervalSeconds: Int = 10,
    ): Boolean {
        val maxAttempts = maxWaitSeconds / pollIntervalSeconds
        for (i in 1..maxAttempts) {
            val result =
                ProcessRunner.execute(
                    "gh api repos/$username/$repoName/branches/$branch --jq .name",
                    description = "Checking for $branch branch (attempt $i/$maxAttempts)...",
                )
            if (result.success && result.stdout.trim() == branch) {
                return true
            }
            if (i < maxAttempts) {
                Thread.sleep(pollIntervalSeconds * 1000L)
            }
        }
        return false
    }
}
