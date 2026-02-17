package com.nplus.bookmanager.model

import kotlinx.serialization.Serializable

// ==================== Request Models ====================

@Serializable
data class MetadataRequest(
    val taskType: String = "generateMetadata",
    val promptFile: String,
    val input: MetadataInput,
)

@Serializable
data class MetadataInput(
    val chineseTitle: String,
    val englishTitle: String,
)

@Serializable
data class StructureRequest(
    val taskType: String = "generateDocsStructure",
    val promptFile: String,
    val input: StructureInput,
)

@Serializable
data class StructureInput(
    val tableOfContents: String,
)

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
