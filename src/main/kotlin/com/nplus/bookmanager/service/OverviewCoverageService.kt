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

class OverviewCoverageService(
    private val booksDir: File,
    private val portalDir: File,
    private val newBooksDir: File? = null,
    private val auditScript: File = File("scripts/audit-overview.py"),
    private val runAudit: (File, File) -> String? = { script, root ->
        ProcessRunner.executeForOutput(
            "python3 ${shellQuote(script.path)} --all --json --root ${shellQuote(root.path)}",
            timeoutSeconds = AUDIT_TIMEOUT_SECONDS,
        )
    },
) {
    val overviewFile: File get() = File(portalDir, "src/data/overview.json")

    companion object {
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

        const val MARKER = "{{% book-overview %}}"
        const val LEGACY_MARKER = "深度概覽"

        const val AUDIT_TIMEOUT_SECONDS = 600L

        private fun shellQuote(s: String) = "'" + s.replace("'", "'\\''") + "'"

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
        val notes: Int,
    )

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

    private val prettyJson =
        Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
}
