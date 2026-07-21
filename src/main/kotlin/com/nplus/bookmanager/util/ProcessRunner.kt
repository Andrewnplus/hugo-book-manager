package com.nplus.bookmanager.util

import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Utility for executing external shell commands
 */
object ProcessRunner {
    /**
     * How long to wait for the stream readers to drain after the process has
     * ended. A grandchild that inherited the pipe can hold it open past the
     * child's exit, so the readers are never joined without a bound.
     */
    private const val DRAIN_GRACE_MS = 2_000L

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

            // Drain both streams on their own threads. Neither may be read on
            // the calling thread: readText() blocks until EOF, so reading here
            // before waitFor() would put the timeout out of reach for exactly
            // the processes it exists to kill (a `gh` call hung on the network
            // or waiting for input). Separate threads also avoid the deadlock
            // where one pipe buffer fills while we drain the other.
            val stdout = AtomicReference("")
            val stderr = AtomicReference("")
            val stdoutThread = drain(process.inputStream, stdout)
            val stderrThread = drain(process.errorStream, stderr)

            val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)

            if (!completed) {
                // The readers only publish at EOF, so there is no partial
                // output to salvage and nothing to wait for: kill the process
                // and leave the daemon readers to be reaped. Joining here
                // would stall for the full grace on exactly the case that
                // causes it — a grandchild still holding the pipe open.
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

    /**
     * Consume a process stream to EOF on a daemon thread, so a reader left
     * blocked on a pipe held open by a grandchild can never keep the JVM alive.
     */
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

    /**
     * Execute a command and return just the stdout if successful, null otherwise
     */
    fun executeForOutput(
        command: String,
        workingDir: File? = null,
        timeoutSeconds: Long = 60,
        description: String? = null,
    ): String? {
        val result = execute(command, workingDir, timeoutSeconds, description)
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
