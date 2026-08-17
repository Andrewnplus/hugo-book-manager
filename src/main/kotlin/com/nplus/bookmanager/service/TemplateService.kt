package com.nplus.bookmanager.service

import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.BookInput
import com.nplus.bookmanager.model.GeneratedMetadata
import com.nplus.bookmanager.util.escapeQuoted
import java.io.File

/**
 * Service for modifying template files in cloned book repositories
 */
class TemplateService : TemplateWriter {
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
    override fun updateTemplateFiles(
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

        // Update site/hugo.toml. The title lands inside a TOML basic string, so
        // it needs escaping; the slug only appears in baseURL and is kebab-case.
        allSuccess = updateFile(
            File(repoDir, HUGO_TOML_PATH),
            mapOf(
                AppConfig.TEMPLATE_ZH_TITLE to escapeQuoted(metadata.chineseTitle),
                AppConfig.TEMPLATE_SLUG to metadata.repoName,
            ),
        ) &&
            allSuccess

        // Update site/content/_index.md with book info
        updateIndexFile(repoDir, metadata, bookInput)

        return allSuccess
    }

    private fun updateIndexFile(
        repoDir: File,
        metadata: GeneratedMetadata,
        bookInput: BookInput,
    ) {
        val indexFile = File(repoDir, INDEX_MD_PATH)
        if (!indexFile.exists()) {
            println("  Warning: _index.md not found")
            return
        }

        // The book's own metadata lives in the `book:` frontmatter map rather
        // than in shortcode arguments, so `layouts/index.json` can publish it —
        // shortcode arguments are invisible to other templates. Every target is
        // a double-quoted YAML value, so each replacement is escaped; titles
        // like `Think "Win-Win"` otherwise break the parse.
        var content = indexFile.readText()
        content =
            content
                .replace("title: \"${AppConfig.TEMPLATE_ZH_TITLE}\"", "title: \"${escapeQuoted(metadata.chineseTitle)}\"")
                .replace(
                    "author: \"${AppConfig.TEMPLATE_AUTHOR_PLACEHOLDER}\"",
                    "author: \"${escapeQuoted(bookInput.author)}\"",
                ).replace(
                    "published: \"${AppConfig.TEMPLATE_DATE_PLACEHOLDER}\"",
                    "published: \"${escapeQuoted(bookInput.publicationDate)}\"",
                ).replace(
                    "link: \"${AppConfig.TEMPLATE_PURCHASE_URL_PLACEHOLDER}\"",
                    "link: \"${escapeQuoted(bookInput.purchaseUrl)}\"",
                ).replace(
                    "blurb: \"${AppConfig.TEMPLATE_BLURB_PLACEHOLDER}\"",
                    "blurb: \"${escapeQuoted(blurbOf(metadata.description))}\"",
                )

        indexFile.writeText(content)
        println("  Updated: _index.md")
    }

    /**
     * The generated repo description packs three values as `Title | Author |
     * Blurb`; only the blurb belongs in the book's own frontmatter. Falls back
     * to the whole string when the description is not in the packed form, so a
     * hand-written one still reaches the page instead of being dropped.
     */
    private fun blurbOf(description: String): String {
        val parts = description.split('|', '｜').map { it.trim() }.filter { it.isNotEmpty() }
        return if (parts.size >= 3) parts.drop(2).joinToString(" ") else description.trim()
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
