package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.GitHubCliService.ChecksStatus
import com.nplus.bookmanager.service.GitHubCliService.PullRequest
import com.nplus.bookmanager.service.GitService
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.UserInput
import java.io.File

/**
 * Command to batch merge Renovate PRs across multiple repositories
 */
class MergePrsCommand(
    private val ghService: GitHubCliService = GitHubCliService(),
    private val gitService: GitService = GitService(),
) : CliktCommand(name = "merge-prs") {
    override fun help(context: Context) = "Batch merge Renovate PRs with passing CI"

    private val parentDir by option("--parent-dir", "-d", help = "Parent directory containing repos")
        .required()

    private val mergeMethod by option("--merge-method", "-m", help = "Merge method: merge, squash, rebase")
        .default("merge")

    override fun run() {
        printHeader()

        // Check prerequisites
        if (!ghService.checkPrerequisites()) return

        // Scan for repos
        val parentDirFile = File(parentDir)
        if (!parentDirFile.exists() || !parentDirFile.isDirectory) {
            println("Error: Directory not found: $parentDir")
            return
        }

        val repos =
            parentDirFile
                .listFiles { file -> file.isDirectory && gitService.isGitRepo(file) }
                ?.toList() ?: emptyList()

        if (repos.isEmpty()) {
            println("No git repositories found in $parentDir")
            return
        }

        println("Scanning ${repos.size} repositories for Renovate PRs...\n")

        val (readyPrs, skippedPrs) = collectPrs(repos)

        if (readyPrs.isEmpty() && skippedPrs.isEmpty()) {
            println("No Renovate PRs found.")
            return
        }

        displayPrSummary(readyPrs, skippedPrs)

        if (readyPrs.isEmpty()) {
            println("No PRs ready to merge.")
            return
        }

        // Confirm merge
        println()
        if (!UserInput.confirm("Merge all ${readyPrs.size} PRs?")) {
            println("Cancelled.")
            return
        }

        val (successCount, failCount) = executeMerges(readyPrs)

        // Summary
        println()
        CliFormatter.printSectionHeader("Complete!")
        println()
        println("  Merged: $successCount")
        println("  Failed: $failCount")
        println("  Skipped: ${skippedPrs.size}")
    }

    private fun collectPrs(repos: List<File>): Pair<List<PullRequest>, List<Pair<PullRequest, String>>> {
        val readyPrs = mutableListOf<PullRequest>()
        val skippedPrs = mutableListOf<Pair<PullRequest, String>>()

        for (repo in repos) {
            val repoFullName = ghService.getRepoFullName(repo)
            if (repoFullName == null) {
                println("  Skipping ${repo.name}: Could not determine repo name")
                continue
            }

            val prs = ghService.listRenovatePrs(repoFullName)
            for (pr in prs) {
                when (pr.checksStatus) {
                    ChecksStatus.PASSING -> readyPrs.add(pr)
                    ChecksStatus.FAILING -> skippedPrs.add(pr to "CI failing")
                    ChecksStatus.PENDING -> skippedPrs.add(pr to "CI pending")
                    ChecksStatus.UNKNOWN -> skippedPrs.add(pr to "No CI status")
                }
            }
        }

        return readyPrs to skippedPrs
    }

    private fun displayPrSummary(
        readyPrs: List<PullRequest>,
        skippedPrs: List<Pair<PullRequest, String>>,
    ) {
        if (readyPrs.isNotEmpty()) {
            CliFormatter.printSectionHeader("Renovate PRs ready to merge (CI passing): ${readyPrs.size}")
            println()

            readyPrs.forEachIndexed { index, pr ->
                println("  [${index + 1}] ${pr.repoFullName}")
                println("      #${pr.number}: ${pr.title}")
            }
            println()
        }

        if (skippedPrs.isNotEmpty()) {
            CliFormatter.printLightDivider()
            println("Skipped PRs (${skippedPrs.size}):")
            CliFormatter.printLightDivider()
            skippedPrs.forEach { (pr, reason) ->
                println("  ${pr.repoFullName} #${pr.number}: $reason")
            }
            println()
        }
    }

    private fun executeMerges(readyPrs: List<PullRequest>): Pair<Int, Int> {
        println("\nMerging PRs...\n")
        var successCount = 0
        var failCount = 0

        for (pr in readyPrs) {
            print("  Merging ${pr.repoFullName} #${pr.number}... ")
            val success = ghService.mergePr(pr.repoFullName, pr.number, mergeMethod)

            if (success) {
                println("OK")
                successCount++
            } else {
                println("FAILED")
                failCount++
            }

            Thread.sleep(AppConfig.API_CALL_DELAY_MS)
        }

        return successCount to failCount
    }

    private fun printHeader() {
        CliFormatter.printHeader("Hugo Book Manager - Merge Renovate PRs")
        println()
    }
}
