package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.nplus.bookmanager.service.GitService
import com.nplus.bookmanager.service.TemplateService
import java.io.File

/**
 * Command to batch update renovate.json in multiple repositories
 */
class UpdateRenovateCommand : CliktCommand(name = "update-renovate") {
    override fun help(context: com.github.ajalt.clikt.core.Context) = "Batch update renovate.json in multiple repositories"

    private val parentDir by option("--parent-dir", "-d", help = "Parent directory containing repos")
        .required()

    private val dryRun by option("--dry-run", "-n", help = "Simulate without making changes")
        .flag(default = false)

    private val noPush by option("--no-push", help = "Update files but don't commit or push (for manual review)")
        .flag(default = false)

    private val recursive by option("--recursive", "-r", help = "Recursively scan subdirectories for git repos")
        .flag(default = false)

    private val gitService = GitService()
    private val templateService = TemplateService()

    private var successCount = 0
    private var skippedCount = 0
    private val failedRepos = mutableListOf<String>()

    override fun run() {
        printHeader()

        val parentDirFile = File(parentDir)
        if (!parentDirFile.exists() || !parentDirFile.isDirectory) {
            println("Error: Directory not found: $parentDir")
            return
        }

        // Get all subdirectories that are git repos
        val repos = if (recursive) {
            findGitReposRecursively(parentDirFile)
        } else {
            parentDirFile
                .listFiles { file ->
                    file.isDirectory && gitService.isGitRepo(file)
                }?.toList() ?: emptyList()
        }

        if (repos.isEmpty()) {
            println("No git repositories found in $parentDir")
            return
        }

        println("Found ${repos.size} repositories")
        if (dryRun) {
            println("[DRY RUN MODE] - No actual changes will be made")
        }
        if (noPush) {
            println("[NO PUSH MODE] - Files will be updated but not committed or pushed")
        }
        println()

        // Process each repository
        for ((index, repo) in repos.withIndex()) {
            println("[${index + 1}/${repos.size}] ${repo.name}")
            processRepository(repo)
            println()
        }

        printSummary(repos.size)
    }

    private fun printHeader() {
        println("=".repeat(60))
        println("Hugo Book Manager - Update Renovate Configuration")
        println("=".repeat(60))
        println()
    }

    private fun findGitReposRecursively(dir: File): List<File> {
        val repos = mutableListOf<File>()
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                if (gitService.isGitRepo(file)) {
                    repos.add(file)
                } else {
                    // Recurse into subdirectories
                    repos.addAll(findGitReposRecursively(file))
                }
            }
        }
        return repos.sortedBy { it.absolutePath }
    }

    private fun processRepository(repoDir: File) {
        try {
            if (dryRun) {
                println("  [DRY RUN] Would perform:")
                println("    - git pull")
                println("    - Update .github/renovate.json")
                println("    - git add .github/renovate.json")
                println("    - git commit -m 'chore: update renovate.json'")
                println("    - git push")
                successCount++
                return
            }

            // 1. Git pull
            print("  Pulling latest... ")
            if (!gitService.pull(repoDir)) {
                println("FAILED")
                failedRepos.add(repoDir.name)
                return
            }
            println("OK")

            // 2. Update renovate.json
            print("  Updating renovate.json... ")
            if (!templateService.updateRenovateJson(repoDir)) {
                println("FAILED")
                failedRepos.add(repoDir.name)
                return
            }
            println("OK")

            // 3. Check if there are changes
            if (!gitService.hasChanges(repoDir)) {
                println("  No changes needed, skipping")
                skippedCount++
                return
            }

            // 4. If --no-push, stop here
            if (noPush) {
                println("  Updated (not committed)")
                successCount++
                return
            }

            // 5. Git add, commit, push
            print("  Committing and pushing... ")
            val success =
                gitService.commitAndPush(
                    repoDir,
                    "chore: update renovate.json",
                    ".github/renovate.json",
                )

            if (success) {
                println("OK")
                successCount++
            } else {
                println("FAILED")
                failedRepos.add(repoDir.name)
            }
        } catch (e: Exception) {
            println("  Error: ${e.message}")
            failedRepos.add(repoDir.name)
        }
    }

    private fun printSummary(totalRepos: Int) {
        println("=".repeat(60))
        println("Summary")
        println("=".repeat(60))
        println()
        println("Total repositories: $totalRepos")
        println("Updated: $successCount")
        println("Skipped (no changes): $skippedCount")
        println("Failed: ${failedRepos.size}")

        if (failedRepos.isNotEmpty()) {
            println("\nFailed repositories:")
            failedRepos.forEach { println("  - $it") }
        }

        if (dryRun) {
            println("\n[DRY RUN] No actual changes were made")
        }
        if (noPush) {
            println("\n[NO PUSH] Files updated but not committed. Review and push manually.")
        }
    }
}
