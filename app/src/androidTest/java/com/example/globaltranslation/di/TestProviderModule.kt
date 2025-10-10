package com.example.globaltranslation.di

import com.example.globaltranslation.core.provider.CameraTranslationProvider
import com.example.globaltranslation.core.provider.SpeechProvider
import com.example.globaltranslation.core.provider.TextToSpeechProvider
import com.example.globaltranslation.core.provider.TranslationProvider
import com.example.globaltranslation.core.repository.ConversationRepository
import com.example.globaltranslation.data.di.ProviderModule
import com.example.globaltranslation.fake.FakeCameraTranslationProvider
import com.example.globaltranslation.fake.FakeConversationRepository
import com.example.globaltranslation.fake.FakeSpeechProvider
import com.example.globaltranslation.fake.FakeTranslationProvider
import com.example.globaltranslation.fake.FakeTtsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Test module that replaces production providers with fakes for testing.
 * Uses @TestInstallIn to automatically replace ProviderModule in tests.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ProviderModule::class]
)
abstract class TestProviderModule {
    
    @Binds
    @Singleton
    abstract fun bindTranslationProvider(
        impl: FakeTranslationProvider
    ): TranslationProvider
    
    @Binds
    @Singleton
    abstract fun bindSpeechProvider(
        impl: FakeSpeechProvider
    ): SpeechProvider
    
    @Binds
    @Singleton
    abstract fun bindTextToSpeechProvider(
        impl: FakeTtsProvider
    ): TextToSpeechProvider
    
    @Binds
    @Singleton
    abstract fun bindCameraTranslationProvider(
        impl: FakeCameraTranslationProvider
    ): CameraTranslationProvider
    
    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        impl: FakeConversationRepository
    ): ConversationRepository
}

