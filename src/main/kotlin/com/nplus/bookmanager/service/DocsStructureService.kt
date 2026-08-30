package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.DocsStructure
import com.nplus.bookmanager.util.escapeQuoted
import java.io.File

class DocsStructureService : DocsStructureWriter {
    override fun createDocsStructure(
        repoDir: File,
        structure: DocsStructure,
        clearExisting: Boolean,
    ): Int {
        val docsDir = File(repoDir, "site/content/docs")

        if (!docsDir.exists()) {
            docsDir.mkdirs()
        }

        if (clearExisting) {
            docsDir.listFiles()?.forEach { it.deleteRecursively() }
        }

        var createdCount = 0

        for (section in structure.sections) {
            if (section.chapters.isEmpty()) {
                createChapterFolder(docsDir, section.folderName, section.title, section.weight)
                createdCount++
            } else {
                val sectionDir = File(docsDir, section.folderName)
                sectionDir.mkdirs()

                createSectionIndex(sectionDir, section.title, section.weight)
                createdCount++

                for (chapter in section.chapters) {
                    createChapterFolder(sectionDir, chapter.folderName, chapter.title, chapter.weight)
                    createdCount++
                }
            }
        }

        return createdCount
    }

    private fun createSectionIndex(
        sectionDir: File,
        title: String,
        weight: Int,
    ) {
        val indexFile = File(sectionDir, "_index.md")
        indexFile.writeText(
            """
            |---
            |title: "${escapeQuoted(title)}"
            |weight: $weight
            |bookCollapseSection: true
            |---
            |
            """.trimMargin(),
        )
    }

    private fun createChapterFolder(
        parentDir: File,
        folderName: String,
        title: String,
        weight: Int,
    ) {
        val chapterDir = File(parentDir, folderName)
        chapterDir.mkdirs()

        val indexFile = File(chapterDir, "_index.md")
        indexFile.writeText(
            """
            |---
            |title: "${escapeQuoted(title)}"
            |weight: $weight
            |---
            |
            """.trimMargin(),
        )
    }

    override fun printDocsStructure(structure: DocsStructure) {
        println("  Docs structure:")
        structure.sections.forEach { section ->
            if (section.chapters.isEmpty()) {
                println("    ${section.folderName}/ - ${section.title}")
            } else {
                println("    ${section.folderName}/ - ${section.title}")
                section.chapters.forEach { chapter ->
                    println("      ${chapter.folderName}/ - ${chapter.title}")
                }
            }
        }
    }
}
