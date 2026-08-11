package com.nplus.bookmanager.util

/**
 * Escape a value that is about to be interpolated into a double-quoted string.
 *
 * Covers every double-quoted context this CLI writes: YAML frontmatter
 * (`title: "..."`), TOML config (`title = "..."`) and Hugo shortcode parameters
 * (`title="..."`). All three use backslash escapes inside double quotes, so one
 * function is enough.
 *
 * This is not cosmetic. Book and chapter titles routinely carry quotes —
 * `Think "Win-Win"`, `Liber Novus: The "Red Book" of C. G. Jung` — and an
 * unescaped one produces frontmatter Hugo refuses to parse, so the repository's
 * very first deploy fails. Four repositories shipped that way before this
 * existed.
 *
 * Backslash must be replaced first, or the backslashes introduced for the
 * quotes would themselves be escaped a second time.
 */
fun escapeQuoted(value: String): String = value.replace("\\", "\\\\").replace("\"", "\\\"")
