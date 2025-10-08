package com.example.gloabtranslation.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gloabtranslation.model.ConversationTurn
import com.example.gloabtranslation.services.SpeechRecognitionResult
import com.example.gloabtranslation.services.SpeechRecognitionService
import com.example.gloabtranslation.services.SpeechEvent
import com.example.gloabtranslation.services.TextToSpeechService
import com.example.gloabtranslation.services.TranslationService
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the conversation screen, managing live translation conversation state.
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val translationService: TranslationService,
    private val speechRecognitionService: SpeechRecognitionService,
    private val textToSpeechService: TextToSpeechService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    init {
        // Initialize TTS service
        viewModelScope.launch {
            textToSpeechService.initialize()
        }
    }

    /**
     * Starts listening for speech input in the specified language.
     */
    fun startListening(forLanguage: String) {
        if (_uiState.value.isListening) return

        _uiState.value = _uiState.value.copy(
            isListening = true,
            error = null
        )

        viewModelScope.launch {
            val locale = getLocaleFromLanguageCode(forLanguage)
            
            speechRecognitionService.startListening(locale).collect { result ->
                when (result) {
                    is SpeechRecognitionResult.ReadyForSpeech -> {
                        _uiState.value = _uiState.value.copy(isListeningReady = true)
                    }
                    
                    is SpeechRecognitionResult.BeginningOfSpeech -> {
                        _uiState.value = _uiState.value.copy(isDetectingSpeech = true)
                    }
                    
                    is SpeechRecognitionResult.PartialResults -> {
                        _uiState.value = _uiState.value.copy(partialSpeechText = result.text)
                    }
                    
                    is SpeechRecognitionResult.Results -> {
                        if (result.matches.isNotEmpty()) {
                            val recognizedText = result.matches.first().text
                            _uiState.value = _uiState.value.copy(
                                isListening = false,
                                isListeningReady = false,
                                isDetectingSpeech = false,
                                partialSpeechText = ""
                            )
                            translateAndAddToConversation(recognizedText, forLanguage)
                        }
                    }
                    
                    is SpeechRecognitionResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isListening = false,
                            isListeningReady = false,
                            isDetectingSpeech = false,
                            partialSpeechText = "",
                            error = result.message
                        )
                    }
                    
                    else -> { /* Handle other events if needed */ }
                }
            }
        }
    }

    /**
     * Stops the current speech recognition session.
     */
    fun stopListening() {
        speechRecognitionService.stopListening()
        _uiState.value = _uiState.value.copy(
            isListening = false,
            isListeningReady = false,
            isDetectingSpeech = false,
            partialSpeechText = ""
        )
    }

    /**
     * Translates text and adds it to the conversation history.
     */
    private fun translateAndAddToConversation(text: String, sourceLanguage: String) {
        val targetLanguage = getTargetLanguage(sourceLanguage)
        
        _uiState.value = _uiState.value.copy(isTranslating = true)

        viewModelScope.launch {
            try {
                val result = translationService.translate(text, sourceLanguage, targetLanguage)
                
                result.fold(
                    onSuccess = { translatedText ->
                        val turn = ConversationTurn(
                            originalText = text,
                            translatedText = translatedText,
                            sourceLang = sourceLanguage,
                            targetLang = targetLanguage
                        )
                        
                        val updatedConversation = _uiState.value.conversationHistory + turn
                        _uiState.value = _uiState.value.copy(
                            conversationHistory = updatedConversation,
                            isTranslating = false
                        )
                        
                        // Optionally speak the translation
                        if (_uiState.value.autoPlayTranslation) {
                            speakText(translatedText, targetLanguage)
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isTranslating = false,
                            error = "Translation failed: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTranslating = false,
                    error = "Translation error: ${e.message}"
                )
            }
        }
    }

    /**
     * Speaks the given text using TTS.
     */
    fun speakText(text: String, languageCode: String) {
        val locale = getLocaleFromLanguageCode(languageCode)
        
        viewModelScope.launch {
            textToSpeechService.speak(text, locale).collect { event ->
                when (event) {
                    is SpeechEvent.Started -> {
                        _uiState.value = _uiState.value.copy(isSpeaking = true)
                    }
                    is SpeechEvent.Completed -> {
                        _uiState.value = _uiState.value.copy(isSpeaking = false)
                    }
                    is SpeechEvent.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSpeaking = false,
                            error = "TTS error: ${event.message}"
                        )
                    }
                    else -> { /* Handle other events if needed */ }
                }
            }
        }
    }

    /**
     * Changes the source language for speech recognition.
     */
    fun setSourceLanguage(languageCode: String) {
        _uiState.value = _uiState.value.copy(sourceLanguage = languageCode)
    }

    /**
     * Changes the target language for translation.
     */
    fun setTargetLanguage(languageCode: String) {
        _uiState.value = _uiState.value.copy(targetLanguage = languageCode)
    }

    /**
     * Swaps source and target languages.
     */
    fun swapLanguages() {
        val currentSource = _uiState.value.sourceLanguage
        val currentTarget = _uiState.value.targetLanguage
        _uiState.value = _uiState.value.copy(
            sourceLanguage = currentTarget,
            targetLanguage = currentSource
        )
    }

    /**
     * Toggles auto-play of translations.
     */
    fun toggleAutoPlay() {
        _uiState.value = _uiState.value.copy(
            autoPlayTranslation = !_uiState.value.autoPlayTranslation
        )
    }

    /**
     * Clears the conversation history.
     */
    fun clearConversation() {
        _uiState.value = _uiState.value.copy(conversationHistory = emptyList())
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun getTargetLanguage(sourceLanguage: String): String {
        return if (sourceLanguage == _uiState.value.sourceLanguage) {
            _uiState.value.targetLanguage
        } else {
            _uiState.value.sourceLanguage
        }
    }

    private fun getLocaleFromLanguageCode(languageCode: String): Locale {
        return when (languageCode) {
            TranslateLanguage.ENGLISH -> Locale.ENGLISH
            TranslateLanguage.SPANISH -> Locale("es")
            TranslateLanguage.FRENCH -> Locale.FRENCH
            TranslateLanguage.GERMAN -> Locale.GERMAN
            TranslateLanguage.ITALIAN -> Locale.ITALIAN
            TranslateLanguage.PORTUGUESE -> Locale("pt")
            TranslateLanguage.CHINESE -> Locale.CHINESE
            TranslateLanguage.JAPANESE -> Locale.JAPANESE
            TranslateLanguage.KOREAN -> Locale.KOREAN
            TranslateLanguage.RUSSIAN -> Locale("ru")
            TranslateLanguage.ARABIC -> Locale("ar")
            TranslateLanguage.HINDI -> Locale("hi")
            else -> Locale.getDefault()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognitionService.cleanup()
        textToSpeechService.cleanup()
    }
}

/**
 * UI state for the conversation screen.
 */
data class ConversationUiState(
    val conversationHistory: List<ConversationTurn> = emptyList(),
    val sourceLanguage: String = TranslateLanguage.ENGLISH,
    val targetLanguage: String = TranslateLanguage.SPANISH,
    val isListening: Boolean = false,
    val isListeningReady: Boolean = false,
    val isDetectingSpeech: Boolean = false,
    val partialSpeechText: String = "",
    val isTranslating: Boolean = false,
    val isSpeaking: Boolean = false,
    val autoPlayTranslation: Boolean = true,
    val error: String? = null
)