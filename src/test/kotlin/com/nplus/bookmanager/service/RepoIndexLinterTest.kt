package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.RepoIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepoIndexLinterTest {
    private fun book(
        name: String,
        description: String,
        kind: String = "nplus-kind-book",
    ) = RepoIndex.RepoEntry(
        name = name,
        description = description,
        url = "https://github.com/nplus-father/$name",
        topics = listOf("hugobook", kind),
    )

    private fun lint(vararg entries: RepoIndex.RepoEntry) = RepoIndexLinter.lint(RepoIndex(lastUpdated = null, repos = entries.toList()))

    private fun lintWithLinks(
        links: Map<String, String>,
        vararg entries: RepoIndex.RepoEntry,
    ) = RepoIndexLinter.lint(RepoIndex(lastUpdated = null, repos = entries.toList()), links)

    private fun codes(vararg entries: RepoIndex.RepoEntry) = lint(*entries).map { it.code }

    @Test
    fun `a well-formed description produces no findings`() {
        val ok = book("deep-work", "Deep Work | Cal Newport | Argues that focused work is the superpower of the modern economy.")
        assertEquals(emptyList(), lint(ok))
    }

    @Test
    fun `blank description is an error`() {
        val findings = lint(book("adam", "  "))
        assertEquals(listOf("empty-description"), findings.map { it.code })
        assertEquals(RepoIndexLinter.Severity.ERROR, findings.single().severity)
    }

    @Test
    fun `fewer than three segments is malformed`() {
        assertTrue("malformed-description" in codes(book("x", "Title | Author")))
    }

    @Test
    fun `handbooks are exempt from the three-segment contract`() {
        val handbook = book("java-mastery", "Java 精通", kind = "nplus-kind-handbook")
        assertEquals(emptyList(), lint(handbook))
    }

    @Test
    fun `a placeholder title is an error`() {
        val findings =
            lint(
                book(
                    "grid-notebook",
                    "讀書筆記模版 | 高橋政史 | 以麥肯錫顧問的方格筆記法為核心，教導讀者運用三分割架構，把筆記變成主動思考的工具。",
                ),
            )
        assertEquals(listOf("placeholder-title"), findings.map { it.code })
    }

    @Test
    fun `a very short blurb warns`() {
        val findings = lint(book("x", "Ignore Everybody | Hugh MacLeod | 40 keys to creativity"))
        assertEquals(listOf("short-blurb"), findings.map { it.code })
        assertEquals(RepoIndexLinter.Severity.WARNING, findings.single().severity)
    }

    @Test
    fun `a short but complete Chinese blurb is accepted`() {
        assertEquals(
            emptyList(),
            lint(book("fengtang-buer", "不二 | 馮唐 | 馮唐的長篇小說，藉情慾敘事探問肉身、自由與人性")),
        )
    }

    @Test
    fun `blurb weight counts Han characters above Latin ones`() {
        assertTrue(RepoIndexLinter.blurbWeight("馮唐的長篇小說") > RepoIndexLinter.blurbWeight("abcdefg"))
    }

    @Test
    fun `a description near the GitHub cap warns`() {
        val long = "T | A | " + "x".repeat(RepoIndexLinter.LENGTH_WARN_THRESHOLD)
        assertTrue("near-length-cap" in codes(book("x", long)))
    }

    @Test
    fun `author separator style is not policed`() {
        assertEquals(
            emptyList(),
            lint(
                book(
                    "getting-to-yes",
                    "Getting to Yes | Roger Fisher, William Ury & Bruce Patton | The Harvard method for principled negotiation on the merits rather than by haggling.",
                ),
            ),
        )
    }

    @Test
    fun `two repos for the same book are flagged on both sides`() {
        val findings =
            lint(
                book(
                    "influence-cialdini",
                    "Influence | Robert B. Cialdini | The classic distilled from three years undercover, reducing countless compliance tactics to six principles.",
                ),
                book(
                    "influence-psychology",
                    "Influence | Robert B. Cialdini | Combines academic research with undercover fieldwork to derive six principles of persuasion.",
                ),
            )
        assertEquals(listOf("duplicate-book", "duplicate-book"), findings.map { it.code })
        assertTrue(findings.any { it.repo == "influence-cialdini" && it.detail.contains("influence-psychology") })
    }

    @Test
    fun `the same book under two languages is caught by its purchase link`() {
        val findings =
            lintWithLinks(
                mapOf(
                    "war-of-words" to "https://www.amazon.com/War-Words/dp/B00LUUWA7K",
                    "tongue-a-creative-force" to "https://www.amazon.com/-/zh_TW/dp/B00LUUWA7K?ref=x",
                ),
                book(
                    "war-of-words",
                    "War of Words | Paul David Tripp | Reads everyday communication struggles through the gospel, arguing that words reveal the heart before they change anything else.",
                ),
                book("tongue-a-creative-force", "言語的威力 | Paul David Tripp | 從改革宗聖經輔導視角切入溝通困境，主張話語顯明心靈。"),
            )
        assertEquals(listOf("duplicate-book", "duplicate-book"), findings.map { it.code })
    }

    @Test
    fun `different books by one author are not merged just because both have links`() {
        assertEquals(
            emptyList(),
            lintWithLinks(
                mapOf(
                    "a" to "https://www.amazon.com/dp/AAAAAAAAAA",
                    "b" to "https://www.amazon.com/dp/BBBBBBBBBB",
                ),
                book("a", "Book One | Same Author | A first book with a perfectly adequate blurb attached."),
                book("b", "Book Two | Same Author | A second book with a perfectly adequate blurb attached."),
            ),
        )
    }

    @Test
    fun `a repo without a known link still falls back to title and author`() {
        assertTrue(
            "duplicate-book" in
                lintWithLinks(
                    emptyMap(),
                    book("x", "Influence | Robert B. Cialdini | The classic distilled from three years undercover work."),
                    book("y", "Influence | Robert B. Cialdini | Six principles of persuasion drawn from field research."),
                ).map { it.code },
        )
    }

    @Test
    fun `same title by different authors is not a duplicate`() {
        assertEquals(
            emptyList(),
            lint(
                book(
                    "noise",
                    "Noise | Daniel Kahneman | On the variability of human judgment: the same case, different judges, wildly different outcomes.",
                ),
                book(
                    "noise-focus",
                    "Noise | Joseph McCormack | On how information overload erodes attention and human connection, and two frameworks for cutting through it.",
                ),
            ),
        )
    }

    @Test
    fun `duplicate detection matches on the lead author when co-authors differ`() {
        assertTrue(
            "duplicate-book" in
                codes(
                    book(
                        "futures-market",
                        "A Complete Guide to the Futures Market | Jack D. Schwager | The definitive reference on futures trading, covering charts, indicators and quantitative analysis.",
                    ),
                    book(
                        "futures-markets",
                        "A Complete Guide to the Futures Market | Jack D. Schwager & Mark Etzkorn | Thirty years of interviewing top traders distilled into a working reference.",
                    ),
                ),
        )
    }

    @Test
    fun `duplicate detection ignores case and spacing`() {
        assertTrue(
            "duplicate-book" in
                codes(
                    book(
                        "a",
                        "Status  Anxiety | Alain de Botton | Diagnoses the modern roots of status anxiety and seeks remedies across philosophy, art, and religion.",
                    ),
                    book(
                        "b",
                        "status anxiety | alain de botton | A humanistic reflection on the causes of status anxiety and the possibility of dissolving it.",
                    ),
                ),
        )
    }
}
