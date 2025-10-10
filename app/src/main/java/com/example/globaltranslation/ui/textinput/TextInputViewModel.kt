package com.example.globaltranslation.ui.textinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globaltranslation.model.ConversationTurn
import com.example.globaltranslation.services.TextToSpeechService
import com.example.globaltranslation.services.TranslationService
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the text input screen, managing manual text translation.
 */
@HiltViewModel
class TextInputViewModel @Inject constructor(
    private val translationService: TranslationService,
    private val ttsService: TextToSpeechService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextInputUiState())
    val uiState: StateFlow<TextInputUiState> = _uiState.asStateFlow()
    
    init {
        // Initialize TTS service
        viewModelScope.launch {
            ttsService.initialize()
        }
    }

    /**
     * Updates the input text as user types.
     */
    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    /**
     * Translates the current input text.
     */
    fun translateText() {
        val currentState = _uiState.value
        val textToTranslate = currentState.inputText.trim()
        
        if (textToTranslate.isEmpty()) {
            _uiState.value = currentState.copy(error = "Please enter text to translate")
            return
        }

        _uiState.value = currentState.copy(
            isTranslating = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val result = translationService.translate(
                    text = textToTranslate,
                    fromLanguage = currentState.sourceLanguage,
                    toLanguage = currentState.targetLanguage
                )

                result.fold(
                    onSuccess = { translatedText ->
                        val translation = TextTranslation(
                            originalText = textToTranslate,
                            translatedText = translatedText,
                            sourceLanguage = currentState.sourceLanguage,
                            targetLanguage = currentState.targetLanguage,
                            timestamp = System.currentTimeMillis()
                        )
                        
                        val updatedHistory = listOf(translation) + currentState.translationHistory
                        
                        _uiState.value = currentState.copy(
                            currentTranslation = translation,
                            translationHistory = updatedHistory,
                            isTranslating = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = currentState.copy(
                            isTranslating = false,
                            error = "Translation failed: ${exception.message}. Ensure models are downloaded and you're on WiFi for first-time downloads."
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isTranslating = false,
                    error = "Translation error: ${e.message}"
                )
            }
        }
    }

    /**
     * Sets the source language for translation.
     */
    fun setSourceLanguage(languageCode: String) {
        _uiState.value = _uiState.value.copy(sourceLanguage = languageCode)
    }

    /**
     * Sets the target language for translation.
     */
    fun setTargetLanguage(languageCode: String) {
        _uiState.value = _uiState.value.copy(targetLanguage = languageCode)
    }

    /**
     * Swaps source and target languages.
     */
    fun swapLanguages() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            sourceLanguage = currentState.targetLanguage,
            targetLanguage = currentState.sourceLanguage,
            inputText = currentState.currentTranslation?.translatedText ?: currentState.inputText
        )
    }

    /**
     * Clears the current input text.
     */
    fun clearInput() {
        _uiState.value = _uiState.value.copy(
            inputText = "",
            currentTranslation = null
        )
    }

    /**
     * Clears the translation history.
     */
    fun clearHistory() {
        _uiState.value = _uiState.value.copy(translationHistory = emptyList())
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Copies text from a previous translation to input.
     */
    fun copyToInput(translation: TextTranslation) {
        _uiState.value = _uiState.value.copy(
            inputText = translation.originalText,
            sourceLanguage = translation.sourceLanguage,
            targetLanguage = translation.targetLanguage
        )
    }
    
    /**
     * Speaks the given text using text-to-speech.
     */
    fun speakText(text: String, languageCode: String) {
        val locale = getLocaleFromLanguageCode(languageCode)
        
        viewModelScope.launch {
            ttsService.speak(text, locale).collect { event ->
                // Handle TTS events if needed (e.g., show speaking indicator)
            }
        }
    }
    
    /**
     * Converts ML Kit language code to Locale.
     */
    private fun getLocaleFromLanguageCode(languageCode: String): Locale {
        return when (languageCode) {
            TranslateLanguage.ENGLISH -> Locale.forLanguageTag("en")
            TranslateLanguage.SPANISH -> Locale.forLanguageTag("es")
            TranslateLanguage.FRENCH -> Locale.forLanguageTag("fr")
            TranslateLanguage.GERMAN -> Locale.forLanguageTag("de")
            TranslateLanguage.ITALIAN -> Locale.forLanguageTag("it")
            TranslateLanguage.PORTUGUESE -> Locale.forLanguageTag("pt")
            TranslateLanguage.CHINESE -> Locale.forLanguageTag("zh")
            TranslateLanguage.JAPANESE -> Locale.forLanguageTag("ja")
            TranslateLanguage.KOREAN -> Locale.forLanguageTag("ko")
            TranslateLanguage.RUSSIAN -> Locale.forLanguageTag("ru")
            TranslateLanguage.ARABIC -> Locale.forLanguageTag("ar")
            TranslateLanguage.HINDI -> Locale.forLanguageTag("hi")
            TranslateLanguage.DUTCH -> Locale.forLanguageTag("nl")
            TranslateLanguage.POLISH -> Locale.forLanguageTag("pl")
            TranslateLanguage.TURKISH -> Locale.forLanguageTag("tr")
            TranslateLanguage.SWEDISH -> Locale.forLanguageTag("sv")
            TranslateLanguage.NORWEGIAN -> Locale.forLanguageTag("no")
            TranslateLanguage.DANISH -> Locale.forLanguageTag("da")
            TranslateLanguage.FINNISH -> Locale.forLanguageTag("fi")
            TranslateLanguage.GREEK -> Locale.forLanguageTag("el")
            else -> Locale.forLanguageTag(languageCode)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsService.cleanup()
    }
}

/**
 * UI state for the text input screen.
 */
data class TextInputUiState(
    val inputText: String = "",
    val sourceLanguage: String = TranslateLanguage.ENGLISH,
    val targetLanguage: String = TranslateLanguage.SPANISH,
    val currentTranslation: TextTranslation? = null,
    val translationHistory: List<TextTranslation> = emptyList(),
    val isTranslating: Boolean = false,
    val error: String? = null
)

/**
 * Data class representing a text translation.
 */
data class TextTranslation(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val timestamp: Long
)
