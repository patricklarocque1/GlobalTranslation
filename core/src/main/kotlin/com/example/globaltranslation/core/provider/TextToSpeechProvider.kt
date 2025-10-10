package com.example.globaltranslation.core.provider

import kotlinx.coroutines.flow.Flow

/**
 * Interface for text-to-speech services.
 */
interface TextToSpeechProvider {
    /**
     * Speaks the given text in the specified language.
     * @param text Text to speak
     * @param languageCode Language code (e.g., "en", "es")
     * @return Flow of TTS events
     */
    fun speak(text: String, languageCode: String): Flow<TtsEvent>
    
    /**
     * Stops current speech playback.
     */
    fun stop()
    
    /**
     * Cleans up TTS resources.
     */
    fun cleanup()
}

/**
 * Sealed class representing TTS events.
 */
sealed class TtsEvent {
    data object Started : TtsEvent()
    data object Completed : TtsEvent()
    data class Error(val message: String) : TtsEvent()
}

