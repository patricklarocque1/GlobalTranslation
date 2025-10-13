package com.example.globaltranslation.core.provider

/**
 * Interface for text recognition (OCR) services.
 * Platform-agnostic abstraction for ML Kit, Tesseract, or cloud OCR.
 */
interface TextRecognitionProvider {
    /**
     * Recognizes text from image data.
     * @param imageData Platform-specific image data (to be wrapped)
     * @param languageCode Language code to select appropriate script recognizer (e.g., "zh", "ja", "ko", "hi")
     * @return Result with detected text structure or error
     */
    suspend fun recognizeText(imageData: Any, languageCode: String? = null): Result<DetectedText>
    
    /**
     * Cleans up OCR resources.
     */
    fun cleanup()
}

/**
 * Represents detected text with hierarchical structure.
 */
data class DetectedText(
    val text: String,
    val textBlocks: List<TextBlock>
)

/**
 * Represents a block of detected text with position.
 */
data class TextBlock(
    val text: String,
    val boundingBox: BoundingBox,
    val lines: List<TextLine> = emptyList()
)

/**
 * Represents a line of text within a block.
 */
data class TextLine(
    val text: String,
    val boundingBox: BoundingBox
)

/**
 * Platform-agnostic bounding box representation.
 */
data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
}

