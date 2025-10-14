package com.example.globaltranslation.testing.fakes

import com.example.globaltranslation.core.provider.TranslationProvider
import kotlinx.coroutines.delay

class FakeTranslationProvider : TranslationProvider {
    var shouldSucceed: Boolean = true
    var translatedPrefix: String = "Translated"
    var delayMs: Long = 0

    override suspend fun translate(text: String, from: String, to: String): Result<String> {
        if (delayMs > 0) delay(delayMs)
        return if (shouldSucceed) Result.success("$translatedPrefix: $text")
        else Result.failure(IllegalStateException("Translation failed"))
    }

    override suspend fun areModelsDownloaded(from: String, to: String): Boolean = true
    override suspend fun downloadModels(from: String, to: String, requireWifi: Boolean): Result<Unit> = Result.success(Unit)
    override suspend fun deleteModel(languageCode: String): Result<Unit> = Result.success(Unit)
    override fun cleanup() {}
}
