package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.RepoIndexLinter
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

        reportLint(RepoIndexLinter.lint(index))
    }

    /**
     * The description is edited on GitHub, outside anything this tool controls,
     * so the freshly-pulled index is the only moment we can reconcile it.
     * Findings are printed, never auto-fixed — every one of them needs a human
     * to decide the correct title, author, or which duplicate repo to keep.
     */
    private fun reportLint(findings: List<RepoIndexLinter.Finding>) {
        if (findings.isEmpty()) {
            println("✅ Description lint: no findings")
            return
        }
        val bySeverity = findings.groupBy { it.severity }
        val errors = bySeverity[RepoIndexLinter.Severity.ERROR].orEmpty()
        val warnings = bySeverity[RepoIndexLinter.Severity.WARNING].orEmpty()
        println("\nDescription lint: ${errors.size} error(s), ${warnings.size} warning(s)")
        for (finding in errors + warnings) {
            val tag = if (finding.severity == RepoIndexLinter.Severity.ERROR) "ERROR" else "warn "
            println("  $tag ${finding.repo} [${finding.code}] ${finding.detail}")
        }
    }
}
