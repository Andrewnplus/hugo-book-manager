package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.RepoIndexService

/**
 * Refresh the cached snapshot of book repos for the configured owner.
 *
 * Run before `init-books` so duplicate detection knows about the latest
 * state on GitHub. Output goes to `templates/existing-repos.yaml`.
 */
class RefreshRepoIndexCommand(
    private val service: RepoIndexService = RepoIndexService(),
) : CliktCommand(name = "refresh-repo-index") {
    override fun help(context: Context) = "Refresh the cached index of book repos for the configured owner"

    override fun run() {
        val owner = AppConfig.githubUsername
        if (owner.isBlank()) {
            println("Error: GITHUB_USERNAME is not set in local.properties")
            return
        }

        println("Refreshing repo index for owner: $owner")
        val index =
            try {
                service.fetchFromGitHub(owner)
            } catch (e: Exception) {
                println("Error: ${e.message}")
                return
            }

        service.save(index)
        println("✅ Saved ${index.repos.size} repo(s) to ${service.indexFile.path}")
    }
}
