package com.nplus.bookmanager.util

fun escapeQuoted(value: String): String = value.replace("\\", "\\\\").replace("\"", "\\\"")
