package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.json.Json

/**
 * Service for interacting with GitHub CLI (gh)
 */
class GitHubCliService : GitHubClient {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        /**
         * Build the `gh repo create` command line.
         *
         * Kept pure and separate from execution so the two things that have
         * actually regressed here can be tested without touching the network:
         * the explicit `owner/` prefix (dropping it once sent repos to the
         * wrong account) and the single-quote escaping of the description
         * (book titles contain apostrophes).
         */
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
    override fun getUsername(): String? = ProcessRunner.executeForOutput("gh api user --jq '.login'")?.trim()

    /**
     * Check if a repository exists
     */
    override fun repoExists(
        username: String,
        repoName: String,
    ): Boolean = ProcessRunner.executeSuccessfully("gh repo view $username/$repoName --json name")

    /**
     * Create a new repository from template
     */
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
     * Enable GitHub Pages with GitHub Actions as the build source.
     * Required by the shared `hugobook-build-deploy` workflow's default
     * `pages-artifact` deploy mode (uses `actions/deploy-pages`).
     */
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
     * Remove a topic from repository
     */
    fun removeTopic(
        username: String,
        repoName: String,
        topic: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo edit $username/$repoName --remove-topic $topic",
        )

    /**
     * Clone a repository
     */
    override fun cloneRepo(
        username: String,
        repoName: String,
        targetDir: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh repo clone $username/$repoName $targetDir",
            description = "Cloning repository...",
        )

    /**
     * Configure repository settings: homepage, topics, and GitHub Pages.
     * Pages is enabled with `build_type=workflow` so the first push to main
     * can deploy via `actions/deploy-pages` without a 404.
     */
    override fun configureRepository(
        username: String,
        repoName: String,
        homepageUrl: String,
        topics: List<String>,
    ) {
        setHomepage(username, repoName, homepageUrl)
        addTopics(username, repoName, topics)
        enableGitHubPages(username, repoName)
    }
}
