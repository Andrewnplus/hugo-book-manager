package com.nplus.bookmanager.util

import kotlin.test.Test
import kotlin.test.assertEquals

class QuotedValueTest {
    @Test
    fun `leaves an ordinary title untouched`() {
        assertEquals("敏捷整潔之道", escapeQuoted("敏捷整潔之道"))
    }

    @Test
    fun `escapes the inner quotes that broke four repositories`() {
        assertEquals("""Think \"Win-Win\"""", escapeQuoted("""Think "Win-Win""""))
        assertEquals(
            """Liber Novus: The \"Red Book\" of C. G. Jung""",
            escapeQuoted("""Liber Novus: The "Red Book" of C. G. Jung"""),
        )
    }

    @Test
    fun `escapes a title that is entirely a quotation`() {
        assertEquals("""\"Subject Ramsay Was Naked…\"""", escapeQuoted(""""Subject Ramsay Was Naked…""""))
    }

    @Test
    fun `escapes backslashes before quotes, not after`() {
        assertEquals("""a\\b\"c""", escapeQuoted("""a\b"c"""))
    }

    @Test
    fun `leaves an apostrophe alone since the context is double-quoted`() {
        assertEquals("Something's Got to Give", escapeQuoted("Something's Got to Give"))
    }
}
