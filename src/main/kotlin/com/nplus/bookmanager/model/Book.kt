package com.nplus.bookmanager.model

/**
 * Book data model representing a row from the CSV file
 */
data class Book(
    val chineseTitle: String,
    val englishTitle: String,
    val author: String,
    val repoName: String,
    val description: String,
    val topics: List<String>,
    val category: String,
    val categoryTopic: String,
    val kindleUrl: String = "",
    val booksTwUrl: String = "",
    val publicationDate: String = "",
    val notes: String = "",
) {
    companion object {
        /**
         * Create a Book from a CSV row map
         */
        fun fromCsvRow(row: Map<String, String>): Book =
            Book(
                chineseTitle = row["chinese_title"] ?: "",
                englishTitle = row["english_title"] ?: "",
                author = row["author"] ?: "",
                repoName = row["repo_name"] ?: "",
                description = row["description"] ?: "",
                topics = (row["topics"] ?: "").split(" ").filter { it.isNotBlank() },
                category = row["category"] ?: "",
                categoryTopic = row["category_topic"] ?: "",
                kindleUrl = row["kindle_url"] ?: "",
                booksTwUrl = row["books_tw_url"] ?: "",
                publicationDate = row["publication_date"] ?: "",
                notes = row["notes"] ?: "",
            )
    }
}
