package com.nplus.bookmanager.model

/**
 * Data class representing book input from YAML file
 */
data class BookInput(
    val chineseTitle: String,
    val englishTitle: String,
    val author: String,
    val publicationDate: String,
    val coverUrl: String,
    val purchaseUrl: String,
    val tableOfContents: String,
)

/**
 * Data class representing AI-generated book metadata
 */
data class GeneratedMetadata(
    val repoName: String,
    val englishTitle: String,
    val chineseTitle: String,
    val description: String,
    val topics: List<String>,
    val category: String,
)

/**
 * Data class representing AI-generated docs structure
 */
data class DocsStructure(
    val sections: List<Section>,
) {
    data class Section(
        val folderName: String,
        val title: String,
        val weight: Int,
        val chapters: List<Chapter> = emptyList(),
    )

    data class Chapter(
        val folderName: String,
        val title: String,
        val weight: Int,
    )
}
