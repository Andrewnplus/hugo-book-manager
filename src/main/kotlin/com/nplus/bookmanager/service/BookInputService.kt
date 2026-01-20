package com.nplus.bookmanager.service

import com.nplus.bookmanager.model.BookInput
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Service for reading book input from YAML files
 */
class BookInputService {
    private val yaml = Yaml()

    /**
     * Load book input from a YAML file
     */
    fun loadBookInput(inputFile: File): BookInput? {
        if (!inputFile.exists()) {
            println("Error: Input file not found: ${inputFile.absolutePath}")
            return null
        }

        return try {
            val data: Map<String, Any> = inputFile.inputStream().use { yaml.load(it) }

            BookInput(
                chineseTitle = data["chinese_title"]?.toString() ?: "",
                englishTitle = data["english_title"]?.toString() ?: "",
                author = data["author"]?.toString() ?: "",
                publicationDate = data["publication_date"]?.toString() ?: "",
                coverUrl = data["cover_url"]?.toString() ?: "",
                purchaseUrl = data["purchase_url"]?.toString() ?: "",
                tableOfContents = data["table_of_contents"]?.toString() ?: "",
            )
        } catch (e: Exception) {
            println("Error reading YAML file: ${e.message}")
            null
        }
    }

    /**
     * Validate book input has all required fields
     */
    fun validate(input: BookInput): List<String> {
        val errors = mutableListOf<String>()

        if (input.chineseTitle.isBlank()) errors.add("chinese_title is required")
        if (input.englishTitle.isBlank()) errors.add("english_title is required")
        if (input.author.isBlank()) errors.add("author is required")
        if (input.publicationDate.isBlank()) errors.add("publication_date is required")
        if (input.coverUrl.isBlank()) errors.add("cover_url is required")
        if (input.purchaseUrl.isBlank()) errors.add("purchase_url is required")
        if (input.tableOfContents.isBlank()) errors.add("table_of_contents is required")

        return errors
    }
}
