package com.example.gloabtranslation.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing service dependencies.
 * All services are singletons to maintain state across the app lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    
    // TranslationService is already @Singleton and @Inject constructor() 
    // so it will be automatically provided by Hilt
    
    // SpeechRecognitionService is already @Singleton and @Inject constructor() 
    // so it will be automatically provided by Hilt
    
    // TextToSpeechService is already @Singleton and @Inject constructor() 
    // so it will be automatically provided by Hilt
    
    // If we need additional configuration or manual providers, we can add them here
    // For example, if we need to provide specific configurations:
    
    /*
    @Provides
    @Singleton
    fun provideTranslationServiceConfig(): TranslationConfig {
        return TranslationConfig(
            downloadOnWifiOnly = true,
            maxCachedModels = 5
        )
    }
    */
}