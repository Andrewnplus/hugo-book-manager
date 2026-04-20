package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.GeneratedMetadata
import java.io.File

/**
 * Service for modifying template files in cloned book repositories
 */
class TemplateService {
    companion object {
        // Hugo Book repo file paths
        const val README_PATH = "README.md"
        const val SETTINGS_GRADLE_PATH = "settings.gradle.kts"
        const val HUGO_TOML_PATH = "site/hugo.toml"
        const val GO_MOD_PATH = "site/go.mod"
        const val INDEX_MD_PATH = "site/content/_index.md"
        const val COVER_IMAGE_PATH = "site/content/cover.png"
    }

    /**
     * Update all template files with full book metadata (for init-books).
     */
    fun updateTemplateFiles(
        repoDir: File,
        metadata: GeneratedMetadata,
        bookInput: BookInput,
    ): Boolean {
        var allSuccess = true

        // Update README.md
        allSuccess = updateFile(
            File(repoDir, README_PATH),
            mapOf(
                AppConfig.TEMPLATE_SLUG to metadata.repoName,
                AppConfig.TEMPLATE_ZH_TITLE to metadata.chineseTitle,
                AppConfig.TEMPLATE_EN_TITLE to metadata.englishTitle,
            ),
        ) &&
            allSuccess

        // Update settings.gradle.kts
        allSuccess = updateFile(
            File(repoDir, SETTINGS_GRADLE_PATH),
            mapOf(AppConfig.TEMPLATE_SLUG to metadata.repoName),
        ) &&
            allSuccess

        // Update site/go.mod
        updateGoMod(repoDir, metadata.repoName)

        // Update site/hugo.toml
        allSuccess = updateFile(
            File(repoDir, HUGO_TOML_PATH),
            mapOf(
                AppConfig.TEMPLATE_ZH_TITLE to metadata.chineseTitle,
                AppConfig.TEMPLATE_SLUG to metadata.repoName,
            ),
        ) &&
            allSuccess

        // Update site/content/_index.md with book info
        updateIndexFile(repoDir, metadata.chineseTitle, bookInput)

        return allSuccess
    }

    private fun updateIndexFile(
        repoDir: File,
        chineseTitle: String,
        bookInput: BookInput,
    ) {
        val indexFile = File(repoDir, INDEX_MD_PATH)
        if (!indexFile.exists()) {
            println("  Warning: _index.md not found")
            return
        }

        var content = indexFile.readText()
        content =
            content
                .replace("title: \"${AppConfig.TEMPLATE_ZH_TITLE}\"", "title: \"$chineseTitle\"")
                .replace("title=\"${AppConfig.TEMPLATE_ZH_TITLE}\"", "title=\"$chineseTitle\"")
                .replace("author=\"${AppConfig.TEMPLATE_AUTHOR_PLACEHOLDER}\"", "author=\"${bookInput.author}\"")
                .replace("date=\"${AppConfig.TEMPLATE_DATE_PLACEHOLDER}\"", "date=\"${bookInput.publicationDate}\"")
                .replace("link=\"${AppConfig.TEMPLATE_PURCHASE_URL_PLACEHOLDER}\"", "link=\"${bookInput.purchaseUrl}\"")

        indexFile.writeText(content)
        println("  Updated: _index.md")
    }

    private fun updateGoMod(
        repoDir: File,
        repoName: String,
    ) {
        val goModFile = File(repoDir, GO_MOD_PATH)
        if (!goModFile.exists()) {
            println("  Warning: go.mod not found")
            return
        }

        var content = goModFile.readText()
        content =
            content.replace(
                Regex("module github\\.com/[^/]+/[^\\s]+"),
                "module github.com/${AppConfig.githubUsername}/$repoName",
            )
        goModFile.writeText(content)
        println("  Updated: go.mod")
    }

    /**
     * Update a single file with the given replacements
     */
    private fun updateFile(
        file: File,
        replacements: Map<String, String>,
    ): Boolean {
        if (!file.exists()) {
            println("  Warning: File not found: ${file.path}")
            return true // Not a failure, just skip
        }

        return try {
            var content = file.readText()

            for ((old, new) in replacements) {
                content = content.replace(old, new)
            }

            file.writeText(content)
            println("  Updated: ${file.name}")
            true
        } catch (e: Exception) {
            println("  Error updating ${file.name}: ${e.message}")
            false
        }
    }
}
