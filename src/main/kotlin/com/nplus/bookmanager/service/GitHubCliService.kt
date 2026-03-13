package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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

    @Serializable
    private data class GhRepoInfoResponse(
        val homepageUrl: String = "",
        val repositoryTopics: List<GhTopicNode> = emptyList(),
    )

    @Serializable
    private data class GhTopicNode(
        val name: String = "",
    )

    /**
     * Get repository info (homepage, topics)
     */
    fun getRepoInfo(
        username: String,
        repoName: String,
    ): RepoInfo? {
        val output =
            ProcessRunner.executeForOutput(
                "gh repo view $username/$repoName --json homepageUrl,repositoryTopics",
            ) ?: return null

        return try {
            val response = json.decodeFromString<GhRepoInfoResponse>(output)
            RepoInfo(
                homepageUrl = response.homepageUrl,
                topics = response.repositoryTopics.map { it.name },
            )
        } catch (e: Exception) {
            println("  Warning: Could not parse repo info: ${e.message}")
            null
        }
    }

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
     * Check if repository is starred
     */
    fun isStarred(
        username: String,
        repoName: String,
    ): Boolean = ProcessRunner.executeSuccessfully("gh api user/starred/$username/$repoName")

    /**
     * Star a repository
     */
    fun starRepo(
        username: String,
        repoName: String,
    ): Boolean =
        ProcessRunner.executeSuccessfully(
            "gh api user/starred/$username/$repoName -X PUT",
            description = "Starring repository...",
        )

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
     * Configure repository settings: homepage, topics, and star.
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
        starRepo(username, repoName)
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

    data class RepoInfo(
        val homepageUrl: String,
        val topics: List<String>,
    )

    /**
     * Data class representing a Pull Request
     */
    data class PullRequest(
        val number: Int,
        val title: String,
        val author: String,
        val repoFullName: String,
        val checksStatus: ChecksStatus,
    ) {
        val isRenovate: Boolean
            get() = author == "renovate[bot]" || author == "app/renovate"

        val isCiPassing: Boolean
            get() = checksStatus == ChecksStatus.PASSING
    }

    enum class ChecksStatus {
        PASSING,
        FAILING,
        PENDING,
        UNKNOWN,
    }

    /**
     * Get repository full name (owner/repo) from a local git directory
     */
    fun getRepoFullName(repoDir: java.io.File): String? =
        ProcessRunner
            .executeForOutput(
                "gh repo view --json nameWithOwner --jq '.nameWithOwner'",
                workingDir = repoDir,
            )?.trim()

    /**
     * List open Renovate PRs for a repository that have passing CI
     */
    fun listRenovatePrs(repoFullName: String): List<PullRequest> {
        val output =
            ProcessRunner.executeForOutput(
                "gh pr list --repo $repoFullName --author app/renovate --state open --json number,title,author,statusCheckRollup",
            ) ?: return emptyList()

        return try {
            val prs = json.parseToJsonElement(output).jsonArray
            prs.mapNotNull { prElement ->
                val pr = prElement.jsonObject
                val number = pr["number"]?.jsonPrimitive?.content?.toIntOrNull() ?: return@mapNotNull null
                val title = pr["title"]?.jsonPrimitive?.contentOrNull ?: ""
                val author =
                    pr["author"]
                        ?.jsonObject
                        ?.get("login")
                        ?.jsonPrimitive
                        ?.contentOrNull ?: ""

                // Parse status checks
                val statusChecks = pr["statusCheckRollup"]?.jsonArray ?: JsonArray(emptyList())
                val checksStatus = parseChecksStatus(statusChecks)

                PullRequest(
                    number = number,
                    title = title,
                    author = author,
                    repoFullName = repoFullName,
                    checksStatus = checksStatus,
                )
            }
        } catch (e: Exception) {
            println("  Warning: Could not parse PR list: ${e.message}")
            emptyList()
        }
    }

    private fun parseChecksStatus(statusChecks: JsonArray): ChecksStatus {
        if (statusChecks.isEmpty()) return ChecksStatus.UNKNOWN

        var hasFailure = false
        var hasPending = false

        for (check in statusChecks) {
            val conclusion = check.jsonObject["conclusion"]?.jsonPrimitive?.contentOrNull
            val status = check.jsonObject["status"]?.jsonPrimitive?.contentOrNull

            when {
                conclusion == "FAILURE" || conclusion == "CANCELLED" || conclusion == "TIMED_OUT" -> hasFailure = true
                status == "IN_PROGRESS" || status == "QUEUED" || status == "PENDING" -> hasPending = true
            }
        }

        return when {
            hasFailure -> ChecksStatus.FAILING
            hasPending -> ChecksStatus.PENDING
            else -> ChecksStatus.PASSING
        }
    }

    /**
     * Merge a pull request
     */
    fun mergePr(
        repoFullName: String,
        prNumber: Int,
        mergeMethod: String = "merge",
    ): Boolean {
        val result =
            ProcessRunner.execute(
                "gh pr merge $prNumber --repo $repoFullName --$mergeMethod --delete-branch",
                description = "Merging PR #$prNumber...",
            )

        if (!result.success) {
            println("  Error merging PR #$prNumber: ${result.stderr}")
        }
        return result.success
    }
}
