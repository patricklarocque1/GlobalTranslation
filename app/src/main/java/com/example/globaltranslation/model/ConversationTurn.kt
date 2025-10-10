package com.example.globaltranslation.model

import com.google.mlkit.nl.translate.TranslateLanguage

/**
 * Represents a single turn in a conversation with original text, translation, and language info
 */
data class ConversationTurn(
    val originalText: String,
    val translatedText: String,
    val sourceLang: String, // Using TranslateLanguage constant (e.g., "en", "es")
    val targetLang: String,
    val timestamp: Long = System.currentTimeMillis()
)
