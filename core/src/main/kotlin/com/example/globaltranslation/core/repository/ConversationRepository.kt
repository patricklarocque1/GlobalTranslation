package com.example.globaltranslation.core.repository

import com.example.globaltranslation.core.model.ConversationTurn
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing conversation history.
 * Abstracts the persistence layer (Room, DataStore, cloud, etc.)
 */
interface ConversationRepository {
    /**
     * Gets all conversation turns ordered by timestamp (newest first).
     * @return Flow of conversation turns
     */
    fun getConversations(): Flow<List<ConversationTurn>>
    
    /**
     * Saves a conversation turn to the repository.
     * @param turn Conversation turn to save
     */
    suspend fun saveConversation(turn: ConversationTurn)
    
    /**
     * Deletes a specific conversation turn.
     * @param timestamp Timestamp identifying the conversation turn
     */
    suspend fun deleteConversation(timestamp: Long)
    
    /**
     * Clears all conversation history.
     */
    suspend fun clearAll()
}

