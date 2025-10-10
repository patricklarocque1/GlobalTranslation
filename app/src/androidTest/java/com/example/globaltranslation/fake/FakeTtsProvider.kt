package com.example.globaltranslation.fake

import com.example.globaltranslation.core.provider.TextToSpeechProvider
import com.example.globaltranslation.core.provider.TtsEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of TextToSpeechProvider for testing.
 * Simulates TTS without audio output.
 */
class FakeTtsProvider : TextToSpeechProvider {
    
    var shouldSucceed = true
    var speakingDuration = 100L // milliseconds
    
    override fun speak(text: String, languageCode: String): Flow<TtsEvent> = flow {
        emit(TtsEvent.Started)
        delay(speakingDuration)
        
        if (shouldSucceed) {
            emit(TtsEvent.Completed)
        } else {
            emit(TtsEvent.Error("TTS failed"))
        }
    }
    
    override fun stop() {
        // No-op for fake
    }
    
    override fun cleanup() {
        // No-op for fake
    }
}

