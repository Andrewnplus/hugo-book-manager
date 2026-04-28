package com.nplus.bookmanager.model

/**
 * Cached snapshot of book repos on the configured GitHub owner.
 * Used by init-books to detect duplicates and to "claim" already-existing
 * repos to a local folder without going through AI generation.
 */
data class RepoIndex(
    val lastUpdated: String?,
    val repos: List<RepoEntry>,
) {
    data class RepoEntry(
        val name: String,
        val description: String,
        val url: String,
        val topics: List<String>,
    ) {
        /**
         * Category extracted from topics — the topic ending in `-book-summary`
         * other than the generic `book-summary` itself. Returns null if absent.
         */
        val category: String?
            get() =
                topics.firstOrNull {
                    it.endsWith("-book-summary") && it != "book-summary"
                }
    }
}
