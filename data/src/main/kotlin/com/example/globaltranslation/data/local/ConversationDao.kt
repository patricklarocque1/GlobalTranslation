package com.example.globaltranslation.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for conversation history.
 */
@Dao
interface ConversationDao {
    
    /**
     * Gets all conversations ordered by timestamp (newest first).
     */
    @Query("SELECT * FROM conversations ORDER BY timestamp DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>
    
    /**
     * Inserts a new conversation turn.
     */
    @Insert
    suspend fun insertConversation(conversation: ConversationEntity)
    
    /**
     * Deletes a conversation by timestamp.
     */
    @Query("DELETE FROM conversations WHERE timestamp = :timestamp")
    suspend fun deleteConversation(timestamp: Long)
    
    /**
     * Clears all conversation history.
     */
    @Query("DELETE FROM conversations")
    suspend fun clearAll()
    
    /**
     * Gets conversations for a specific language pair.
     */
    @Query("SELECT * FROM conversations WHERE sourceLang = :sourceLang AND targetLang = :targetLang ORDER BY timestamp DESC")
    fun getConversationsForLanguagePair(sourceLang: String, targetLang: String): Flow<List<ConversationEntity>>
}

