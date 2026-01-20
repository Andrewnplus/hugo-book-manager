package com.nplus.bookmanager.service

import java.io.File

/**
 * Service for modifying template files in cloned book repositories
 */
class TemplateService {
    private val templateRepoName = "hugo-book-template"
    private val templateBookTitle = "讀書筆記模版"

    /**
     * Update all template files in a cloned repository
     *
     * @param repoDir The cloned repository directory
     * @param repoName The new repository name
     * @param chineseTitle The Chinese book title
     */
    fun updateTemplateFiles(
        repoDir: File,
        repoName: String,
        chineseTitle: String,
    ): Boolean {
        var allSuccess = true

        // Update README.md
        allSuccess = updateFile(
            File(repoDir, "README.md"),
            mapOf(
                templateRepoName to repoName,
                templateBookTitle to chineseTitle,
            ),
        ) &&
            allSuccess

        // Update settings.gradle.kts
        allSuccess = updateFile(
            File(repoDir, "settings.gradle.kts"),
            mapOf(templateRepoName to repoName),
        ) &&
            allSuccess

        // Update site/hugo.toml
        val hugoToml = File(repoDir, "site/hugo.toml")
        allSuccess = updateFile(
            hugoToml,
            mapOf(
                templateBookTitle to chineseTitle,
                "baseURL = \"https://nplus.wiki/$templateRepoName/\"" to "baseURL = \"https://nplus.wiki/$repoName/\"",
            ),
        ) &&
            allSuccess

        // Update site/content/_index.md
        allSuccess = updateFile(
            File(repoDir, "site/content/_index.md"),
            mapOf(templateBookTitle to chineseTitle),
        ) &&
            allSuccess

        // Update site/go.mod
        allSuccess = updateFile(
            File(repoDir, "site/go.mod"),
            mapOf(
                "module github.com/Andrewnplus/$templateRepoName" to "module github.com/Andrewnplus/$repoName",
            ),
        ) &&
            allSuccess

        return allSuccess
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

    /**
     * Create or update renovate.json with the standard configuration
     */
    fun updateRenovateJson(repoDir: File): Boolean {
        val githubDir = File(repoDir, ".github")
        if (!githubDir.exists()) {
            githubDir.mkdirs()
        }

        val renovateFile = File(githubDir, "renovate.json")
        return try {
            renovateFile.writeText(STANDARD_RENOVATE_JSON)
            println("  Updated: .github/renovate.json")
            true
        } catch (e: Exception) {
            println("  Error updating renovate.json: ${e.message}")
            false
        }
    }

    companion object {
        val STANDARD_RENOVATE_JSON =
            """
{
  "${'$'}schema": "https://docs.renovatebot.com/renovate-schema.json",
  "extends": ["config:base"],
  "schedule": ["every 3 weeks on saturday"],
  "automerge": true,
  "automergeType": "pr",
  "packageRules": [
    {
      "matchUpdateTypes": ["minor", "patch"],
      "enabled": false
    },
    {
      "matchUpdateTypes": ["major"],
      "groupName": "major updates",
      "automerge": true
    }
  ]
}
            """.trimIndent()
    }
}
