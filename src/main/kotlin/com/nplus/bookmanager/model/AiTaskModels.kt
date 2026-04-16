package com.nplus.bookmanager.model

import kotlinx.serialization.Serializable

// ==================== Batch Request Models ====================

@Serializable
data class BatchMetadataRequest(
    val taskType: String = "batchGenerateMetadata",
    val promptFile: String,
    val books: List<BatchBookInput>,
)

@Serializable
data class BatchBookInput(
    val bookId: String,
    val chineseTitle: String,
    val englishTitle: String,
    val tableOfContents: String,
)

@Serializable
data class BatchMetadataResponse(
    val results: List<BatchMetadataResult>,
)

@Serializable
data class BatchMetadataResult(
    val bookId: String,
    val metadata: GeneratedMetadata,
    val structure: DocsStructure,
)
