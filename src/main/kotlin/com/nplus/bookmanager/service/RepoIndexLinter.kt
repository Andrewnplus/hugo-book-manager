package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.RepoIndex

object RepoIndexLinter {
    enum class Severity { ERROR, WARNING }

    data class Finding(
        val repo: String,
        val severity: Severity,
        val code: String,
        val detail: String,
    )

    const val LENGTH_WARN_THRESHOLD = 330

    const val MIN_BLURB_WEIGHT = 50

    private const val CJK_WEIGHT = 25

    fun blurbWeight(blurb: String): Int =
        blurb.sumOf { ch ->
            if (Character.UnicodeScript.of(ch.code) == Character.UnicodeScript.HAN) CJK_WEIGHT else 10
        } / 10

    private val PLACEHOLDER_MARKERS = listOf("模版", "模板", "template", "todo", "untitled", "tbd")

    private val SEPARATOR = Regex("[|｜]")

    fun lint(
        index: RepoIndex,
        links: Map<String, String> = emptyMap(),
    ): List<Finding> = index.repos.flatMap(::lintEntry) + lintDuplicates(index, links)

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

        return findings
    }

    private fun lintDuplicates(
        index: RepoIndex,
        links: Map<String, String>,
    ): List<Finding> {
        val books = index.repos.filter { it.isBook }
        val groups = mutableMapOf<Any, MutableList<String>>()
        val labels = mutableMapOf<Any, String>()

        for (entry in books) {
            val parts =
                entry.description
                    .split(SEPARATOR)
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            val link = links[entry.name]?.let(::normalizeLink)
            val key: Any? =
                when {
                    !link.isNullOrBlank() -> LinkKey(link)
                    parts.size >= 2 -> DuplicateKey(parts[0], parts[1])
                    else -> null
                }
            if (key == null) continue
            groups.getOrPut(key) { mutableListOf() }.add(entry.name)
            labels[key] = parts.firstOrNull() ?: entry.name
        }

        return groups
            .filterValues { it.size > 1 }
            .flatMap { (key, names) ->
                names.sorted().map { name ->
                    Finding(
                        name,
                        Severity.ERROR,
                        "duplicate-book",
                        "\"${labels[key]}\" also exists as ${(names - name).sorted().joinToString(", ")}",
                    )
                }
            }
    }

    private fun normalizeLink(raw: String): String {
        val lower =
            raw
                .trim()
                .lowercase()
                .substringBefore('?')
                .substringBefore('#')
        val asin = Regex("/(?:dp|gp/product|product)/([a-z0-9]{10})").find(lower)?.groupValues?.get(1)
        return if (asin != null) "asin:$asin" else lower.trimEnd('/')
    }

    private data class LinkKey(
        private val value: String,
    )

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
