package com.example.globaltranslation.core.provider

/**
 * Interface for combined camera OCR + translation services.
 */
interface CameraTranslationProvider {
    /**
     * Processes an image for text recognition and translation.
     * @param imageData Platform-specific image data
     * @param sourceLanguage Source language code
     * @param targetLanguage Target language code
     * @return Result with translated text blocks or error
     */
    suspend fun processImage(
        imageData: Any,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<List<TranslatedTextBlock>>
    
    /**
     * Checks if required models are downloaded for the language pair.
     * @param sourceLanguage Source language code
     * @param targetLanguage Target language code
     * @return true if models are available
     */
    suspend fun areModelsAvailable(sourceLanguage: String, targetLanguage: String): Boolean
}

/**
 * Represents a text block with original and translated text.
 */
data class TranslatedTextBlock(
    val originalText: String,
    val translatedText: String,
    val boundingBox: BoundingBox,
    val confidence: Float = 1.0f
)

