package com.nplus.bookmanager.config

import java.io.File
import java.util.Properties

/**
 * Application configuration loaded from local.properties
 */
object AppConfig {
    // Rate limit delays (milliseconds)
    const val POST_CREATION_DELAY_MS = 2000L
    const val TOPIC_API_DELAY_MS = 500L

    // Paths
    const val PROMPTS_DIR = "templates/prompts"

    // Template placeholders — values to be replaced when creating a new book repo
    const val TEMPLATE_SLUG = "hugo-book-template"
    const val TEMPLATE_EN_TITLE = "Hugo Book Template"
    const val TEMPLATE_ZH_TITLE = "讀書筆記模版"
    const val TEMPLATE_AUTHOR_PLACEHOLDER = "待填寫作者"
    const val TEMPLATE_DATE_PLACEHOLDER = "待填寫日期"
    const val TEMPLATE_PURCHASE_URL_PLACEHOLDER = "https://www.amazon.com/"

    private val properties by lazy {
        Properties().also { props ->
            val localPropertiesFile = File("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { props.load(it) }
            }

            // Also check environment variables as fallback
            System.getenv().forEach { (key, value) ->
                if (!props.containsKey(key)) {
                    props.setProperty(key, value)
                }
            }
        }
    }

    val githubUsername: String
        get() = getProperty("GITHUB_USERNAME") ?: ""

    val defaultWorkDir: String
        get() = getProperty("DEFAULT_WORK_DIR") ?: System.getProperty("user.home")

    val templateRepo: String
        get() = getProperty("TEMPLATE_REPO") ?: "Andrewnplus/hugo-book-template"

    val homepageBaseUrl: String
        get() = getProperty("HOMEPAGE_BASE_URL") ?: "https://nplus.wiki"

    /** Local clone of the nplus.wiki portal repo (goals.yaml + progress.json live there). */
    val portalDir: String
        get() = getProperty("PORTAL_DIR") ?: ""

    /** Workspace directory holding the Astro note stations (incl. leetcode-note). */
    val notesDir: String
        get() = getProperty("NOTES_DIR") ?: ""

    /** Root of the filed book repos (books-done, top/sub/leaf tree). */
    val booksDir: String
        get() = getProperty("BOOKS_DIR") ?: ""

    private fun getProperty(key: String): String? = properties.getProperty(key)

    override fun toString(): String =
        """
        AppConfig:
          GITHUB_USERNAME: ${githubUsername.ifBlank { "(not set)" }}
          DEFAULT_WORK_DIR: $defaultWorkDir
          TEMPLATE_REPO: $templateRepo
          HOMEPAGE_BASE_URL: $homepageBaseUrl
        """.trimIndent()
}
