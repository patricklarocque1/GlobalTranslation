package com.example.globaltranslation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for conversation history.
 */
@Database(
    entities = [ConversationEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ConversationDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}

