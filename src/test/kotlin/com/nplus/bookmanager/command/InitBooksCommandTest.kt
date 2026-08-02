package com.nplus.bookmanager.command

import com.github.ajalt.clikt.testing.test
import com.nplus.bookmanager.service.AiTaskService
import com.nplus.bookmanager.service.BookInputService
import com.nplus.bookmanager.service.ConfigureResult
import com.nplus.bookmanager.service.GitHubClient
import com.nplus.bookmanager.service.RepoIndexService
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers which book `init-books` picks and what phase 1 does to the queue.
 *
 * Everything the command touches here is either a temp directory or a fake
 * GitHub client, so no test creates a repo, runs `gh`, or writes into the
 * project's own `ai-tasks/`. Phase 2 is out of scope — it goes through
 * BookRepoService, which is covered by its own test.
 */
class InitBooksCommandTest {
    @TempDir
    lateinit var root: File

    private val queueFile get() = File(root, "books-queue.yaml")
    private val aiTasksDir get() = File(root, "ai-tasks")

    /** `gh` never runs; the gate is the only call phase 1 needs from it. */
    private class FakeGitHub(
        private val ready: Boolean = true,
    ) : GitHubClient {
        override fun checkPrerequisites() = ready

        override fun getUsername() = "tester"

        override fun repoExists(
            username: String,
            repoName: String,
        ) = error("must not reach GitHub")

        override fun createRepo(
            owner: String,
            repoName: String,
            description: String,
            templateRepo: String,
            isPrivate: Boolean,
        ) = error("must not reach GitHub")

        override fun cloneRepo(
            username: String,
            repoName: String,
            targetDir: String,
        ) = error("must not reach GitHub")

        override fun configureRepository(
            username: String,
            repoName: String,
            homepageUrl: String,
            topics: List<String>,
        ): ConfigureResult = error("must not reach GitHub")
    }

    private fun book(
        id: String,
        status: String,
    ) = """
        |  - id: $id
        |    status: $status
        |    chinese_title: 書名-$id
        |    english_title: Title $id
        |    author: Author
        |    publication_date: "2020"
        |    cover_url: https://example.invalid/$id.png
        |    purchase_url: https://example.invalid/buy/$id
        |    table_of_contents: "1. One"
        """.trimMargin()

    private fun writeQueue(vararg entries: String) {
        queueFile.writeText("books:\n" + entries.joinToString("\n") + "\n")
    }

    private fun command(ready: Boolean = true) =
        InitBooksCommand(
            bookInputService = BookInputService(),
            aiTaskService = AiTaskService(aiTasksDir.path),
            ghService = FakeGitHub(ready),
            repoIndexService = RepoIndexService(File(root, "existing-repos.yaml")),
        )

    /** The command reports through println, which Clikt's harness does not capture. */
    private fun run(
        vararg args: String,
        ready: Boolean = true,
    ): String {
        val buffer = ByteArrayOutputStream()
        val original = System.out
        System.setOut(PrintStream(buffer))
        try {
            command(ready).test(arrayOf("--queue", queueFile.path) + args)
        } finally {
            System.setOut(original)
        }
        return buffer.toString()
    }

    private fun queueText() = queueFile.readText()

    private fun requestFile() = File(aiTasksDir, "input/batch-metadata-request.json")

    // ==================== queue loading ====================

    @Test
    fun `reports a missing queue file instead of throwing`() {
        val output = run("--status")

        assertContains(output, "Failed to load queue file")
    }

    // ==================== --status ====================

    @Test
    fun `status counts each state and never touches GitHub`() {
        writeQueue(book("a", "pending"), book("b", "completed"), book("c", "error"))

        val output = run("--status")

        assertContains(output, "Total:      3 book(s)")
        assertContains(output, "Pending:    1")
        assertContains(output, "Completed:  1")
        assertContains(output, "Error:      1")
        // --status returns before the prerequisite gate, so it works unauthenticated.
        assertFalse(output.contains("Checking prerequisites"))
    }

    // ==================== --reset ====================

    @Test
    fun `reset with an id puts the book back to pending and persists it`() {
        writeQueue(book("a", "completed"))

        val output = run("--reset", "--id", "a")

        assertContains(output, "Reset book 'a' to pending")
        assertContains(queueText(), "status: pending")
    }

    @Test
    fun `reset without an id is ignored and the run proceeds to the next pending book`() {
        // Documents a trap: --reset alone looks like it did something.
        writeQueue(book("a", "pending"))

        val output = run("--reset")

        assertContains(output, "[Phase 1]")
        assertTrue(requestFile().exists())
    }

    @Test
    fun `reset reports an unknown id rather than failing silently`() {
        writeQueue(book("a", "pending"))

        val output = run("--reset", "--id", "nope")

        assertContains(output, "not found in queue")
        assertContains(queueText(), "status: pending")
    }

    // ==================== prerequisites ====================

    @Test
    fun `stops before selecting a book when gh is unavailable`() {
        writeQueue(book("a", "pending"))

        val output = run(ready = false)

        assertFalse(output.contains("[Phase 1]"))
        assertFalse(requestFile().exists())
        assertContains(queueText(), "status: pending")
    }

    // ==================== book selection ====================

    @Test
    fun `reports an unknown --id instead of picking some other book`() {
        writeQueue(book("a", "pending"))

        val output = run("--id", "nope")

        assertContains(output, "Book with ID 'nope' not found")
        assertFalse(requestFile().exists())
    }

    @Test
    fun `a processing book wins over a pending one`() {
        writeQueue(book("a", "pending"), book("b", "processing"))

        val output = run()

        // b is mid-flight; picking a would strand it.
        assertContains(output, "書名-b")
        assertContains(output, "[Phase 2]")
    }

    @Test
    fun `an all-completed queue stops without writing an AI task`() {
        writeQueue(book("a", "completed"), book("b", "completed"))

        val output = run()

        assertContains(output, "All books in queue have been processed")
        assertFalse(requestFile().exists())
    }

    @Test
    fun `a completed book named by --id does not roll on to the next one`() {
        writeQueue(book("a", "completed"), book("b", "pending"))

        val output = run("--id", "a")

        assertContains(output, "already completed")
        assertFalse(output.contains("Moving to next pending"))
        assertFalse(requestFile().exists())
    }

    @Test
    fun `a duplicate book is reported only when asked for by id`() {
        writeQueue(book("a", "duplicate"), book("b", "pending"))

        val output = run("--id", "a")

        assertContains(output, "marked as duplicate")
        assertFalse(requestFile().exists())
    }

    /**
     * Selection only ever yields a processing or a pending book, so the
     * "moving to next pending book" arms inside the completed and duplicate
     * branches cannot run without --id — and with --id they are skipped. They
     * are unreachable; this pins the behaviour that makes them so.
     */
    @Test
    fun `without an id a duplicate is passed over rather than reported`() {
        writeQueue(book("a", "duplicate"), book("b", "pending"))

        val output = run()

        assertContains(output, "書名-b")
        assertFalse(output.contains("marked as duplicate"))
        assertContains(output, "[Phase 1]")
    }

    @Test
    fun `an errored book asks for a reset instead of retrying on its own`() {
        writeQueue(book("a", "error"), book("b", "pending"))

        val output = run("--id", "a")

        assertContains(output, "--reset --id=a")
        assertFalse(requestFile().exists())
    }

    // ==================== phase 1 ====================

    @Test
    fun `phase 1 writes the metadata request and flips the book to processing`() {
        writeQueue(book("a", "pending"))

        val output = run()

        assertContains(output, "[Phase 1]")
        assertTrue(requestFile().exists())
        assertContains(requestFile().readText(), "Title a")
        assertContains(output, "a → processing")
        assertContains(queueText(), "status: processing")
    }

    @Test
    fun `dry run writes the request but leaves the queue untouched`() {
        writeQueue(book("a", "pending"))

        run("--dry-run")

        assertTrue(requestFile().exists())
        assertContains(queueText(), "status: pending")
    }

    @Test
    fun `phase 1 refuses to queue a second request while one is pending`() {
        writeQueue(book("a", "pending"), book("b", "pending"))
        run() // leaves a request for a, and a in processing

        // Reset a so the command reaches phase 1 again with the request still there.
        queueFile.writeText(queueText().replace("status: processing", "status: pending"))
        val output = run()

        assertContains(output, "already pending")
    }

    @Test
    fun `phase 2 waits instead of guessing when the AI response is missing`() {
        writeQueue(book("a", "processing"))

        val output = run()

        assertContains(output, "[Phase 2]")
        assertContains(output, "not yet completed")
        assertContains(queueText(), "status: processing")
    }

    @Test
    fun `an invalid queue entry is rejected before any work happens`() {
        queueFile.writeText("books:\n  - id: a\n    status: pending\n    chinese_title: 只有標題\n")

        val output = run()

        assertContains(output, "Invalid book entry")
        assertFalse(requestFile().exists())
        assertEquals(false, requestFile().exists())
    }
}
