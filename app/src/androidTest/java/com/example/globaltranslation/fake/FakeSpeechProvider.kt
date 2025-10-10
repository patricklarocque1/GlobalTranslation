package com.example.globaltranslation.fake

import com.example.globaltranslation.core.provider.SpeechProvider
import com.example.globaltranslation.core.provider.SpeechResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of SpeechProvider for testing.
 * Simulates speech recognition without requiring microphone permissions.
 */
class FakeSpeechProvider : SpeechProvider {
    
    var shouldSucceed = true
    var recognizedText = "Test speech"
    var emitPartialResults = true
    
    override fun startListening(languageCode: String): Flow<SpeechResult> = flow {
        emit(SpeechResult.ReadyForSpeech)
        delay(50) // Simulate processing time
        
        if (emitPartialResults) {
            emit(SpeechResult.PartialResult("Test"))
            delay(50)
        }
        
        if (shouldSucceed) {
            emit(SpeechResult.FinalResult(recognizedText))
        } else {
            emit(SpeechResult.Error("Speech recognition failed"))
        }
        
        emit(SpeechResult.EndOfSpeech)
    }
    
    override fun stopListening() {
        // No-op for fake
    }
    
    override fun cleanup() {
        // No-op for fake
    }
}

