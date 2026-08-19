package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Publishes how far the deep-overview rewrite has got, as `src/data/overview.json`
 * in the portal — the same derived-artifact pattern as `refreshBookHealth`.
 *
 * Until now the campaign was tracked by two things that cannot answer "which
 * books still need doing" on their own: `overview-lanes/lane-*.txt`, a frozen
 * snapshot of the backlog at the moment the lanes were carved (1,087 of the
 * 1,753 repos — a repo created afterwards can never appear in it), and
 * `OVERVIEW-PROGRESS.md`, which is only as accurate as the last time someone
 * remembered to regenerate it. On 2026-08-19 it still claimed 686 rewritten
 * when the real figure was 1,703.
 *
 * ## Where each field comes from
 *
 * `state` is decided by one rule, applied identically by all three producers of
 * this artifact — this service, the theme's `layouts/index.json`, and the
 * portal's `fetch-overview.ts`: does the home page's markdown contain
 * `{{% book-overview %}}`. It is deliberately not derived from the audit's check
 * results, because a four-section overview that fails a length check is still a
 * rewritten book, and a legacy one is still legacy however many checks it
 * happens to pass. `next.sh` and `verify.sh` have always keyed on this same
 * substring, so the three tools cannot disagree about what "done" means.
 *
 * Everything else — section lengths, distinct years, named works, filler words,
 * sentence count in the limits section — comes from `audit-overview.py --all
 * --json`. That script is the gate `/book-generate-deep-overview` enforces, so
 * it owns the thresholds; reimplementing its ten checks in Kotlin would create a
 * second definition free to drift from the one that actually blocks a commit.
 */
class OverviewCoverageService(
    private val booksDir: File,
    private val portalDir: File,
    /**
     * In-progress books, deliberately flat (no category folders). Scanned for the
     * same reason `BookHealthService` scans them: they are exactly the books whose
     * overview has not been written yet, and leaving them out would hide the only
     * part of the backlog that is still growing.
     */
    private val newBooksDir: File? = null,
    private val auditScript: File = File("scripts/audit-overview.py"),
    /** Seam for tests: the real one shells out to python. */
    private val runAudit: (File, File) -> String? = { script, root ->
        ProcessRunner.executeForOutput(
            "python3 ${shellQuote(script.path)} --all --json --root ${shellQuote(root.path)}",
            timeoutSeconds = AUDIT_TIMEOUT_SECONDS,
        )
    },
) {
    val overviewFile: File get() = File(portalDir, "src/data/overview.json")

    companion object {
        /** Section names and bounds mirror `audit-overview.py`'s SECTION_CHARS verbatim. */
        val SECTIONS = listOf("作者的位置", "完整摘要", "定位", "這本書的限制")
        val SECTION_CHARS =
            mapOf(
                "作者的位置" to (250 to 800),
                "完整摘要" to (600 to 1700),
                "定位" to (250 to 800),
                "這本書的限制" to (300 to 900),
            )
        const val MIN_YEARS = 2
        const val MIN_REFS = 3
        const val MIN_LIMIT_SENTENCES = 3

        /** The marker every producer keys on. A constant so it stays greppable. */
        const val MARKER = "{{% book-overview %}}"
        const val LEGACY_MARKER = "深度概覽"

        /**
         * A full scan reads 1,700+ home pages and walks every `docs/` tree for the
         * note volume. The script's docstring claims six seconds; measured it is
         * closer to forty, and a cold page cache is slower still.
         */
        const val AUDIT_TIMEOUT_SECONDS = 600L

        private fun shellQuote(s: String) = "'" + s.replace("'", "'\\''") + "'"

        /**
         * Which of the three states this repo is in. Reading the file costs one
         * open per repo and is exact; inferring it from the audit's `legacy` flag
         * is not — that flag only reports that a `details` block exists, and says
         * nothing about a home page carrying no overview at all, which is where
         * every book in `new-books/` currently sits.
         */
        fun stateOf(repo: File): String {
            val home = File(repo, "site/content/_index.md")
            if (!home.isFile) return "none"
            val text = home.readText()
            return when {
                text.contains(MARKER) -> "done"
                text.contains(LEGACY_MARKER) -> "legacy"
                else -> "none"
            }
        }
    }

    data class Book(
        val slug: String,
        val top: String,
        val sub: String,
        val leaf: String,
        val state: String,
        val fails: Int,
        val failed: List<String>,
        val chars: Int,
        val sections: Map<String, Int>,
        val years: Int,
        val refs: Int,
        val filler: Int,
        val limitSentences: Int,
        /** Note volume in bytes, so a queue can be ordered by how much there is to work with. */
        val notes: Int,
    )

    /**
     * Run the audit over both roots and join it with the state of each home page.
     *
     * Returns null — rather than an empty list — when the audit could not be run,
     * so the caller can refuse to overwrite a good artifact with nothing.
     */
    fun scan(): List<Book>? {
        val fromBooks = scanRoot(booksDir, categorised = true) ?: return null
        val fromNew =
            if (newBooksDir?.isDirectory == true) {
                scanRoot(newBooksDir, categorised = false) ?: return null
            } else {
                emptyList()
            }
        return (fromBooks + fromNew).sortedBy { it.slug }
    }

    private fun scanRoot(
        root: File,
        categorised: Boolean,
    ): List<Book>? {
        if (!root.isDirectory) return emptyList()
        val out = runAudit(auditScript, root) ?: return null
        return parse(out, root, categorised)
    }

    /**
     * Pure, so it can be tested without python on the box. `slug` in the audit's
     * output is the path relative to the root it was given: three category folders
     * plus the repo under `books-done`, a bare repo name under `new-books`.
     */
    internal fun parse(
        json: String,
        root: File,
        categorised: Boolean,
    ): List<Book> {
        val rows = Json.parseToJsonElement(json.trim()) as? JsonArray ?: return emptyList()
        return rows.mapNotNull { element ->
            val row = element as? JsonObject ?: return@mapNotNull null
            val rel = row["slug"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val parts = rel.split("/")
            Book(
                slug = parts.last(),
                top = if (categorised && parts.size >= 4) parts[parts.size - 4] else "",
                sub = if (categorised && parts.size >= 3) parts[parts.size - 3] else "",
                leaf = if (categorised && parts.size >= 2) parts[parts.size - 2] else "",
                state = stateOf(File(root, rel)),
                fails = row["fails"]?.jsonPrimitive?.intOrNull ?: 0,
                failed = row["failed"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList(),
                chars = row["total"]?.jsonPrimitive?.intOrNull ?: 0,
                sections =
                    row["sections"]?.jsonObject?.let { s ->
                        SECTIONS.associateWith { s[it]?.jsonPrimitive?.intOrNull ?: 0 }
                    } ?: SECTIONS.associateWith { 0 },
                years = row["years"]?.jsonPrimitive?.intOrNull ?: 0,
                refs = row["latin"]?.jsonPrimitive?.intOrNull ?: 0,
                filler = row["filler"]?.jsonPrimitive?.intOrNull ?: 0,
                limitSentences = row["limit_sentences"]?.jsonPrimitive?.intOrNull ?: 0,
                notes = row["notes"]?.jsonPrimitive?.intOrNull ?: 0,
            )
        }
    }

    /** Write the derived artifact with stable ordering for clean git diffs. */
    fun save(books: List<Book>) {
        val root =
            buildJsonObject {
                put(
                    "\$comment",
                    "DERIVED — generated by hugo-book-manager `./gradlew refreshOverviewCoverage`. Do not edit by hand.",
                )
                put("generatedAt", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                put(
                    "thresholds",
                    buildJsonObject {
                        put(
                            "sections",
                            buildJsonObject {
                                for (name in SECTIONS) {
                                    val (lo, hi) = SECTION_CHARS.getValue(name)
                                    put(
                                        name,
                                        buildJsonObject {
                                            put("min", lo)
                                            put("max", hi)
                                        },
                                    )
                                }
                            },
                        )
                        put("minYears", MIN_YEARS)
                        put("minRefs", MIN_REFS)
                        put("minLimitSentences", MIN_LIMIT_SENTENCES)
                    },
                )
                put("books", buildJsonArray { for (b in books) add(bookJson(b)) })
            }
        overviewFile.parentFile?.mkdirs()
        overviewFile.writeText(prettyJson.encodeToString(JsonObject.serializer(), root) + "\n")
    }

    private fun bookJson(b: Book): JsonObject =
        buildJsonObject {
            put("slug", b.slug)
            put("top", b.top)
            put("sub", b.sub)
            put("leaf", b.leaf)
            put("state", b.state)
            put("fails", b.fails)
            put("failed", buildJsonArray { for (f in b.failed) add(JsonPrimitive(f)) })
            put("chars", b.chars)
            put(
                "sections",
                buildJsonObject {
                    for (name in SECTIONS) put(name, b.sections[name] ?: 0)
                },
            )
            put("years", b.years)
            put("refs", b.refs)
            put("filler", b.filler)
            put("limitSentences", b.limitSentences)
            put("notes", b.notes)
        }

    /**
     * Two-space indent, matching the portal's prettier config — kotlinx defaults
     * to four, which fails `npm run lint` on the very next commit of the file.
     * `health.json` learned this the hard way on 2026-08-17.
     */
    private val prettyJson =
        Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
}
