package com.example.globaltranslation.core.provider

/**
 * Interface for translation services.
 * Abstracts the underlying translation implementation (ML Kit, cloud APIs, etc.)
 */
interface TranslationProvider {
    /**
     * Translates text from one language to another.
     * @param text Text to translate
     * @param from Source language code (e.g., "en")
     * @param to Target language code (e.g., "es")
     * @return Result with translated text or error
     */
    suspend fun translate(text: String, from: String, to: String): Result<String>
    
    /**
     * Checks if translation models are downloaded for the given language pair.
     * @param from Source language code
     * @param to Target language code
     * @return true if both models are downloaded
     */
    suspend fun areModelsDownloaded(from: String, to: String): Boolean
    
    /**
     * Downloads translation models for the given language pair.
     * @param from Source language code
     * @param to Target language code
     * @return Result indicating success or failure
     */
    suspend fun downloadModels(from: String, to: String): Result<Unit>
    
    /**
     * Deletes a downloaded translation model.
     * @param languageCode Language code to delete (e.g., "es")
     * @return Result indicating success or failure
     */
    suspend fun deleteModel(languageCode: String): Result<Unit>
    
    /**
     * Cleans up resources (translators, etc.)
     * Should be called when the provider is no longer needed.
     */
    fun cleanup()
}

