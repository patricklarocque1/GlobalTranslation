package com.example.globaltranslation.data.di

import android.content.Context
import androidx.room.Room
import com.example.globaltranslation.data.local.ConversationDao
import com.example.globaltranslation.data.local.ConversationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data layer dependencies (Room database, DAOs).
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideConversationDatabase(
        @ApplicationContext context: Context
    ): ConversationDatabase {
        return Room.databaseBuilder(
            context,
            ConversationDatabase::class.java,
            "conversation_database"
        )
        // Use destructive migration only on downgrade (safer for production)
        .fallbackToDestructiveMigrationOnDowngrade()
        // Add migrations here as schema evolves
        // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideConversationDao(
        database: ConversationDatabase
    ): ConversationDao {
        return database.conversationDao()
    }
}

