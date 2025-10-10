package com.example.globaltranslation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.globaltranslation.core.model.ConversationTurn

/**
 * Room entity for storing conversation turns.
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val timestamp: Long
)

/**
 * Converts entity to domain model.
 */
fun ConversationEntity.toDomainModel() = ConversationTurn(
    originalText = originalText,
    translatedText = translatedText,
    sourceLang = sourceLang,
    targetLang = targetLang,
    timestamp = timestamp
)

/**
 * Converts domain model to entity.
 */
fun ConversationTurn.toEntity() = ConversationEntity(
    originalText = originalText,
    translatedText = translatedText,
    sourceLang = sourceLang,
    targetLang = targetLang,
    timestamp = timestamp
)

