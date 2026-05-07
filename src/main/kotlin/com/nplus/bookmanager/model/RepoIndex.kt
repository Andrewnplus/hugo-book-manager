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

        /**
         * Three-tier folder path `<top>/<sub>/<leaf>` if all three are tagged,
         * else null. Used by claim flow to place the clone correctly.
         */
        val categoryPath: String?
            get() {
                val t = topCategory ?: return null
                val s = subCategory ?: return null
                val l = leafCategory ?: return null
                return "$t/$s/$l"
            }

        /**
         * Legacy folder name from the old two-tier schema (`growth-book-summary`,
         * `business-book-summary`, etc.). Returned only when the entry is *not*
         * yet migrated to the three-tier schema. Lets the claim flow fall back
         * to the old layout for un-migrated repos.
         */
        val legacyCategory: String?
            get() =
                topics.firstOrNull {
                    it.endsWith("-book-summary") && it != "book-summary"
                }
    }
}
