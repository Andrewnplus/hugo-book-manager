package com.nplus.bookmanager.util

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Utility for executing external shell commands
 */
object ProcessRunner {
    data class CommandResult(
        val success: Boolean,
        val stdout: String,
        val stderr: String,
        val exitCode: Int,
    )

    /**
     * Execute a shell command and return the result
     *
     * @param command The command to execute
     * @param workingDir Optional working directory
     * @param timeoutSeconds Timeout in seconds (default 60)
     * @param description Optional description for logging
     */
    fun execute(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
        description: String? = null,
    ): CommandResult {
        if (description != null) {
            println("  $description")
        }

        return try {
            val processBuilder = ProcessBuilder("/bin/sh", "-c", command)

            workingDir?.let { processBuilder.directory(it) }

            // Inherit environment
            processBuilder.environment().putAll(System.getenv())

            val process = processBuilder.start()

            // Read stdout and stderr in parallel to avoid deadlock
            // when either buffer fills up
            var stderr = ""
            val stderrThread =
                Thread {
                    stderr = process.errorStream.bufferedReader().readText()
                }
            stderrThread.start()
            val stdout = process.inputStream.bufferedReader().readText()
            stderrThread.join()

            val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                CommandResult(
                    success = false,
                    stdout = stdout,
                    stderr = "Process timed out after $timeoutSeconds seconds",
                    exitCode = -1,
                )
            } else {
                CommandResult(
                    success = process.exitValue() == 0,
                    stdout = stdout.trim(),
                    stderr = stderr.trim(),
                    exitCode = process.exitValue(),
                )
            }
        } catch (e: Exception) {
            CommandResult(
                success = false,
                stdout = "",
                stderr = e.message ?: "Unknown error",
                exitCode = -1,
            )
        }
    }

    /**
     * Execute a command and return just the stdout if successful, null otherwise
     */
    fun executeForOutput(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
    ): String? {
        val result = execute(command, workingDir, timeoutSeconds)
        return if (result.success) result.stdout else null
    }

    /**
     * Execute a command and return true if successful
     */
    fun executeSuccessfully(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
        description: String? = null,
    ): Boolean = execute(command, workingDir, timeoutSeconds, description).success
}
