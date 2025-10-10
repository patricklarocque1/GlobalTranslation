package com.example.globaltranslation.fake

import com.example.globaltranslation.core.provider.TranslationProvider

/**
 * Fake implementation of TranslationProvider for testing.
 * Provides controllable behavior for UI tests.
 */
class FakeTranslationProvider : TranslationProvider {
    
    var shouldSucceed = true
    var translationResult = "Translated Text"
    var modelsDownloaded = true
    var downloadShouldSucceed = true
    var deleteShouldSucceed = true
    
    override suspend fun translate(text: String, from: String, to: String): Result<String> {
        return if (shouldSucceed) {
            Result.success("$translationResult: $text")
        } else {
            Result.failure(Exception("Translation failed"))
        }
    }
    
    override suspend fun areModelsDownloaded(from: String, to: String): Boolean {
        return modelsDownloaded
    }
    
    override suspend fun downloadModels(from: String, to: String): Result<Unit> {
        return if (downloadShouldSucceed) {
            modelsDownloaded = true
            Result.success(Unit)
        } else {
            Result.failure(Exception("Download failed"))
        }
    }
    
    override suspend fun deleteModel(languageCode: String): Result<Unit> {
        return if (deleteShouldSucceed) {
            modelsDownloaded = false
            Result.success(Unit)
        } else {
            Result.failure(Exception("Delete failed"))
        }
    }
    
    override fun cleanup() {
        // No-op for fake
    }
}

