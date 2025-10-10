package com.example.globaltranslation.data.provider

import com.example.globaltranslation.core.provider.TranslationProvider
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ML Kit implementation of TranslationProvider.
 * Handles model downloading, translation operations, and resource management.
 */
@Singleton
class MlKitTranslationProvider @Inject constructor() : TranslationProvider {
    
    private val activeTranslators = mutableMapOf<String, Translator>()
    private val modelsReady = mutableSetOf<String>()
    
    override suspend fun translate(
        text: String,
        from: String,
        to: String
    ): Result<String> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Text cannot be empty"))
        }
        
        return try {
            val key = "$from-$to"
            val translator = getOrCreateTranslator(from, to)
            
            // Only download model if not already ready
            if (key !in modelsReady) {
                ensureModelDownloaded(translator)
                modelsReady.add(key)
            }
            
            val translatedText = translator.translate(text).await()
            Result.success(translatedText)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun areModelsDownloaded(from: String, to: String): Boolean {
        return try {
            val modelManager = RemoteModelManager.getInstance()
            
            val fromModel = TranslateRemoteModel.Builder(from).build()
            val toModel = TranslateRemoteModel.Builder(to).build()
            
            val fromDownloaded = modelManager.isModelDownloaded(fromModel).await()
            val toDownloaded = modelManager.isModelDownloaded(toModel).await()
            
            fromDownloaded && toDownloaded
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun downloadModels(from: String, to: String): Result<Unit> {
        return try {
            val translator = getOrCreateTranslator(from, to)
            ensureModelDownloaded(translator)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteModel(languageCode: String): Result<Unit> {
        return try {
            val modelManager = RemoteModelManager.getInstance()
            val model = TranslateRemoteModel.Builder(languageCode).build()
            
            modelManager.deleteDownloadedModel(model).await()
            
            // Remove translator from cache
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
    
    override fun cleanup() {
        activeTranslators.values.forEach { it.close() }
        activeTranslators.clear()
        modelsReady.clear()
    }
    
    private fun getOrCreateTranslator(from: String, to: String): Translator {
        val key = "$from-$to"
        return activeTranslators.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(from)
                .setTargetLanguage(to)
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
}

