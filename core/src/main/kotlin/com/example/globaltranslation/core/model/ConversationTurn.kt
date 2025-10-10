package com.example.globaltranslation.core.model

/**
 * Represents a single turn in a conversation with original text, translation, and language info.
 * Pure Kotlin data model with no Android dependencies.
 */
data class ConversationTurn(
    val originalText: String,
    val translatedText: String,
    val sourceLang: String, // Language code (e.g., "en", "es")
    val targetLang: String,
    val timestamp: Long = System.currentTimeMillis()
)

