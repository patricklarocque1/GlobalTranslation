package com.example.gloabtranslation.ui.languages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gloabtranslation.services.TranslationService
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing translation language models.
 */
@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val translationService: TranslationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        loadAvailableLanguages()
    }

    /**
     * Loads the list of available languages and checks their download status.
     */
    private fun loadAvailableLanguages() {
        val languages = getSupportedLanguages().map { lang ->
            LanguageModel(
                code = lang.code,
                name = lang.name,
                isDownloading = false,
                isDownloaded = false // Will be checked async
            )
        }
        
        _uiState.value = _uiState.value.copy(
            availableLanguages = languages,
            isLoading = true
        )
        
        // Check download status for each language pair with English
        checkDownloadStatus()
    }

    /**
     * Checks which language models are already downloaded.
     */
    private fun checkDownloadStatus() {
        viewModelScope.launch {
            val updatedLanguages = _uiState.value.availableLanguages.map { language ->
                if (language.code == TranslateLanguage.ENGLISH) {
                    // English is always available
                    language.copy(isDownloaded = true)
                } else {
                    try {
                        val isDownloaded = translationService.areModelsDownloaded(
                            TranslateLanguage.ENGLISH,
                            language.code
                        )
                        language.copy(isDownloaded = isDownloaded)
                    } catch (e: Exception) {
                        language.copy(isDownloaded = false)
                    }
                }
            }
            
            _uiState.value = _uiState.value.copy(
                availableLanguages = updatedLanguages,
                isLoading = false
            )
        }
    }

    /**
     * Downloads a language model.
     */
    fun downloadLanguage(languageCode: String) {
        if (languageCode == TranslateLanguage.ENGLISH) return // Already available
        
        val updatedLanguages = _uiState.value.availableLanguages.map { lang ->
            if (lang.code == languageCode) {
                lang.copy(isDownloading = true)
            } else lang
        }
        
        _uiState.value = _uiState.value.copy(availableLanguages = updatedLanguages)
        
        viewModelScope.launch {
            try {
                val result = translationService.downloadModels(
                    TranslateLanguage.ENGLISH,
                    languageCode
                )
                
                result.fold(
                    onSuccess = {
                        val finalUpdatedLanguages = _uiState.value.availableLanguages.map { lang ->
                            if (lang.code == languageCode) {
                                lang.copy(isDownloading = false, isDownloaded = true)
                            } else lang
                        }
                        _uiState.value = _uiState.value.copy(availableLanguages = finalUpdatedLanguages)
                    },
                    onFailure = { exception ->
                        val finalUpdatedLanguages = _uiState.value.availableLanguages.map { lang ->
                            if (lang.code == languageCode) {
                                lang.copy(isDownloading = false)
                            } else lang
                        }
                        _uiState.value = _uiState.value.copy(
                            availableLanguages = finalUpdatedLanguages,
                            error = "Failed to download ${getLanguageName(languageCode)}: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                val finalUpdatedLanguages = _uiState.value.availableLanguages.map { lang ->
                    if (lang.code == languageCode) {
                        lang.copy(isDownloading = false)
                    } else lang
                }
                _uiState.value = _uiState.value.copy(
                    availableLanguages = finalUpdatedLanguages,
                    error = "Download error: ${e.message}"
                )
            }
        }
    }

    /**
     * Refreshes the download status of all languages.
     */
    fun refreshLanguages() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        checkDownloadStatus()
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun getLanguageName(code: String): String {
        return getSupportedLanguages().find { it.code == code }?.name ?: code
    }
}

/**
 * UI state for the language management screen.
 */
data class LanguageUiState(
    val availableLanguages: List<LanguageModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Data class representing a language model.
 */
data class LanguageModel(
    val code: String,
    val name: String,
    val isDownloaded: Boolean,
    val isDownloading: Boolean
)

/**
 * Data class for supported languages.
 */
data class SupportedLanguage(
    val code: String,
    val name: String
)

/**
 * Returns list of supported languages for ML Kit translation.
 */
fun getSupportedLanguages(): List<SupportedLanguage> {
    return listOf(
        SupportedLanguage(TranslateLanguage.ENGLISH, "English"),
        SupportedLanguage(TranslateLanguage.SPANISH, "Spanish"),
        SupportedLanguage(TranslateLanguage.FRENCH, "French"),
        SupportedLanguage(TranslateLanguage.GERMAN, "German"),
        SupportedLanguage(TranslateLanguage.ITALIAN, "Italian"),
        SupportedLanguage(TranslateLanguage.PORTUGUESE, "Portuguese"),
        SupportedLanguage(TranslateLanguage.CHINESE, "Chinese"),
        SupportedLanguage(TranslateLanguage.JAPANESE, "Japanese"),
        SupportedLanguage(TranslateLanguage.KOREAN, "Korean"),
        SupportedLanguage(TranslateLanguage.RUSSIAN, "Russian"),
        SupportedLanguage(TranslateLanguage.ARABIC, "Arabic"),
        SupportedLanguage(TranslateLanguage.HINDI, "Hindi"),
        SupportedLanguage(TranslateLanguage.DUTCH, "Dutch"),
        SupportedLanguage(TranslateLanguage.POLISH, "Polish"),
        SupportedLanguage(TranslateLanguage.TURKISH, "Turkish"),
        SupportedLanguage(TranslateLanguage.THAI, "Thai"),
        SupportedLanguage(TranslateLanguage.VIETNAMESE, "Vietnamese"),
        SupportedLanguage(TranslateLanguage.INDONESIAN, "Indonesian"),
        SupportedLanguage(TranslateLanguage.MALAY, "Malay"),
        SupportedLanguage(TranslateLanguage.BENGALI, "Bengali")
    )
}