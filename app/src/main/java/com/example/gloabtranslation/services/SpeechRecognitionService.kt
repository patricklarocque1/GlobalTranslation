package com.example.gloabtranslation.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for speech recognition using Android's built-in SpeechRecognizer.
 * Provides a flow-based API for listening to speech input.
 */
@Singleton
class SpeechRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    /**
     * Starts listening for speech input and returns a Flow of recognition events.
     * 
     * @param languageHint The preferred language locale (e.g., Locale.ENGLISH)
     * @param maxResults Maximum number of recognition results to return
     * @return Flow<SpeechRecognitionResult> with recognition events
     */
    fun startListening(
        languageHint: Locale = Locale.getDefault(),
        maxResults: Int = 5
    ): Flow<SpeechRecognitionResult> = callbackFlow {
        
        // Check if speech recognition is available
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechRecognitionResult.Error("Speech recognition not available"))
            close()
            return@callbackFlow
        }
        
        // Create speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(SpeechRecognitionResult.ReadyForSpeech)
            }
            
            override fun onBeginningOfSpeech() {
                trySend(SpeechRecognitionResult.BeginningOfSpeech)
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                trySend(SpeechRecognitionResult.VolumeChanged(rmsdB))
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // Not typically used
            }
            
            override fun onEndOfSpeech() {
                trySend(SpeechRecognitionResult.EndOfSpeech)
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }
                trySend(SpeechRecognitionResult.Error(errorMessage))
                close()
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidence = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                
                if (!matches.isNullOrEmpty()) {
                    val recognitionResults = matches.mapIndexed { index, text ->
                        SpeechRecognitionMatch(
                            text = text,
                            confidence = confidence?.getOrNull(index) ?: 0f
                        )
                    }
                    trySend(SpeechRecognitionResult.Results(recognitionResults))
                } else {
                    trySend(SpeechRecognitionResult.Error("No results found"))
                }
                close()
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(SpeechRecognitionResult.PartialResults(matches.first()))
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                // Not typically used
            }
        }
        
        speechRecognizer?.setRecognitionListener(recognitionListener)
        
        // Create recognition intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageHint.toString())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        // Start listening
        speechRecognizer?.startListening(intent)
        
        awaitClose {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
    
    /**
     * Stops the current speech recognition session.
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
    }
    
    /**
     * Cancels the current speech recognition session.
     */
    fun cancelListening() {
        speechRecognizer?.cancel()
    }
    
    /**
     * Checks if speech recognition is currently available on the device.
     */
    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    /**
     * Clean up resources.
     */
    fun cleanup() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

/**
 * Sealed class representing different speech recognition events.
 */
sealed class SpeechRecognitionResult {
    object ReadyForSpeech : SpeechRecognitionResult()
    object BeginningOfSpeech : SpeechRecognitionResult()
    object EndOfSpeech : SpeechRecognitionResult()
    data class VolumeChanged(val rmsdB: Float) : SpeechRecognitionResult()
    data class PartialResults(val text: String) : SpeechRecognitionResult()
    data class Results(val matches: List<SpeechRecognitionMatch>) : SpeechRecognitionResult()
    data class Error(val message: String) : SpeechRecognitionResult()
}

/**
 * Data class representing a single speech recognition match with confidence.
 */
data class SpeechRecognitionMatch(
    val text: String,
    val confidence: Float
)