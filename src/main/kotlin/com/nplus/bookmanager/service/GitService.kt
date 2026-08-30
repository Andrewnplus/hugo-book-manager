package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import java.io.File

class GitService : GitOperations {
    fun isGitRepo(dir: File): Boolean =
        File(dir, ".git").exists() ||
            ProcessRunner.executeSuccessfully("git rev-parse --git-dir", dir)

    override fun setHooksPath(
        repoDir: File,
        path: String,
    ): Boolean = ProcessRunner.executeSuccessfully("git config core.hooksPath $path", repoDir)

    fun pull(repoDir: File): Boolean =
        ProcessRunner.executeSuccessfully(
            "git pull",
            repoDir,
            description = "Pulling latest changes...",
        )

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

    fun addAll(repoDir: File): Boolean = ProcessRunner.executeSuccessfully("git add -A", repoDir)

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

    fun push(repoDir: File): Boolean =
        ProcessRunner.executeSuccessfully(
            "git push",
            repoDir,
            description = "Pushing to remote...",
        )

    fun hasChanges(repoDir: File): Boolean {
        val result = ProcessRunner.executeForOutput("git status --porcelain", repoDir)
        return result?.isNotBlank() == true
    }

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
