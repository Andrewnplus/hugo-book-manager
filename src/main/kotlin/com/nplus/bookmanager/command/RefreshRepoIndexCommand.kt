package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.RepoIndexLinter
import com.nplus.bookmanager.service.RepoIndexService
import java.io.File

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

        reportLint(RepoIndexLinter.lint(index, purchaseLinks()))
    }

    /**
     * slug → `book.link` from each local clone's home page frontmatter.
     *
     * Read here rather than from the index because the index mirrors what the
     * GitHub API returns — name, description, topics — and the purchase link
     * lives in the repo's own content. Missing clones simply fall out of the
     * map; the linter degrades to matching on title and author for those.
     */
    private fun purchaseLinks(): Map<String, String> {
        val roots = listOf(AppConfig.booksDir, AppConfig.defaultWorkDir).map(::File).filter { it.isDirectory }
        val out = mutableMapOf<String, String>()
        for (root in roots) {
            root
                .walkTopDown()
                .maxDepth(5)
                .filter { it.isFile && it.name == "_index.md" && it.parentFile?.name == "content" }
                .forEach { file ->
                    val slug =
                        file.parentFile.parentFile
                            ?.parentFile
                            ?.name ?: return@forEach
                    // Only the `book:` map's link, not a stray `link:` elsewhere
                    // in the frontmatter; the two-space indent is what marks it.
                    file
                        .useLines { lines ->
                            lines.firstOrNull { it.startsWith("  link: ") }
                        }?.removePrefix("  link: ")
                        ?.trim()
                        ?.trim('"')
                        ?.takeIf { it.isNotBlank() }
                        ?.let { out[slug] = it }
                }
        }
        return out
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
