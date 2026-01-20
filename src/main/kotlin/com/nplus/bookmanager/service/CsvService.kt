package com.nplus.bookmanager.service

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.nplus.bookmanager.model.Book
import java.io.File

/**
 * Service for reading book data from CSV files
 */
class CsvService {
    /**
     * Load books from a CSV file
     *
     * @param csvPath Path to the CSV file
     * @return List of Book objects
     */
    fun loadBooks(csvPath: String): List<Book> {
        val file = File(csvPath)
        if (!file.exists()) {
            throw IllegalArgumentException("CSV file not found: $csvPath")
        }

        return csvReader().readAllWithHeader(file).map { row ->
            Book.fromCsvRow(row)
        }
    }

    /**
     * Load books from a CSV file, returning a result with error handling
     */
    fun loadBooksResult(csvPath: String): Result<List<Book>> =
        try {
            Result.success(loadBooks(csvPath))
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Validate that a CSV file has the required columns
     */
    fun validateCsv(csvPath: String): List<String> {
        val errors = mutableListOf<String>()
        val file = File(csvPath)

        if (!file.exists()) {
            errors.add("CSV file not found: $csvPath")
            return errors
        }

        val requiredColumns =
            listOf(
                "chinese_title",
                "english_title",
                "repo_name",
                "description",
                "topics",
            )

        try {
            val headers = csvReader().readAllWithHeader(file).firstOrNull()?.keys ?: emptySet()

            for (column in requiredColumns) {
                if (column !in headers) {
                    errors.add("Missing required column: $column")
                }
            }
        } catch (e: Exception) {
            errors.add("Error reading CSV: ${e.message}")
        }

        return errors
    }
}
