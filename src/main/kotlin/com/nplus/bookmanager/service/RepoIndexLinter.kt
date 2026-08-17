package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.RepoIndex

/**
 * Validates the packed `description` field that every book repo carries.
 *
 * The description is the single source of truth for three separate values,
 * positionally encoded as `Title | Author | Blurb`, and it is consumed by the
 * portal (`src/lib/data.ts`), which derives `/authors/<slug>` pages straight
 * from segment two. Nothing on the write path enforces the shape — a repo
 * description can be edited on GitHub at any time — so this linter is the
 * reconciliation pass, run by `refresh-repo-index` right after the index is
 * pulled back from GitHub.
 *
 * Findings are pure functions of the index so they can be unit-tested without
 * touching the network; `refresh-repo-index` only prints them.
 */
object RepoIndexLinter {
    enum class Severity { ERROR, WARNING }

    data class Finding(
        val repo: String,
        val severity: Severity,
        val code: String,
        val detail: String,
    )

    /** GitHub caps a repo description at 350; warn before authors hit the wall. */
    const val LENGTH_WARN_THRESHOLD = 330

    /**
     * Below this weight a blurb says nothing useful on a portal card.
     * Measured in [blurbWeight], not characters: a 22-character Chinese blurb
     * is a complete sentence, while a 21-character English one
     * ("40 keys to creativity") is barely a label.
     */
    const val MIN_BLURB_WEIGHT = 50

    /** CJK ideographs carry far more meaning per character than Latin letters. */
    private const val CJK_WEIGHT = 25 // ×0.01, kept integral to avoid float drift

    /**
     * Information-content estimate for a blurb, normalising CJK against Latin
     * so one threshold can serve a library that is roughly a fifth Chinese.
     */
    fun blurbWeight(blurb: String): Int =
        blurb.sumOf { ch ->
            if (Character.UnicodeScript.of(ch.code) == Character.UnicodeScript.HAN) CJK_WEIGHT else 10
        } / 10

    /**
     * Words that mean "this description was never filled in properly". Caught
     * a real case: `grid-notebook` shipped with the title `讀書筆記模版`.
     */
    private val PLACEHOLDER_MARKERS = listOf("模版", "模板", "template", "todo", "untitled", "tbd")

    private val SEPARATOR = Regex("[|｜]")

    fun lint(index: RepoIndex): List<Finding> = index.repos.flatMap(::lintEntry) + lintDuplicates(index)

    private fun lintEntry(entry: RepoIndex.RepoEntry): List<Finding> {
        val findings = mutableListOf<Finding>()

        fun add(
            severity: Severity,
            code: String,
            detail: String,
        ) = findings.add(Finding(entry.name, severity, code, detail))

        val description = entry.description.trim()
        if (description.isEmpty()) {
            add(Severity.ERROR, "empty-description", "description is blank")
            return findings
        }

        if (description.length > LENGTH_WARN_THRESHOLD) {
            add(
                Severity.WARNING,
                "near-length-cap",
                "${description.length} chars, GitHub caps at 350",
            )
        }

        // Handbooks deliberately use a shorter, unpacked form; only books carry
        // the three-segment contract the portal parses.
        if (!entry.isBook) return findings

        val parts = description.split(SEPARATOR).map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size < 3) {
            add(
                Severity.ERROR,
                "malformed-description",
                "expected 'Title | Author | Blurb', got ${parts.size} segment(s)",
            )
            return findings
        }

        val (title, author) = parts
        val blurb = parts.drop(2).joinToString(" ")

        PLACEHOLDER_MARKERS.firstOrNull { title.lowercase().contains(it) }?.let {
            add(Severity.ERROR, "placeholder-title", "title looks unfilled: \"$title\"")
        }
        val weight = blurbWeight(blurb)
        if (weight < MIN_BLURB_WEIGHT) {
            add(Severity.WARNING, "short-blurb", "weight $weight (min $MIN_BLURB_WEIGHT): \"$blurb\"")
        }
        // Deliberately NOT checking the author separator. The portal slugifies
        // the whole author string and collapses every non-alphanumeric run to a
        // single dash, so "A & B" and "A, B" yield the identical slug — style
        // here cannot split or duplicate an author page.

        return findings
    }

    /**
     * Two repos for the same book. Real cases found in the library: Cialdini's
     * *Influence*, de Botton's *Status Anxiety*, 安納金's 一個投機者的告白實戰書,
     * and Schwager's *A Complete Guide to the Futures Market* each had two.
     * Keyed on title+author so genuinely distinct same-titled books — Kahneman's
     * *Noise* vs McCormack's *Noise* — do not trip it.
     */
    private fun lintDuplicates(index: RepoIndex): List<Finding> =
        index.repos
            .filter { it.isBook }
            .mapNotNull { entry ->
                val parts =
                    entry.description
                        .split(SEPARATOR)
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                if (parts.size < 2) null else DuplicateKey(parts[0], parts[1]) to entry.name
            }.groupBy({ it.first }, { it.second })
            .filterValues { it.size > 1 }
            .flatMap { (key, names) ->
                names.sorted().map { name ->
                    Finding(
                        name,
                        Severity.ERROR,
                        "duplicate-book",
                        "\"${key.title}\" by ${key.author} also exists as ${(names - name).sorted().joinToString(", ")}",
                    )
                }
            }

    /**
     * Case- and whitespace-insensitive identity for a book.
     *
     * Keyed on the *lead* author only, because the same book is often credited
     * differently across two repos — Schwager's futures guide was filed once as
     * "Jack D. Schwager" and once as "Jack D. Schwager & Mark Etzkorn". Matching
     * the full author string missed that pair; matching title alone would
     * wrongly merge Kahneman's *Noise* with McCormack's.
     */
    private data class DuplicateKey(
        private val rawTitle: String,
        private val rawAuthor: String,
    ) {
        val title: String get() = rawTitle
        val author: String get() = rawAuthor

        private val normTitle = normalize(rawTitle)
        private val normLeadAuthor = normalize(rawAuthor.split('&', ',').first())

        override fun equals(other: Any?): Boolean =
            other is DuplicateKey && other.normTitle == normTitle && other.normLeadAuthor == normLeadAuthor

        override fun hashCode(): Int = 31 * normTitle.hashCode() + normLeadAuthor.hashCode()

        private fun normalize(value: String) = value.lowercase().replace(Regex("\\s+"), " ").trim()
    }

    private val RepoIndex.RepoEntry.isBook: Boolean
        get() = topics.contains("nplus-kind-book")
}
