package com.example.globaltranslation.services

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Service for text-to-speech functionality using Android's built-in TTS engine.
 * Provides a flow-based API for speech synthesis events.
 */
@Singleton
class TextToSpeechService @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    
    /**
     * Initializes the TTS engine. Must be called before using other methods.
     */
    suspend fun initialize(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        textToSpeech = TextToSpeech(context) { status ->
            when (status) {
                TextToSpeech.SUCCESS -> {
                    isInitialized = true
                    continuation.resume(Result.success(Unit))
                }
                else -> {
                    isInitialized = false
                    continuation.resume(Result.failure(Exception("TTS initialization failed with status: $status")))
                }
            }
        }
    }
    
    /**
     * Speaks the given text in the specified language.
     * 
     * @param text The text to speak
     * @param language The language locale (e.g., Locale.ENGLISH)
     * @param queueMode TextToSpeech.QUEUE_FLUSH or TextToSpeech.QUEUE_ADD
     * @return Flow<SpeechEvent> with speech synthesis events
     */
    fun speak(
        text: String,
        language: Locale = Locale.getDefault(),
        queueMode: Int = TextToSpeech.QUEUE_FLUSH
    ): Flow<SpeechEvent> = callbackFlow {
        
        if (!isInitialized) {
            trySend(SpeechEvent.Error("TTS not initialized"))
            close()
            return@callbackFlow
        }
        
        val tts = textToSpeech ?: run {
            trySend(SpeechEvent.Error("TTS engine not available"))
            close()
            return@callbackFlow
        }
        
        // Set language
        val result = tts.setLanguage(language)
        when (result) {
            TextToSpeech.LANG_MISSING_DATA -> {
                trySend(SpeechEvent.Error("Language data missing for ${language.displayName}"))
                close()
                return@callbackFlow
            }
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                trySend(SpeechEvent.Error("Language not supported: ${language.displayName}"))
                close()
                return@callbackFlow
            }
        }
        
        // Create unique utterance ID
        val utteranceId = "utterance_${System.currentTimeMillis()}"
        
        // Set up progress listener
        val progressListener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                trySend(SpeechEvent.Started)
            }
            
            override fun onDone(utteranceId: String?) {
                trySend(SpeechEvent.Completed)
                close()
            }
            
            override fun onError(utteranceId: String?) {
                trySend(SpeechEvent.Error("TTS synthesis error"))
                close()
            }
            
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                trySend(SpeechEvent.Stopped(interrupted))
                close()
            }
            
            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                trySend(SpeechEvent.RangeStart(start, end))
            }
        }
        
        tts.setOnUtteranceProgressListener(progressListener)
        
        // Create speech parameters
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        
        // Start speaking
        val speakResult = tts.speak(text, queueMode, params, utteranceId)
        if (speakResult == TextToSpeech.ERROR) {
            trySend(SpeechEvent.Error("Failed to start TTS"))
            close()
        }
        
        awaitClose {
            // Cleanup is handled by the progress listener callbacks
        }
    }
    
    /**
     * Checks if a specific language is available for TTS.
     */
    fun isLanguageAvailable(language: Locale): LanguageAvailability {
        if (!isInitialized) return LanguageAvailability.NOT_SUPPORTED
        
        return when (textToSpeech?.isLanguageAvailable(language)) {
            TextToSpeech.LANG_AVAILABLE -> LanguageAvailability.AVAILABLE
            TextToSpeech.LANG_COUNTRY_AVAILABLE -> LanguageAvailability.COUNTRY_AVAILABLE
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> LanguageAvailability.COUNTRY_VAR_AVAILABLE
            TextToSpeech.LANG_MISSING_DATA -> LanguageAvailability.MISSING_DATA
            TextToSpeech.LANG_NOT_SUPPORTED -> LanguageAvailability.NOT_SUPPORTED
            else -> LanguageAvailability.NOT_SUPPORTED
        }
    }
    
    /**
     * Gets a list of available languages for TTS.
     */
    fun getAvailableLanguages(): Set<Locale> {
        return if (isInitialized) {
            textToSpeech?.availableLanguages ?: emptySet()
        } else {
            emptySet()
        }
    }
    
    /**
     * Stops the current speech synthesis.
     */
    fun stop() {
        textToSpeech?.stop()
    }
    
    /**
     * Checks if TTS is currently speaking.
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    /**
     * Sets the speech rate (0.1 to 3.0, where 1.0 is normal rate).
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }
    
    /**
     * Sets the pitch (0.1 to 2.0, where 1.0 is normal pitch).
     */
    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch)
    }
    
    /**
     * Clean up resources when service is no longer needed.
     */
    fun cleanup() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}

/**
 * Sealed class representing different TTS events.
 */
sealed class SpeechEvent {
    object Started : SpeechEvent()
    object Completed : SpeechEvent()
    data class Stopped(val interrupted: Boolean) : SpeechEvent()
    data class RangeStart(val start: Int, val end: Int) : SpeechEvent()
    data class Error(val message: String) : SpeechEvent()
}

/**
 * Enum representing language availability status for TTS.
 */
enum class LanguageAvailability {
    AVAILABLE,
    COUNTRY_AVAILABLE,
    COUNTRY_VAR_AVAILABLE,
    MISSING_DATA,
    NOT_SUPPORTED
}

