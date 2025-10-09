package com.example.gloabtranslation.services

import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for optical character recognition (OCR) using ML Kit Text Recognition v2.
 * Detects and extracts text from images with high accuracy.
 */
@Singleton
class TextRecognitionService @Inject constructor() {
    
    private val recognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )
    
    /**
     * Recognizes text in the provided image.
     * 
     * @param image The input image to process
     * @return Result containing detected text blocks or error
     */
    suspend fun recognizeText(image: InputImage): Result<DetectedText> {
        return try {
            val result = recognizer.process(image).await()
            
            val textBlocks = result.textBlocks.map { block ->
                TextBlock(
                    text = block.text,
                    boundingBox = block.boundingBox ?: Rect(),
                    lines = block.lines.map { line ->
                        TextLine(
                            text = line.text,
                            boundingBox = line.boundingBox ?: Rect()
                        )
                    }
                )
            }
            
            Result.success(
                DetectedText(
                    fullText = result.text,
                    textBlocks = textBlocks
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clean up resources when service is no longer needed.
     */
    fun cleanup() {
        recognizer.close()
    }
}

/**
 * Represents all detected text in an image.
 */
data class DetectedText(
    val fullText: String,
    val textBlocks: List<TextBlock>
)

/**
 * Represents a block of detected text.
 */
data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val lines: List<TextLine>
)

/**
 * Represents a line of text within a text block.
 */
data class TextLine(
    val text: String,
    val boundingBox: Rect
)

