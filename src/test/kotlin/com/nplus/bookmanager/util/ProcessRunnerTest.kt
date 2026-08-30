package com.nplus.bookmanager.util

import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProcessRunnerTest {
    @TempDir
    lateinit var tempDir: File

    @Test
    fun `execute captures stdout and exit code on success`() {
        val result = ProcessRunner.execute("echo hello")

        assertTrue(result.success)
        assertEquals(0, result.exitCode)
        assertEquals("hello", result.stdout)
    }

    @Test
    fun `execute captures stderr and exit code on failure`() {
        val result = ProcessRunner.execute("echo oops >&2; exit 3")

        assertFalse(result.success)
        assertEquals(3, result.exitCode)
        assertEquals("oops", result.stderr)
    }

    @Test
    fun `execute runs in the given working directory`() {
        File(tempDir, "marker.txt").writeText("x")

        val result = ProcessRunner.execute("ls", workingDir = tempDir)

        assertEquals("marker.txt", result.stdout)
    }

    @Test
    fun `execute enforces the timeout instead of waiting for the process`() {
        val startedAt = System.currentTimeMillis()
        val result = ProcessRunner.execute("sleep 10; echo done", timeoutSeconds = 1)
        val elapsedMs = System.currentTimeMillis() - startedAt

        assertFalse(result.success, "a timed-out process must not report success")
        assertEquals(-1, result.exitCode)
        assertContains(result.stderr, "timed out")
        assertTrue(elapsedMs < 4_000, "returned after ${elapsedMs}ms — timeout was not enforced")
    }

    @Test
    fun `execute reports failure instead of throwing when the working dir is missing`() {
        val result = ProcessRunner.execute("echo hi", workingDir = File(tempDir, "does-not-exist"))

        assertFalse(result.success)
        assertEquals(-1, result.exitCode)
    }

    @Test
    fun `executeForOutput returns stdout on success and null on failure`() {
        assertEquals("hi", ProcessRunner.executeForOutput("echo hi"))
        assertNull(ProcessRunner.executeForOutput("exit 1"))
    }

    @Test
    fun `executeSuccessfully reflects the exit status`() {
        assertTrue(ProcessRunner.executeSuccessfully("true"))
        assertFalse(ProcessRunner.executeSuccessfully("false"))
    }
}
