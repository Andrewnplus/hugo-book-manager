package com.nplus.bookmanager.model

data class GoalDefinition(
    val id: String,
    val metric: String,
    val scope: GoalScope,
) {
    companion object {
        const val METRIC_NOTE_STATUS = "note-status-count"
        const val METRIC_LEETCODE = "leetcode-count"
        const val METRIC_REPO_COMPLETION = "repo-completion"
        const val METRIC_ARTICLES = "article-count"
        const val METRIC_PODCAST = "podcast-episodes"
    }
}

data class GoalScope(
    val station: String? = null,
    val statuses: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val repos: List<String> = emptyList(),
)

data class GoalProgress(
    val goalId: String,
    val done: Int,
    val total: Int,
    val unit: String,
    val breakdown: List<BreakdownEntry>,
) {
    data class BreakdownEntry(
        val key: String,
        val done: Int,
        val total: Int,
        val section: String? = null,
    )
}

data class RecentActivity(
    val date: String,
    val goalId: String,
    val item: String,
)
