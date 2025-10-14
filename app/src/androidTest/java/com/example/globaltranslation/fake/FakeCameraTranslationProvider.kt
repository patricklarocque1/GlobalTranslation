package com.example.globaltranslation.fake

import com.example.globaltranslation.core.provider.BoundingBox
import com.example.globaltranslation.core.provider.CameraTranslationProvider
import com.example.globaltranslation.core.provider.TranslatedTextBlock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of CameraTranslationProvider for testing.
 * Simulates camera OCR and translation without camera hardware.
 */
@Singleton
class FakeCameraTranslationProvider @Inject constructor() : CameraTranslationProvider {
    
    var shouldSucceed = true
    var detectedBlocks = listOf(
        TranslatedTextBlock(
            originalText = "Hello",
            translatedText = "Hola",
            boundingBox = BoundingBox(10, 10, 100, 50),
            confidence = 0.95f
        )
    )
    var modelsAvailable = true
    
    override suspend fun processImage(
        imageData: Any,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<List<TranslatedTextBlock>> {
        return if (shouldSucceed) {
            Result.success(detectedBlocks)
        } else {
            Result.failure(Exception("Image processing failed"))
        }
    }
    
    override suspend fun areModelsAvailable(sourceLanguage: String, targetLanguage: String): Boolean {
        return modelsAvailable
    }
}

