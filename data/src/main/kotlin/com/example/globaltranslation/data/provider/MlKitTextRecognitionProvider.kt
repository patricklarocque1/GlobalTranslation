package com.example.globaltranslation.data.provider

import com.example.globaltranslation.core.provider.BoundingBox
import com.example.globaltranslation.core.provider.DetectedText
import com.example.globaltranslation.core.provider.TextBlock
import com.example.globaltranslation.core.provider.TextLine
import com.example.globaltranslation.core.provider.TextRecognitionProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ML Kit implementation of TextRecognitionProvider.
 * Uses ML Kit Text Recognition v2 for OCR.
 */
@Singleton
class MlKitTextRecognitionProvider @Inject constructor() : TextRecognitionProvider {
    
    private val recognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )
    
    override suspend fun recognizeText(imageData: Any): Result<DetectedText> {
        if (imageData !is InputImage) {
            return Result.failure(IllegalArgumentException("imageData must be InputImage"))
        }
        
        return try {
            val result = recognizer.process(imageData).await()
            
            val textBlocks = result.textBlocks.map { block ->
                TextBlock(
                    text = block.text,
                    boundingBox = block.boundingBox?.let {
                        BoundingBox(it.left, it.top, it.right, it.bottom)
                    } ?: BoundingBox(0, 0, 0, 0),
                    lines = block.lines.map { line ->
                        TextLine(
                            text = line.text,
                            boundingBox = line.boundingBox?.let {
                                BoundingBox(it.left, it.top, it.right, it.bottom)
                            } ?: BoundingBox(0, 0, 0, 0)
                        )
                    }
                )
            }
            
            Result.success(
                DetectedText(
                    text = result.text,
                    textBlocks = textBlocks
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun cleanup() {
        recognizer.close()
    }
}

