package com.example.globaltranslation.services

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for translating text using ML Kit Translation API.
 * Handles model downloading, translation operations, and proper resource management.
 */
@Singleton
class TranslationService @Inject constructor() {
    
    private val activeTranslators = mutableMapOf<String, Translator>()
    
    // Track which translators have already downloaded models
    private val modelsReady = mutableSetOf<String>()
    
    /**
     * Translates text from source language to target language.
     * 
     * @param text The text to translate
     * @param fromLanguage Source language code (e.g., TranslateLanguage.ENGLISH)
     * @param toLanguage Target language code (e.g., TranslateLanguage.SPANISH)
     * @return Result containing translated text or error
     */
    suspend fun translate(
        text: String,
        fromLanguage: String,
        toLanguage: String
    ): Result<String> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Text cannot be empty"))
        }
        
        return try {
            val key = "$fromLanguage-$toLanguage"
            val translator = getOrCreateTranslator(fromLanguage, toLanguage)
            
            // Only download model if not already ready
            if (key !in modelsReady) {
                ensureModelDownloaded(translator)
                modelsReady.add(key)
            }
            
            // Perform translation
            val translatedText = translator.translate(text).await()
            Result.success(translatedText)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Checks if translation models are downloaded for the given language pair.
     * NOTE: This properly checks model download status using the RemoteModelManager.
     */
    suspend fun areModelsDownloaded(
        fromLanguage: String,
        toLanguage: String
    ): Boolean {
        return try {
            val modelManager = com.google.mlkit.common.model.RemoteModelManager.getInstance()
            
            val fromModel = com.google.mlkit.nl.translate.TranslateRemoteModel.Builder(fromLanguage).build()
            val toModel = com.google.mlkit.nl.translate.TranslateRemoteModel.Builder(toLanguage).build()
            
            val fromDownloaded = modelManager.isModelDownloaded(fromModel).await()
            val toDownloaded = modelManager.isModelDownloaded(toModel).await()
            
            fromDownloaded && toDownloaded
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Downloads translation models for the specified language pair.
     */
    suspend fun downloadModels(
        fromLanguage: String,
        toLanguage: String
    ): Result<Unit> {
        return try {
            val translator = getOrCreateTranslator(fromLanguage, toLanguage)
            ensureModelDownloaded(translator)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Deletes a downloaded translation model from device storage.
     * This removes both the language pair models (to/from English).
     */
    suspend fun deleteModel(languageCode: String): Result<Unit> {
        return try {
            val modelManager = com.google.mlkit.common.model.RemoteModelManager.getInstance()
            val model = com.google.mlkit.nl.translate.TranslateRemoteModel.Builder(languageCode).build()
            
            modelManager.deleteDownloadedModel(model).await()
            
            // Remove translator from cache and ready set if it exists
            activeTranslators.entries.removeIf { (key, translator) ->
                if (key.contains(languageCode)) {
                    translator.close()
                    modelsReady.remove(key)
                    true
                } else false
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getOrCreateTranslator(
        fromLanguage: String,
        toLanguage: String
    ): Translator {
        val key = "$fromLanguage-$toLanguage"
        
        return activeTranslators.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguage)
                .setTargetLanguage(toLanguage)
                .build()
            Translation.getClient(options)
        }
    }
    
    private suspend fun ensureModelDownloaded(translator: Translator) {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        
        translator.downloadModelIfNeeded(conditions).await()
    }
    
    /**
     * Clean up resources when service is no longer needed.
     * Should be called from Application's onTerminate or similar lifecycle method.
     */
    fun cleanup() {
        activeTranslators.values.forEach { translator ->
            translator.close()
        }
        activeTranslators.clear()
        modelsReady.clear()
    }
}
