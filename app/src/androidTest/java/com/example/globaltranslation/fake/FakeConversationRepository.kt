package com.example.globaltranslation.fake

import com.example.globaltranslation.core.model.ConversationTurn
import com.example.globaltranslation.core.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of ConversationRepository for testing.
 * Stores conversations in memory instead of Room database.
 */
class FakeConversationRepository : ConversationRepository {
    
    private val conversations = mutableListOf<ConversationTurn>()
    private val _conversationsFlow = MutableStateFlow<List<ConversationTurn>>(emptyList())
    
    override fun getConversations(): Flow<List<ConversationTurn>> {
        return _conversationsFlow.asStateFlow()
    }
    
    override suspend fun saveConversation(turn: ConversationTurn) {
        conversations.add(turn)
        _conversationsFlow.value = conversations.toList()
    }
    
    override suspend fun deleteConversation(timestamp: Long) {
        conversations.removeIf { it.timestamp == timestamp }
        _conversationsFlow.value = conversations.toList()
    }
    
    override suspend fun clearAll() {
        conversations.clear()
        _conversationsFlow.value = emptyList()
    }
    
    // Test helper to get all saved conversations
    fun getSavedConversations(): List<ConversationTurn> = conversations.toList()
}

