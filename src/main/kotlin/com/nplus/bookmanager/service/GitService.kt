package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import java.io.File

/**
 * Service for Git operations
 */
class GitService : GitOperations {
    /**
     * Check if a directory is a Git repository
     */
    fun isGitRepo(dir: File): Boolean =
        File(dir, ".git").exists() ||
            ProcessRunner.executeSuccessfully("git rev-parse --git-dir", dir)

    /**
     * Point git hooks to the tracked .githooks directory, activating the
     * Spotless pre-commit hook shipped in the template.
     */
    override fun setHooksPath(
        repoDir: File,
        path: String,
    ): Boolean = ProcessRunner.executeSuccessfully("git config core.hooksPath $path", repoDir)

    /**
     * Pull latest changes
     */
    fun pull(repoDir: File): Boolean =
        ProcessRunner.executeSuccessfully(
            "git pull",
            repoDir,
            description = "Pulling latest changes...",
        )

    /**
     * Add files to staging
     */
    fun add(
        repoDir: File,
        vararg files: String,
    ): Boolean {
        val fileList = files.joinToString(" ") { "'${it.replace("'", "'\\''")}'" }
        return ProcessRunner.executeSuccessfully(
            "git add $fileList",
            repoDir,
            description = "Staging changes...",
        )
    }

    /**
     * Add all changes
     */
    fun addAll(repoDir: File): Boolean = ProcessRunner.executeSuccessfully("git add -A", repoDir)

    /**
     * Commit changes
     */
    fun commit(
        repoDir: File,
        message: String,
    ): Boolean {
        val escapedMessage = message.replace("\"", "\\\"")
        return ProcessRunner.executeSuccessfully(
            """git commit -m "$escapedMessage"""",
            repoDir,
            description = "Committing changes...",
        )
    }

    /**
     * Push to remote
     */
    fun push(repoDir: File): Boolean =
        ProcessRunner.executeSuccessfully(
            "git push",
            repoDir,
            description = "Pushing to remote...",
        )

    /**
     * Check if there are uncommitted changes
     */
    fun hasChanges(repoDir: File): Boolean {
        val result = ProcessRunner.executeForOutput("git status --porcelain", repoDir)
        return result?.isNotBlank() == true
    }

    /**
     * Full git workflow: add, commit, push
     */
    override fun commitAndPush(
        repoDir: File,
        message: String,
        vararg files: String,
    ): Boolean {
        if (files.isEmpty()) {
            if (!addAll(repoDir)) return false
        } else {
            if (!add(repoDir, *files)) return false
        }

        if (!commit(repoDir, message)) {
            // Check if there's nothing to commit
            val status = ProcessRunner.executeForOutput("git status --porcelain", repoDir)
            if (status.isNullOrBlank()) {
                println("  No changes to commit")
                return true
            }
            return false
        }

        return push(repoDir)
    }
}
