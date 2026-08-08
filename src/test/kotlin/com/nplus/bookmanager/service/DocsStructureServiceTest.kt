package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.DocsStructure
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DocsStructureServiceTest {
    @TempDir
    lateinit var tempDir: File

    private val service = DocsStructureService()

    private fun docsDir() = File(tempDir, "site/content/docs")

    @Test
    fun `flat sections create one chapter folder each`() {
        val structure =
            DocsStructure(
                sections =
                    listOf(
                        DocsStructure.Section("01-intro", "導論", 1),
                        DocsStructure.Section("02-habit-loop", "習慣迴路", 2),
                    ),
            )

        val created = service.createDocsStructure(tempDir, structure)

        assertEquals(2, created)
        val index = File(docsDir(), "01-intro/_index.md")
        assertTrue(index.exists())
        assertContains(index.readText(), "title: \"導論\"")
        assertContains(index.readText(), "weight: 1")
        assertFalse(index.readText().contains("bookCollapseSection"), "a leaf chapter must not collapse")
    }

    @Test
    fun `sections with chapters nest and get a collapsible section index`() {
        val structure =
            DocsStructure(
                sections =
                    listOf(
                        DocsStructure.Section(
                            folderName = "01-part-one",
                            title = "第一部",
                            weight = 1,
                            chapters =
                                listOf(
                                    DocsStructure.Chapter("01-first", "第一章", 1),
                                    DocsStructure.Chapter("02-second", "第二章", 2),
                                ),
                        ),
                    ),
            )

        // 1 section index + 2 chapters
        assertEquals(3, service.createDocsStructure(tempDir, structure))

        val sectionIndex = File(docsDir(), "01-part-one/_index.md")
        assertContains(sectionIndex.readText(), "bookCollapseSection: true")
        assertTrue(File(docsDir(), "01-part-one/01-first/_index.md").exists())
        assertTrue(File(docsDir(), "01-part-one/02-second/_index.md").exists())
    }

    @Test
    fun `clearExisting wipes the docs root, stray index file included`() {
        val docs = docsDir()
        File(docs, "99-placeholder").mkdirs()
        File(docs, "_index.md").writeText("root")

        service.createDocsStructure(tempDir, DocsStructure(listOf(DocsStructure.Section("01-intro", "導論", 1))))

        assertFalse(File(docs, "99-placeholder").exists())
        assertFalse(File(docs, "_index.md").exists(), "the docs root must not keep a section index")
        assertTrue(File(docs, "01-intro").exists())
    }

    @Test
    fun `clearExisting false keeps existing folders`() {
        File(docsDir(), "99-keep-me").mkdirs()

        service.createDocsStructure(
            tempDir,
            DocsStructure(listOf(DocsStructure.Section("01-intro", "導論", 1))),
            clearExisting = false,
        )

        assertTrue(File(docsDir(), "99-keep-me").exists())
    }

    @Test
    fun `empty structure creates the docs dir and nothing else`() {
        assertEquals(0, service.createDocsStructure(tempDir, DocsStructure(emptyList())))
        assertTrue(docsDir().isDirectory)
        assertEquals(0, docsDir().listFiles()?.size)
    }
}
