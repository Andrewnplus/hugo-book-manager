package com.nplus.bookmanager.util

import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object ProcessRunner {
    private const val DRAIN_GRACE_MS = 2_000L

    data class CommandResult(
        val success: Boolean,
        val stdout: String,
        val stderr: String,
        val exitCode: Int,
    )

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

            processBuilder.environment().putAll(System.getenv())

            val process = processBuilder.start()

            val stdout = AtomicReference("")
            val stderr = AtomicReference("")
            val stdoutThread = drain(process.inputStream, stdout)
            val stderrThread = drain(process.errorStream, stderr)

            val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                CommandResult(
                    success = false,
                    stdout = "",
                    stderr = "Process timed out after $timeoutSeconds seconds",
                    exitCode = -1,
                )
            } else {
                stdoutThread.join(DRAIN_GRACE_MS)
                stderrThread.join(DRAIN_GRACE_MS)
                CommandResult(
                    success = process.exitValue() == 0,
                    stdout = stdout.get().trim(),
                    stderr = stderr.get().trim(),
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

    private fun drain(
        stream: InputStream,
        sink: AtomicReference<String>,
    ): Thread =
        Thread {
            runCatching { sink.set(stream.bufferedReader().readText()) }
        }.apply {
            isDaemon = true
            start()
        }

    fun executeForOutput(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
        description: String? = null,
    ): String? {
        val result = execute(command, workingDir, timeoutSeconds, description)
        return if (result.success) result.stdout else null
    }

    fun executeSuccessfully(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
        description: String? = null,
    ): Boolean = execute(command, workingDir, timeoutSeconds, description).success
}
