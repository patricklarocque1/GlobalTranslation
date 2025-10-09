package com.example.gloabtranslation.services

import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that combines text recognition and translation for camera-based translation.
 * Processes camera frames to detect and translate text in real-time.
 */
@Singleton
class CameraTranslationService @Inject constructor(
    private val textRecognitionService: TextRecognitionService,
    private val translationService: TranslationService
) {
    
    /**
     * Processes an image: recognizes text and translates it with smart grouping.
     * 
     * @param image The input image from camera
     * @param sourceLanguage Source language code
     * @param targetLanguage Target language code
     * @return Result containing translated text blocks
     */
    suspend fun processImage(
        image: InputImage,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<List<TranslatedTextBlock>> {
        return try {
            // Step 1: Recognize text in image (OCR)
            val detectedText = textRecognitionService.recognizeText(image)
                .getOrThrow()
            
            if (detectedText.textBlocks.isEmpty()) {
                return Result.success(emptyList())
            }
            
            // Step 2: Filter and group text blocks intelligently
            val processedBlocks = filterAndGroupTextBlocks(detectedText.textBlocks)
            
            if (processedBlocks.isEmpty()) {
                return Result.success(emptyList())
            }
            
            // Step 3: Translate grouped text blocks in parallel
            val translatedBlocks = coroutineScope {
                processedBlocks.map { textBlock ->
                    async {
                        val translationResult = translationService.translate(
                            text = textBlock.text,
                            fromLanguage = sourceLanguage,
                            toLanguage = targetLanguage
                        )
                        
                        if (translationResult.isFailure) {
                            TranslatedTextBlock(
                                originalText = textBlock.text,
                                translatedText = null,
                                boundingBox = textBlock.boundingBox
                            )
                        } else {
                            TranslatedTextBlock(
                                originalText = textBlock.text,
                                translatedText = translationResult.getOrNull(),
                                boundingBox = textBlock.boundingBox
                            )
                        }
                    }
                }.awaitAll()
            }
            
            Result.success(translatedBlocks)
        } catch (e: Exception) {
            Result.failure(Exception("Camera translation error: ${e.message}"))
        }
    }
    
    /**
     * Filters noise and groups nearby text blocks for better translation accuracy.
     * Uses advanced heuristics to filter out technical jargon, code, and gibberish.
     */
    private fun filterAndGroupTextBlocks(blocks: List<TextBlock>): List<TextBlock> {
        // Step 1: Advanced noise filtering
        val filteredBlocks = blocks.filter { block ->
            val text = block.text.trim()
            
            // Basic size checks
            if (text.length < 3) return@filter false
            if (block.boundingBox.width() < 30 || block.boundingBox.height() < 15) return@filter false
            
            // Must contain letters
            if (!text.any { it.isLetter() }) return@filter false
            
            // Advanced filters:
            // 1. Filter code-like patterns (CamelCase, snake_case, file paths)
            if (isCodeLike(text)) return@filter false
            
            // 2. Filter technical gibberish (too many consonants, no vowels)
            if (isGibberish(text)) return@filter false
            
            // 3. Filter excessive punctuation or symbols
            val symbolRatio = text.count { !it.isLetterOrDigit() && !it.isWhitespace() } / text.length.toFloat()
            if (symbolRatio > 0.3) return@filter false
            
            // 4. Filter all-caps technical terms (unless it's a short acronym or normal sentence)
            if (isAllCapsNoise(text)) return@filter false
            
            // 5. Calculate text quality score
            val qualityScore = calculateTextQuality(text)
            qualityScore > 0.5 // Only keep high-quality text
        }
        
        if (filteredBlocks.isEmpty()) {
            return emptyList()
        }
        
        // Step 2: Sort blocks by reading order (top to bottom, left to right)
        val sortedBlocks = filteredBlocks.sortedWith(
            compareBy<TextBlock> { it.boundingBox.top }
                .thenBy { it.boundingBox.left }
        )
        
        // Step 3: Group nearby blocks that likely belong together
        val groupedBlocks = mutableListOf<TextBlock>()
        var currentGroup = mutableListOf(sortedBlocks.first())
        var currentBounds = sortedBlocks.first().boundingBox
        
        for (i in 1 until sortedBlocks.size) {
            val block = sortedBlocks[i]
            val blockBounds = block.boundingBox
            
            // Calculate if this block is close to the current group
            val verticalDistance = blockBounds.top - currentBounds.bottom
            val horizontalOverlap = calculateHorizontalOverlap(currentBounds, blockBounds)
            
            // Group if:
            // - Vertically close (within 1.5x line height)
            // - Has horizontal overlap OR very close horizontally
            val averageHeight = (currentBounds.height() + blockBounds.height()) / 2
            val shouldGroup = verticalDistance < averageHeight * 1.5 && 
                             (horizontalOverlap > 0.3 || isHorizontallyClose(currentBounds, blockBounds))
            
            if (shouldGroup) {
                // Add to current group
                currentGroup.add(block)
                currentBounds = expandBounds(currentBounds, blockBounds)
            } else {
                // Start new group
                if (currentGroup.isNotEmpty()) {
                    groupedBlocks.add(mergeTextBlocks(currentGroup, currentBounds))
                }
                currentGroup = mutableListOf(block)
                currentBounds = blockBounds
            }
        }
        
        // Add last group
        if (currentGroup.isNotEmpty()) {
            groupedBlocks.add(mergeTextBlocks(currentGroup, currentBounds))
        }
        
        return groupedBlocks
    }
    
    /**
     * Calculates horizontal overlap between two rectangles (0.0 to 1.0).
     */
    private fun calculateHorizontalOverlap(rect1: Rect, rect2: Rect): Float {
        val overlapStart = maxOf(rect1.left, rect2.left)
        val overlapEnd = minOf(rect1.right, rect2.right)
        val overlap = maxOf(0, overlapEnd - overlapStart)
        val minWidth = minOf(rect1.width(), rect2.width())
        return if (minWidth > 0) overlap.toFloat() / minWidth else 0f
    }
    
    /**
     * Checks if two rectangles are horizontally close (within reasonable distance).
     */
    private fun isHorizontallyClose(rect1: Rect, rect2: Rect): Boolean {
        val horizontalGap = if (rect1.right < rect2.left) {
            rect2.left - rect1.right
        } else if (rect2.right < rect1.left) {
            rect1.left - rect2.right
        } else {
            0
        }
        val averageWidth = (rect1.width() + rect2.width()) / 2
        return horizontalGap < averageWidth * 0.5 // Within half the average width
    }
    
    /**
     * Expands a bounding box to include another.
     */
    private fun expandBounds(rect1: Rect, rect2: Rect): Rect {
        return Rect(
            minOf(rect1.left, rect2.left),
            minOf(rect1.top, rect2.top),
            maxOf(rect1.right, rect2.right),
            maxOf(rect1.bottom, rect2.bottom)
        )
    }
    
    /**
     * Merges multiple text blocks into one.
     */
    private fun mergeTextBlocks(blocks: List<TextBlock>, bounds: Rect): TextBlock {
        val combinedText = blocks.joinToString(" ") { it.text.trim() }
        val combinedLines = blocks.flatMap { it.lines }
        return TextBlock(
            text = combinedText,
            boundingBox = bounds,
            lines = combinedLines
        )
    }
    
    /**
     * Detects code-like patterns that shouldn't be translated.
     */
    private fun isCodeLike(text: String): Boolean {
        // Check for common code patterns
        val codePatterns = listOf(
            Regex("\\w+\\.\\w+"), // file.extension or class.method
            Regex("\\w+::\\w+"), // namespace::function
            Regex("[a-z]+[A-Z]"), // camelCase (but allow first word capitalized like "Hello")
            Regex("_[a-z]+_"), // _snake_case_
            Regex("\\w+\\.kt|\\w+\\.java|\\w+\\.xml"), // file extensions
            Regex("fun |class |val |var |import "), // code keywords
            Regex("\\{|\\}|\\[|\\]|<|>|;"), // code brackets
            Regex("@\\w+"), // annotations
            Regex("\\w+\\(\\)") // function calls
        )
        
        return codePatterns.any { it.containsMatchIn(text) }
    }
    
    /**
     * Detects gibberish text (random letters without proper vowel distribution).
     */
    private fun isGibberish(text: String): Boolean {
        val letters = text.filter { it.isLetter() }
        if (letters.length < 3) return true
        
        val vowels = letters.count { it.lowercaseChar() in "aeiou" }
        val vowelRatio = vowels.toFloat() / letters.length
        
        // Natural language typically has 30-50% vowels
        // Too few or too many suggests gibberish
        if (vowelRatio < 0.15 || vowelRatio > 0.75) return true
        
        // Check for excessive consonant clusters (more than 4 in a row)
        val hasExcessiveConsonants = Regex("[^aeiouAEIOU\\s]{5,}").containsMatchIn(text)
        if (hasExcessiveConsonants) return true
        
        return false
    }
    
    /**
     * Filters all-caps text that looks like technical noise.
     */
    private fun isAllCapsNoise(text: String): Boolean {
        val letters = text.filter { it.isLetter() }
        if (letters.isEmpty()) return true
        
        val upperRatio = letters.count { it.isUpperCase() } / letters.length.toFloat()
        
        // If mostly uppercase...
        if (upperRatio > 0.8) {
            // Allow short acronyms (2-4 letters)
            if (letters.length in 2..4) return false
            
            // Allow if it looks like a proper sentence (spaces between words)
            val words = text.split(Regex("\\s+"))
            if (words.size >= 3) return false // "HELLO WORLD TODAY" is probably ok
            
            // Otherwise it's likely technical noise
            return true
        }
        
        return false
    }
    
    /**
     * Calculates a quality score for detected text (0.0 to 1.0).
     * Higher score = more likely to be natural language text worth translating.
     */
    private fun calculateTextQuality(text: String): Float {
        var score = 0.5f // Start neutral
        
        // 1. Length score (longer text is usually more meaningful)
        when {
            text.length > 50 -> score += 0.2f
            text.length > 20 -> score += 0.1f
            text.length < 5 -> score -= 0.2f
        }
        
        // 2. Word count (multiple words better than single)
        val wordCount = text.split(Regex("\\s+")).size
        when {
            wordCount >= 4 -> score += 0.15f
            wordCount >= 2 -> score += 0.05f
            wordCount == 1 -> score -= 0.1f
        }
        
        // 3. Sentence structure (has proper punctuation)
        if (text.matches(Regex(".*[.!?]\\s*$"))) score += 0.1f
        if (text.contains(",")) score += 0.05f
        
        // 4. Natural capitalization (not all caps or all lowercase)
        val letters = text.filter { it.isLetter() }
        if (letters.isNotEmpty()) {
            val upperRatio = letters.count { it.isUpperCase() } / letters.length.toFloat()
            if (upperRatio in 0.05f..0.3f) score += 0.1f // Natural mix
        }
        
        // 5. Has spaces (not a single concatenated word)
        if (text.contains(" ")) score += 0.1f
        
        // 6. Proper vowel distribution
        val vowels = text.count { it.lowercaseChar() in "aeiou" }
        val vowelRatio = vowels / text.length.toFloat()
        if (vowelRatio in 0.25f..0.45f) score += 0.1f
        
        return score.coerceIn(0f, 1f)
    }
    
    /**
     * Checks if translation models are available for the given language pair.
     */
    suspend fun areModelsAvailable(
        sourceLanguage: String,
        targetLanguage: String
    ): Boolean {
        return translationService.areModelsDownloaded(sourceLanguage, targetLanguage)
    }
}

/**
 * Represents a detected text block with its translation.
 */
data class TranslatedTextBlock(
    val originalText: String,
    val translatedText: String?,
    val boundingBox: Rect
)

