package com.nplus.bookmanager.config

import java.io.File
import java.util.Properties

/**
 * Application configuration loaded from local.properties
 */
object AppConfig {
    private val properties = Properties()
    private var loaded = false

    val githubUsername: String
        get() = getProperty("GITHUB_USERNAME") ?: ""

    val defaultWorkDir: String
        get() = getProperty("DEFAULT_WORK_DIR") ?: System.getProperty("user.home")

    val templateRepo: String
        get() = getProperty("TEMPLATE_REPO") ?: "Andrewnplus/hugo-book-template"

    val homepageBaseUrl: String
        get() = getProperty("HOMEPAGE_BASE_URL") ?: "https://nplus.wiki"

    private fun ensureLoaded() {
        if (loaded) return

        val localPropertiesFile = File("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }

        // Also check environment variables as fallback
        System.getenv().forEach { (key, value) ->
            if (!properties.containsKey(key)) {
                properties.setProperty(key, value)
            }
        }

        loaded = true
    }

    private fun getProperty(key: String): String? {
        ensureLoaded()
        return properties.getProperty(key) ?: System.getenv(key)
    }

    override fun toString(): String =
        """
        AppConfig:
          GITHUB_USERNAME: ${githubUsername.ifBlank { "(not set)" }}
          DEFAULT_WORK_DIR: $defaultWorkDir
          TEMPLATE_REPO: $templateRepo
          HOMEPAGE_BASE_URL: $homepageBaseUrl
        """.trimIndent()
}
