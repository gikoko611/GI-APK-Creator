package com.gi.apkcreator.model

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val prompt: String,
    val template: String = "CUSTOM",
    val packageName: String = "com.gi.generated",
    val versionName: String = "0.1.0",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
