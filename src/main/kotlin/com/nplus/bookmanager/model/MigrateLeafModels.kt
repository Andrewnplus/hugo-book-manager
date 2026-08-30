package com.nplus.bookmanager.model

import kotlinx.serialization.Serializable

@Serializable
data class MigrateLeafRequest(
    val taskType: String = "migrateLeafTaxonomy",
    val promptFile: String,
    val taxonomyFile: String,
    val repos: List<MigrateLeafRepoInput>,
)

@Serializable
data class MigrateLeafRepoInput(
    val name: String,
    val description: String,
    val currentTopics: List<String>,
)

@Serializable
data class MigrateLeafResponse(
    val results: List<MigrateLeafResult>,
)

@Serializable
data class MigrateLeafResult(
    val name: String,
    val topCategory: String,
    val subCategory: String,
    val leafCategory: String,
    val reason: String? = null,
)
