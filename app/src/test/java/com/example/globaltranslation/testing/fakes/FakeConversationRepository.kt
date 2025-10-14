package com.example.globaltranslation.testing.fakes

import com.example.globaltranslation.core.repository.ConversationRepository
import com.example.globaltranslation.core.model.ConversationTurn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeConversationRepository : ConversationRepository {
    private val _items = MutableStateFlow<List<ConversationTurn>>(emptyList())
    val saved: List<ConversationTurn> get() = _items.value

    override fun getConversations(): Flow<List<ConversationTurn>> = _items

    override suspend fun saveConversation(turn: ConversationTurn) {
        _items.value = listOf(turn) + _items.value
    }

    override suspend fun deleteConversation(timestamp: Long) {
        _items.value = _items.value.filterNot { it.timestamp == timestamp }
    }

    override suspend fun clearAll() {
        _items.value = emptyList()
    }
}
