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
        /** Top-tier slug (without `top-` prefix), or null if not tagged. */
        val topCategory: String?
            get() = topics.firstOrNull { it.startsWith("top-") }?.removePrefix("top-")

        /** Sub-tier slug (without `sub-` prefix), or null if not tagged. */
        val subCategory: String?
            get() = topics.firstOrNull { it.startsWith("sub-") }?.removePrefix("sub-")

        /** Leaf-tier slug (without `leaf-` prefix), or null if not tagged. */
        val leafCategory: String?
            get() = topics.firstOrNull { it.startsWith("leaf-") }?.removePrefix("leaf-")
    }
}
