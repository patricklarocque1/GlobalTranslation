package com.example.globaltranslation.data.di

import com.example.globaltranslation.core.provider.CameraTranslationProvider
import com.example.globaltranslation.core.provider.SpeechProvider
import com.example.globaltranslation.core.provider.TextRecognitionProvider
import com.example.globaltranslation.core.provider.TextToSpeechProvider
import com.example.globaltranslation.core.provider.TranslationProvider
import com.example.globaltranslation.core.repository.ConversationRepository
import com.example.globaltranslation.data.provider.AndroidSpeechProvider
import com.example.globaltranslation.data.provider.AndroidTextToSpeechProvider
import com.example.globaltranslation.data.provider.MlKitCameraTranslationProvider
import com.example.globaltranslation.data.provider.MlKitTextRecognitionProvider
import com.example.globaltranslation.data.provider.MlKitTranslationProvider
import com.example.globaltranslation.data.repository.RoomConversationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds provider interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
    
    @Binds
    @Singleton
    abstract fun bindTranslationProvider(
        impl: MlKitTranslationProvider
    ): TranslationProvider
    
    @Binds
    @Singleton
    abstract fun bindTextRecognitionProvider(
        impl: MlKitTextRecognitionProvider
    ): TextRecognitionProvider
    
    @Binds
    @Singleton
    abstract fun bindSpeechProvider(
        impl: AndroidSpeechProvider
    ): SpeechProvider
    
    @Binds
    @Singleton
    abstract fun bindTextToSpeechProvider(
        impl: AndroidTextToSpeechProvider
    ): TextToSpeechProvider
    
    @Binds
    @Singleton
    abstract fun bindCameraTranslationProvider(
        impl: MlKitCameraTranslationProvider
    ): CameraTranslationProvider
    
    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        impl: RoomConversationRepository
    ): ConversationRepository
}

