package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.model.GeneratedMetadata
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookRepoServiceTest {
    @TempDir
    lateinit var workDir: File

    private val calls = mutableListOf<String>()

    private fun capturingStdout(block: () -> Unit): String {
        val buffer = ByteArrayOutputStream()
        val original = System.out
        System.setOut(PrintStream(buffer))
        try {
            block()
        } finally {
            System.setOut(original)
        }
        return buffer.toString()
    }

    private inner class FakeGitHub(
        private val username: String? = "tester",
        private val exists: Boolean = false,
        private val cloneSucceeds: Boolean = true,
        private val topicsApplied: Int? = null,
    ) : GitHubClient {
        var homepageUrl: String? = null
        var topics: List<String> = emptyList()

        override fun checkPrerequisites(): Boolean {
            calls += "checkPrerequisites"
            return true
        }

        override fun getUsername(): String? {
            calls += "getUsername"
            return username
        }

        override fun repoExists(
            username: String,
            repoName: String,
        ): Boolean {
            calls += "repoExists"
            return exists
        }

        override fun createRepo(
            owner: String,
            repoName: String,
            description: String,
            templateRepo: String,
            isPrivate: Boolean,
        ): Boolean {
            calls += "createRepo($owner/$repoName)"
            return true
        }

        override fun cloneRepo(
            username: String,
            repoName: String,
            targetDir: String,
        ): Boolean {
            calls += "cloneRepo"
            if (cloneSucceeds) File(targetDir).mkdirs()
            return cloneSucceeds
        }

        override fun configureRepository(
            username: String,
            repoName: String,
            homepageUrl: String,
            topics: List<String>,
        ): ConfigureResult {
            calls += "configureRepository"
            this.homepageUrl = homepageUrl
            this.topics = topics
            return ConfigureResult(
                homepageSet = true,
                topicsSet = topicsApplied ?: topics.size,
                topicsRequested = topics.size,
                pagesEnabled = true,
            )
        }
    }

    private inner class FakeTemplate : TemplateWriter {
        override fun updateTemplateFiles(
            repoDir: File,
            metadata: GeneratedMetadata,
            bookInput: BookInput,
        ): Boolean {
            calls += "updateTemplateFiles"
            return true
        }
    }

    private inner class FakeCover : CoverImageFetcher {
        override fun downloadAndResize(
            imageUrl: String,
            outputFile: File,
        ): Boolean {
            calls += "downloadAndResize"
            return true
        }
    }

    private inner class FakeDocs : DocsStructureWriter {
        override fun createDocsStructure(
            repoDir: File,
            structure: DocsStructure,
            clearExisting: Boolean,
        ): Int {
            calls += "createDocsStructure"
            return structure.sections.size
        }

        override fun printDocsStructure(structure: DocsStructure) {
            calls += "printDocsStructure"
        }
    }

    private inner class FakeGit : GitOperations {
        override fun setHooksPath(
            repoDir: File,
            path: String,
        ): Boolean {
            calls += "setHooksPath"
            return true
        }

        override fun commitAndPush(
            repoDir: File,
            message: String,
            vararg files: String,
        ): Boolean {
            calls += "commitAndPush"
            return true
        }
    }

    private val metadata =
        GeneratedMetadata(
            repoName = "clean-code",
            englishTitle = "Clean Code",
            chineseTitle = "無瑕的程式碼",
            description = "Clean Code reading notes",
            topics = listOf("hugobook", "nplus-portal", "leaf-coding-practice"),
            topCategory = "craft",
            subCategory = "engineering",
            leafCategory = "coding-practice",
        )

    private fun bookInput(coverUrl: String = "https://example.invalid/cover.png") =
        BookInput(
            chineseTitle = "無瑕的程式碼",
            englishTitle = "Clean Code",
            author = "Robert C. Martin",
            publicationDate = "2008",
            coverUrl = coverUrl,
            purchaseUrl = "https://example.invalid/buy",
            tableOfContents = "1. Clean Code",
        )

    private val structure = DocsStructure(sections = listOf(DocsStructure.Section("01-intro", "Intro", 1)))

    private fun service(
        gh: GitHubClient,
        username: String = "",
        confirm: (String) -> Boolean = { true },
    ) = BookRepoService(
        ghService = gh,
        templateService = FakeTemplate(),
        imageService = FakeCover(),
        docsStructureService = FakeDocs(),
        gitService = FakeGit(),
        config =
            BookRepoConfig(
                githubUsername = username,
                homepageBaseUrl = "https://nplus.wiki",
                workDir = workDir,
            ),
        confirm = confirm,
    )

    @Test
    fun `aborts before creating anything when no username can be resolved`() {
        val gh = FakeGitHub(username = null)

        val result = service(gh).createBookRepository(metadata, bookInput(), structure)

        assertFalse(result.success)
        assertEquals(null, result.repoDir)
        assertEquals(listOf("getUsername"), calls)
    }

    @Test
    fun `creates, clones and pushes in order for a brand new book`() {
        val gh = FakeGitHub()

        val result = service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(), structure)

        assertTrue(result.success)
        assertEquals(File(workDir, "clean-code"), result.repoDir)
        assertEquals(
            listOf(
                "repoExists",
                "createRepo(nplus-father/clean-code)",
                "configureRepository",
                "cloneRepo",
                "setHooksPath",
                "updateTemplateFiles",
                "downloadAndResize",
                "createDocsStructure",
                "commitAndPush",
            ),
            calls,
        )
        assertFalse(calls.contains("getUsername"))
    }

    @Test
    fun `builds the homepage URL with a trailing slash from the configured base`() {
        val gh = FakeGitHub()

        service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(), structure)

        assertEquals("https://nplus.wiki/clean-code/", gh.homepageUrl)
        assertContains(gh.topics, "leaf-coding-practice")
    }

    @Test
    fun `skips the cover download when the book has no cover URL`() {
        val gh = FakeGitHub()

        service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(coverUrl = ""), structure)

        assertFalse(calls.contains("downloadAndResize"))
        assertTrue(calls.contains("createDocsStructure"))
    }

    @Test
    fun `stops without cloning when the repo exists and the user declines`() {
        val gh = FakeGitHub(exists = true)

        val result =
            service(gh, username = "nplus-father", confirm = { false })
                .createBookRepository(metadata, bookInput(), structure)

        assertFalse(result.success)
        assertEquals(listOf("repoExists"), calls)
    }

    @Test
    fun `reports failure when the clone fails`() {
        val gh = FakeGitHub(cloneSucceeds = false)

        val result = service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(), structure)

        assertFalse(result.success)
        assertEquals(null, result.repoDir)
        assertFalse(calls.contains("updateTemplateFiles"))
    }

    @Test
    fun `says so when only some topics were applied instead of reporting success`() {
        val gh = FakeGitHub(topicsApplied = 1)

        val output =
            capturingStdout {
                service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(), structure)
            }

        assertContains(output, "only partly configured")
        assertContains(output, "only 1 of 3 applied")
        assertFalse(output.contains("Repository configured"))
    }

    @Test
    fun `reuses an existing local directory instead of cloning over it`() {
        File(workDir, "clean-code").mkdirs()
        val gh = FakeGitHub()

        val result = service(gh, username = "nplus-father").createBookRepository(metadata, bookInput(), structure)

        assertTrue(result.success)
        assertFalse(calls.contains("cloneRepo"))
    }
}
