package com.nplus.bookmanager.model

/**
 * A mid-term goal definition, read from the portal repo's
 * `src/data/goals.yaml` (the single hand-written source of goal
 * definitions). The scanner only needs id/metric/scope — label, target and
 * due date are presentation concerns handled by the portal build.
 */
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
    /** notes/ station name for note-status-count. */
    val station: String? = null,
    /** Frontmatter statuses that count as done. */
    val statuses: List<String> = emptyList(),
    /** Category filter (empty = all) for leetcode-count. */
    val categories: List<String> = emptyList(),
    /** Book repo names for repo-completion (M2+). */
    val repos: List<String> = emptyList(),
)

/** Scan result for one goal: done/total plus per-category breakdown. */
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
        /** Station URL segment the key lives under ("concepts"/"problems") — lets the portal deep-link it. */
        val section: String? = null,
    )
}

/** One recent-activity item (a review/read event within the window). */
data class RecentActivity(
    val date: String,
    val goalId: String,
    val item: String,
)
