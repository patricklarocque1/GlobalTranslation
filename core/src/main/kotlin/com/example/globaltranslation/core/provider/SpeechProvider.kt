package com.example.globaltranslation.core.provider

import kotlinx.coroutines.flow.Flow

/**
 * Interface for speech recognition services.
 */
interface SpeechProvider {
    /**
     * Starts listening for speech input in the specified language.
     * @param languageCode Language code for recognition (e.g., "en-US")
     * @return Flow of speech recognition results
     */
    fun startListening(languageCode: String): Flow<SpeechResult>
    
    /**
     * Stops listening for speech input.
     */
    fun stopListening()
    
    /**
     * Cleans up resources.
     */
    fun cleanup()
}

/**
 * Sealed class representing speech recognition results.
 */
sealed class SpeechResult {
    data class PartialResult(val text: String) : SpeechResult()
    data class FinalResult(val text: String) : SpeechResult()
    data class Error(val message: String) : SpeechResult()
    data object ReadyForSpeech : SpeechResult()
    data object EndOfSpeech : SpeechResult()
}

