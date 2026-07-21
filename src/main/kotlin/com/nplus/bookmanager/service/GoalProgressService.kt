package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.GoalDefinition
import com.nplus.bookmanager.model.GoalProgress
import com.nplus.bookmanager.model.GoalScope
import com.nplus.bookmanager.model.RecentActivity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Computes goal progress by scanning frontmatter across the local clones —
 * the static single source of truth. Output is the derived artifact
 * `src/data/progress.json` in the portal repo; it must never be hand-edited
 * (the portal dashboard and nplus-backend both consume it read-only).
 *
 * M1 scope: `note-status-count` (Astro note stations, notes-core schema) and
 * `leetcode-count` (leetcode-note problems). `repo-completion` lands in M2
 * once book repos carry read/readAt frontmatter; `article-count` and
 * `podcast-episodes` are computed elsewhere (portal build) and skipped here.
 */
class GoalProgressService(
    private val portalDir: File,
    private val notesDir: File,
    private val booksDir: File? = null,
) {
    private val yaml = Yaml()

    /** Reviews/reads within this many days land in the `recent` activity list. */
    private val recentWindowDays = 14L

    val goalsFile: File get() = File(portalDir, "src/data/goals.yaml")
    val progressFile: File get() = File(portalDir, "src/data/progress.json")

    fun loadGoals(): List<GoalDefinition> {
        require(goalsFile.exists()) { "goals.yaml not found at ${goalsFile.path}" }
        val raw: List<Map<String, Any>> = goalsFile.inputStream().use { yaml.load(it) } ?: emptyList()
        return raw.map { entry ->
            @Suppress("UNCHECKED_CAST")
            val scope = entry["scope"] as? Map<String, Any> ?: emptyMap()
            GoalDefinition(
                id = entry["id"]?.toString() ?: error("goal without id in ${goalsFile.path}"),
                metric = entry["metric"]?.toString() ?: "",
                scope =
                    GoalScope(
                        station = scope["station"]?.toString(),
                        statuses = stringList(scope["statuses"]),
                        categories = stringList(scope["categories"]),
                        repos = stringList(scope["repos"]),
                    ),
            )
        }
    }

    /** Scan all scanner-supported goals; returns progress + recent activity. */
    fun scan(goals: List<GoalDefinition>): Pair<List<GoalProgress>, List<RecentActivity>> {
        val progress = mutableListOf<GoalProgress>()
        val recent = mutableListOf<RecentActivity>()
        val since = LocalDate.now().minusDays(recentWindowDays)

        for (goal in goals) {
            when (goal.metric) {
                GoalDefinition.METRIC_NOTE_STATUS -> {
                    val station = goal.scope.station
                    if (station == null) {
                        println("⚠ ${goal.id}: note-status-count without scope.station — skipped")
                    } else {
                        progress += scanNoteStation(goal, station, recent, since)
                    }
                }
                GoalDefinition.METRIC_LEETCODE -> progress += scanLeetcode(goal, recent, since)
                GoalDefinition.METRIC_REPO_COMPLETION -> {
                    if (booksDir == null || !booksDir.isDirectory) {
                        println("⚠ ${goal.id}: BOOKS_DIR not configured — skipped")
                    } else {
                        progress += scanRepoCompletion(goal, booksDir, recent, since)
                    }
                }
                GoalDefinition.METRIC_ARTICLES, GoalDefinition.METRIC_PODCAST ->
                    Unit // computed at portal build time, not by this scanner
                else -> println("⚠ ${goal.id}: unknown metric '${goal.metric}' — skipped")
            }
        }
        return progress to recent.sortedByDescending { it.date }
    }

    /**
     * A note station (notes-core schema): concepts/ and problems/ markdown
     * with `status: draft|studied|reviewed` and optional `lastReviewed`.
     */
    private fun scanNoteStation(
        goal: GoalDefinition,
        station: String,
        recent: MutableList<RecentActivity>,
        since: LocalDate,
    ): GoalProgress {
        val contentDir = File(notesDir, "$station/src/content")
        require(contentDir.isDirectory) { "${goal.id}: station content dir not found: ${contentDir.path}" }

        val byCategory = linkedMapOf<CatKey, Pair<Int, Int>>()
        for (sub in listOf("concepts", "problems")) {
            val base = File(contentDir, sub)
            if (!base.isDirectory) continue
            for (file in contentFiles(base)) {
                val fm = frontmatter(file, goal.id)
                val category =
                    file.parentFile
                        .relativeTo(base)
                        .path
                        .ifEmpty { sub }
                val done = fm["status"]?.toString() in goal.scope.statuses
                val key = CatKey(category, section = sub)
                val (d, t) = byCategory.getOrDefault(key, 0 to 0)
                byCategory[key] = (d + if (done) 1 else 0) to (t + 1)

                val lastReviewed = parseDate(fm["lastReviewed"], file, goal.id)
                if (lastReviewed != null && !lastReviewed.isBefore(since)) {
                    recent +=
                        RecentActivity(
                            date = lastReviewed.toString(),
                            goalId = goal.id,
                            item = "$station/${file.nameWithoutExtension}",
                        )
                }
            }
        }
        return toProgress(goal.id, byCategory, unit = "notes")
    }

    /**
     * leetcode-note problems: `status: draft|written|reviewed`, category
     * filter via frontmatter `category`, review events via `reviewedDates`.
     */
    private fun scanLeetcode(
        goal: GoalDefinition,
        recent: MutableList<RecentActivity>,
        since: LocalDate,
    ): GoalProgress {
        val base = File(notesDir, "leetcode-note/src/content/problems")
        require(base.isDirectory) { "${goal.id}: leetcode problems dir not found: ${base.path}" }

        val byCategory = linkedMapOf<CatKey, Pair<Int, Int>>()
        for (file in contentFiles(base)) {
            val fm = frontmatter(file, goal.id)
            val category = fm["category"]?.toString() ?: file.parentFile.name
            if (goal.scope.categories.isNotEmpty() && category !in goal.scope.categories) continue

            val done = fm["status"]?.toString() in goal.scope.statuses
            val key = CatKey(category)
            val (d, t) = byCategory.getOrDefault(key, 0 to 0)
            byCategory[key] = (d + if (done) 1 else 0) to (t + 1)

            for (raw in stringList(fm["reviewedDates"])) {
                val date = parseDate(raw, file, goal.id) ?: continue
                if (!date.isBefore(since)) {
                    recent +=
                        RecentActivity(
                            date = date.toString(),
                            goalId = goal.id,
                            item = "leetcode/${file.nameWithoutExtension}",
                        )
                }
            }
        }
        return toProgress(goal.id, byCategory, unit = "problems")
    }

    /**
     * Book repos in books-done: chapter `_index.md` frontmatter carries
     * `read: true` + `readAt: YYYY-MM-DD` (see BookChapterService / markRead).
     */
    private fun scanRepoCompletion(
        goal: GoalDefinition,
        booksDir: File,
        recent: MutableList<RecentActivity>,
        since: LocalDate,
    ): GoalProgress {
        val chapters = BookChapterService(booksDir)
        val byChapter = linkedMapOf<CatKey, Pair<Int, Int>>()
        for (repoName in goal.scope.repos) {
            val repoDir = chapters.findRepoDir(repoName)
            if (repoDir == null) {
                println("⚠ ${goal.id}: repo '$repoName' not found under ${booksDir.path} — skipped")
                continue
            }
            val files = chapters.chapterFiles(repoDir)
            if (files.isEmpty()) println("⚠ ${goal.id}: repo '$repoName' has no chapters")
            val prefix = if (goal.scope.repos.size > 1) "$repoName/" else ""
            for (file in files) {
                val fm = frontmatter(file, goal.id)
                val key = CatKey(prefix + chapters.chapterKey(repoDir, file))
                val read = fm["read"] == true || fm["read"]?.toString() == "true"
                byChapter[key] = (if (read) 1 else 0) to 1

                val readAt = parseDate(fm["readAt"], file, goal.id)
                if (read && readAt != null && !readAt.isBefore(since)) {
                    recent += RecentActivity(date = readAt.toString(), goalId = goal.id, item = "$repoName/${file.parentFile.name}")
                }
            }
        }
        return toProgress(goal.id, byChapter, unit = "chapters")
    }

    /** Write the derived artifact with stable ordering for clean git diffs. */
    fun save(
        progress: List<GoalProgress>,
        recent: List<RecentActivity>,
    ) {
        val root =
            buildJsonObject {
                put(
                    "\$comment",
                    "DERIVED — generated by hugo-book-manager `./gradlew refreshGoalProgress`. Do not edit by hand.",
                )
                put("generatedAt", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                put(
                    "goals",
                    buildJsonObject {
                        for (p in progress) {
                            put(
                                p.goalId,
                                buildJsonObject {
                                    put("done", p.done)
                                    put("total", p.total)
                                    put("unit", p.unit)
                                    put(
                                        "breakdown",
                                        buildJsonArray {
                                            for (b in p.breakdown.sortedBy { it.key }) {
                                                add(
                                                    buildJsonObject {
                                                        put("key", b.key)
                                                        put("done", b.done)
                                                        put("total", b.total)
                                                        b.section?.let { put("section", it) }
                                                    },
                                                )
                                            }
                                        },
                                    )
                                },
                            )
                        }
                    },
                )
                put(
                    "recent",
                    buildJsonArray {
                        for (r in recent.take(MAX_RECENT)) {
                            add(
                                buildJsonObject {
                                    put("date", r.date)
                                    put("goalId", r.goalId)
                                    put("item", r.item)
                                },
                            )
                        }
                    },
                )
            }
        progressFile.writeText(prettyJson.encodeToString(JsonObject.serializer(), root) + "\n")
    }

    /** Breakdown grouping key: the display key plus the station URL segment it lives under. */
    private data class CatKey(
        val key: String,
        val section: String? = null,
    )

    private fun toProgress(
        goalId: String,
        byCategory: Map<CatKey, Pair<Int, Int>>,
        unit: String,
    ): GoalProgress {
        val done = byCategory.values.sumOf { it.first }
        val total = byCategory.values.sumOf { it.second }
        return GoalProgress(
            goalId = goalId,
            done = done,
            total = total,
            unit = unit,
            breakdown = byCategory.map { (k, v) -> GoalProgress.BreakdownEntry(k.key, v.first, v.second, k.section) },
        )
    }

    private fun contentFiles(base: File): List<File> =
        base
            .walkTopDown()
            .filter { it.isFile && it.extension == "md" && it.name != "_index.md" }
            .sortedBy { it.path }
            .toList()

    /** Parse the YAML frontmatter block between the leading `---` fences. */
    private fun frontmatter(
        file: File,
        goalId: String,
    ): Map<String, Any> {
        val text = file.readText()
        if (!text.startsWith("---")) return emptyMap()
        val end = text.indexOf("\n---", startIndex = 3)
        if (end < 0) return emptyMap()
        return try {
            @Suppress("UNCHECKED_CAST")
            (yaml.load(text.substring(3, end)) as? Map<String, Any>) ?: emptyMap()
        } catch (e: Exception) {
            println("⚠ $goalId: bad frontmatter in ${file.path} (${e.message}) — treated as empty")
            emptyMap()
        }
    }

    /** Hugo/Astro have no zod on our side — validate dates and warn loudly. */
    private fun parseDate(
        raw: Any?,
        file: File,
        goalId: String,
    ): LocalDate? {
        // An unquoted `readAt: 2026-07-21` is a YAML timestamp, which SnakeYAML
        // hands back as a Date whose toString() ("Tue Jul 21 ...") would fail
        // the text parse below and silently drop the activity.
        if (raw is java.util.Date) {
            return raw.toInstant().atZone(ZoneOffset.UTC).toLocalDate()
        }
        val s = raw?.toString()?.trim().orEmpty()
        if (s.isEmpty()) return null
        return try {
            LocalDate.parse(s.take(10))
        } catch (e: Exception) {
            println("⚠ $goalId: bad date '$s' in ${file.path} — ignored")
            null
        }
    }

    private fun stringList(raw: Any?): List<String> = (raw as? List<*>)?.map { it.toString() } ?: emptyList()

    companion object {
        private const val MAX_RECENT = 50
        private val prettyJson =
            Json {
                prettyPrint = true
                prettyPrintIndent = "  "
            }
    }
}
