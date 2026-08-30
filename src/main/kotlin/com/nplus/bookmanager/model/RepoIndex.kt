package com.nplus.bookmanager.model

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
        val topCategory: String?
            get() = topics.firstOrNull { it.startsWith("top-") }?.removePrefix("top-")

        val subCategory: String?
            get() = topics.firstOrNull { it.startsWith("sub-") }?.removePrefix("sub-")

        val leafCategory: String?
            get() = topics.firstOrNull { it.startsWith("leaf-") }?.removePrefix("leaf-")
    }
}
