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
}
