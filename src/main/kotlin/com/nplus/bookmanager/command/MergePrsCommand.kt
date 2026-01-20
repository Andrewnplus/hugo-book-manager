package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.GitHubCliService.ChecksStatus
import com.nplus.bookmanager.service.GitHubCliService.PullRequest
import com.nplus.bookmanager.service.GitService
import java.io.File

/**
 * Command to batch merge Renovate PRs across multiple repositories
 */
class MergePrsCommand : CliktCommand(name = "merge-prs") {
    override fun help(context: Context) = "Batch merge Renovate PRs with passing CI"

    private val parentDir by option("--parent-dir", "-d", help = "Parent directory containing repos")
        .required()

    private val mergeMethod by option("--merge-method", "-m", help = "Merge method: merge, squash, rebase")
        .default("merge")

    private val ghService = GitHubCliService()
    private val gitService = GitService()

    override fun run() {
        printHeader()

        // Check prerequisites
        if (!checkPrerequisites()) return

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

        // Collect all Renovate PRs with passing CI
        val allPrs = mutableListOf<PullRequest>()
        val skippedPrs = mutableListOf<Pair<PullRequest, String>>() // PR and reason

        for (repo in repos) {
            val repoFullName = ghService.getRepoFullName(repo)
            if (repoFullName == null) {
                println("  Skipping ${repo.name}: Could not determine repo name")
                continue
            }

            val prs = ghService.listRenovatePrs(repoFullName)
            for (pr in prs) {
                when (pr.checksStatus) {
                    ChecksStatus.PASSING -> allPrs.add(pr)
                    ChecksStatus.FAILING -> skippedPrs.add(pr to "CI failing")
                    ChecksStatus.PENDING -> skippedPrs.add(pr to "CI pending")
                    ChecksStatus.UNKNOWN -> skippedPrs.add(pr to "No CI status")
                }
            }
        }

        // Display results
        if (allPrs.isEmpty() && skippedPrs.isEmpty()) {
            println("No Renovate PRs found.")
            return
        }

        // Show PRs ready to merge
        if (allPrs.isNotEmpty()) {
            println("=".repeat(70))
            println("Renovate PRs ready to merge (CI passing): ${allPrs.size}")
            println("=".repeat(70))
            println()

            allPrs.forEachIndexed { index, pr ->
                println("  [${index + 1}] ${pr.repoFullName}")
                println("      #${pr.number}: ${pr.title}")
            }
            println()
        }

        // Show skipped PRs
        if (skippedPrs.isNotEmpty()) {
            println("-".repeat(70))
            println("Skipped PRs (${skippedPrs.size}):")
            println("-".repeat(70))
            skippedPrs.forEach { (pr, reason) ->
                println("  ${pr.repoFullName} #${pr.number}: $reason")
            }
            println()
        }

        if (allPrs.isEmpty()) {
            println("No PRs ready to merge.")
            return
        }

        // Confirm merge
        println("=".repeat(70))
        print("Merge all ${allPrs.size} PRs? (yes/no): ")
        val confirm = readLine()?.lowercase()

        if (confirm != "yes") {
            println("Cancelled.")
            return
        }

        // Execute merge
        println("\nMerging PRs...\n")
        var successCount = 0
        var failCount = 0

        for (pr in allPrs) {
            print("  Merging ${pr.repoFullName} #${pr.number}... ")
            val success = ghService.mergePr(pr.repoFullName, pr.number, mergeMethod)

            if (success) {
                println("OK")
                successCount++
            } else {
                println("FAILED")
                failCount++
            }

            // Rate limiting
            Thread.sleep(1000)
        }

        // Summary
        println()
        println("=".repeat(70))
        println("Complete!")
        println("=".repeat(70))
        println()
        println("  Merged: $successCount")
        println("  Failed: $failCount")
        println("  Skipped: ${skippedPrs.size}")
    }

    private fun printHeader() {
        println("=".repeat(70))
        println("Hugo Book Manager - Merge Renovate PRs")
        println("=".repeat(70))
        println()
    }

    private fun checkPrerequisites(): Boolean {
        println("Checking prerequisites...")

        print("  GitHub CLI... ")
        if (!ghService.isGhInstalled()) {
            println("NOT FOUND")
            return false
        }
        println("OK")

        print("  GitHub authentication... ")
        if (!ghService.isAuthenticated()) {
            println("NOT AUTHENTICATED")
            return false
        }
        println("OK")

        println()
        return true
    }
}
