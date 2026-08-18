package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Measures how much has actually been written in each book repo, and publishes
 * the result as `src/data/health.json` in the portal (same derived-artifact
 * pattern as `refreshGoalProgress`).
 *
 * The two metrics are the ones the library's own re-summary list has always
 * used, and this implementation is calibrated to reproduce that list's numbers
 * exactly — verified against dignity-of-speaking (6,648/29/229), zen-programmer,
 * whats-left-without-your-business-card and learning-to-be-deceived:
 *
 *  - chars: every markdown file anywhere under `site/content/docs`, frontmatter
 *    removed, whitespace KEPT. Stripping whitespace or folding in the home page
 *    both break the correspondence with the thresholds below.
 *  - pages: how many such files there are — the home `_index.md` is excluded.
 *  - density = chars / pages, rounded. Total alone hides a book with many empty
 *    chapters; density alone punishes books whose chapters are meant to be short.
 */
class BookHealthService(
    private val booksDir: File,
    private val portalDir: File,
    /**
     * In-progress books, deliberately kept flat (no category folders) until
     * `migrate-topic-tiers` files them. Scanned too, because otherwise a run of
     * this command would drop them from health.json entirely — `fetch-health.ts`
     * does see them, since repos.json lists every book repo regardless of where
     * its clone lives.
     */
    private val newBooksDir: File? = null,
) {
    val healthFile: File get() = File(portalDir, "src/data/health.json")

    /** Tier thresholds carried over from `_resummary-candidates.md`. */
    companion object {
        const val NEAR_EMPTY_DENSITY = 250
        const val THIN_CHARS = 8_000
        const val WATCH_CHARS = 15_000

        private val ISO_DATE = Regex("""\d{4}-\d{2}-\d{2}""")

        /**
         * Frontmatter runs to the second `---` line. A `---` inside the body
         * (a horizontal rule) still increments the counter and is itself never
         * counted, which is what the reference implementation did.
         */
        fun bodyChars(file: File): Int {
            var fence = 0
            var total = 0
            file.forEachLine { line ->
                if (line.trim() == "---") {
                    fence++
                } else if (fence >= 2) {
                    total += line.length + 1
                }
            }
            return total
        }

        /**
         * A chapter Hugo will not build, and therefore one the published site
         * does not have. Counting it here would make the local scan disagree
         * with `fetch-health.ts`, which can only ever see what was deployed —
         * `dictionary-of-the-later-new-testament` carries 121 such chapters and
         * the two sources differed by exactly those.
         */
        fun isDraft(file: File): Boolean {
            var fence = 0
            var draft = false
            file.forEachLine { line ->
                if (line.trim() == "---") {
                    fence++
                } else if (fence == 1 && line.trim().replace(" ", "") == "draft:true") {
                    draft = true
                }
            }
            return draft
        }
    }

    data class Book(
        val slug: String,
        val top: String,
        val sub: String,
        val leaf: String,
        val chars: Int,
        val pages: Int,
        /** ISO date of the last commit touching the chapters; null if unknown. */
        val lastWritten: String? = null,
    ) {
        val density: Int get() = if (pages == 0) 0 else (chars + pages / 2) / pages

        /** Which bucket of the re-summary queue this book falls in. */
        val tier: String
            get() =
                when {
                    density < NEAR_EMPTY_DENSITY -> "near-empty"
                    chars < THIN_CHARS -> "thin"
                    chars < WATCH_CHARS -> "watch"
                    else -> "ok"
                }
    }

    /**
     * Walk `<booksDir>/<top>/<sub>/<leaf>/<slug>`. The depth is fixed rather
     * than discovered: a shallower walk would sweep in the category folders
     * themselves, and `migrate-topic-tiers` guarantees this shape.
     */
    fun scan(): List<Book> {
        if (!booksDir.isDirectory) return emptyList()
        val books = mutableListOf<Book>()
        for (top in booksDir.listDirs()) {
            for (sub in top.listDirs()) {
                for (leaf in sub.listDirs()) {
                    for (repo in leaf.listDirs()) {
                        measure(repo, top.name, sub.name, leaf.name)?.let { books += it }
                    }
                }
            }
        }
        newBooksDir?.takeIf { it.isDirectory }?.let { dir ->
            for (repo in dir.listDirs()) {
                measure(repo, top = "", sub = "", leaf = "")?.let { books += it }
            }
        }
        return books.sortedBy { it.slug }
    }

    /**
     * Taxonomy is passed in rather than derived: books-done encodes it in the
     * path, new-books has none yet. The portal fills the blanks from repo
     * topics, which are the actual source of truth for classification.
     */
    private fun measure(
        repo: File,
        top: String,
        sub: String,
        leaf: String,
    ): Book? {
        val docs = File(repo, "site/content/docs")
        if (!docs.isDirectory) return null
        val pages =
            docs
                .walkTopDown()
                .filter { it.isFile && it.extension == "md" && !isDraft(it) }
                .toList()
        if (pages.isEmpty()) return null
        return Book(
            slug = repo.name,
            top = top,
            sub = sub,
            leaf = leaf,
            chars = pages.sumOf { bodyChars(it) },
            pages = pages.size,
            lastWritten = lastWritten(repo),
        )
    }

    private fun File.listDirs(): List<File> =
        listFiles()?.filter { it.isDirectory && !it.name.startsWith(".") }?.sortedBy { it.name } ?: emptyList()

    /**
     * Date of the last commit that touched the chapters, so a thin book that is
     * being actively written can be told apart from one abandoned two years ago
     * — the dashboard treats those identically on character count alone.
     *
     * Scoped to `site/content/docs` on purpose: fleet-wide chores (dependency
     * bumps, the frontmatter migration, cover re-encoding) touch every repo and
     * would otherwise make every book look freshly worked on. File mtimes are
     * useless here for the same reason.
     *
     * Author date, not committer date: a rebase rewrites the latter, and these
     * repos do get rebased onto their remote, which would date a book to the
     * rebase rather than to when it was written.
     */
    private fun lastWritten(repoDir: File): String? {
        val result =
            ProcessRunner.execute(
                "git log -1 --format=%aI -- site/content/docs",
                workingDir = repoDir,
                timeoutSeconds = 15,
            )
        if (!result.success) return null
        return result.stdout
            .trim()
            .take(10)
            .takeIf { it.matches(ISO_DATE) }
    }

    /** Write the derived artifact with stable ordering for clean git diffs. */
    fun save(books: List<Book>) {
        val root =
            buildJsonObject {
                put(
                    "\$comment",
                    "DERIVED — generated by hugo-book-manager `./gradlew refreshBookHealth`. Do not edit by hand.",
                )
                put("generatedAt", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                put(
                    "thresholds",
                    buildJsonObject {
                        put("nearEmptyDensity", NEAR_EMPTY_DENSITY)
                        put("thinChars", THIN_CHARS)
                        put("watchChars", WATCH_CHARS)
                    },
                )
                put("books", buildJsonArray { for (b in books) add(bookJson(b)) })
            }
        healthFile.parentFile?.mkdirs()
        healthFile.writeText(prettyJson.encodeToString(JsonObject.serializer(), root) + "\n")
    }

    private fun bookJson(b: Book): JsonObject =
        buildJsonObject {
            put("slug", b.slug)
            put("top", b.top)
            put("sub", b.sub)
            put("leaf", b.leaf)
            put("chars", b.chars)
            put("pages", b.pages)
            put("density", b.density)
            put("tier", b.tier)
            b.lastWritten?.let { put("lastWritten", it) }
        }

    /**
     * Two-space indent, matching the portal's prettier config and
     * `fetch-health.ts`'s `JSON.stringify(_, null, 2)`. kotlinx defaults to four,
     * which makes `npm run lint` (prettier --check) fail on the very next commit
     * of `health.json` — it did, on 2026-08-17 and again on the 02:00 UTC rebuild.
     */
    private val prettyJson =
        Json {
            prettyPrint = true
            prettyPrintIndent = "  "
        }
}
