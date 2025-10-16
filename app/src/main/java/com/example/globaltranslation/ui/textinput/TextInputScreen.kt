package com.example.globaltranslation.ui.textinput

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.globaltranslation.ui.components.MultiDevicePreview
import com.example.globaltranslation.ui.components.DesignVariantPreview
import com.example.globaltranslation.ui.components.PreviewScaffold
import com.example.globaltranslation.ui.components.UiCheckPreview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.globaltranslation.model.ConversationTurn
import com.example.globaltranslation.ui.components.LanguagePickerButton
import com.example.globaltranslation.ui.components.TwoLanguagePickerButton
import com.example.globaltranslation.ui.theme.GlobalTranslationTheme
import com.google.mlkit.nl.translate.TranslateLanguage
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Screen for manual text input and translation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputScreen(
    modifier: Modifier = Modifier,
    viewModel: TextInputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    
    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    TextInputScreenContent(
        uiState = uiState,
        onSourceLanguageChange = viewModel::setSourceLanguage,
        onTargetLanguageChange = viewModel::setTargetLanguage,
        onUpdateInputText = viewModel::updateInputText,
        onTranslate = {
            viewModel.translateText()
            keyboardController?.hide()
        },
        onClearInput = viewModel::clearInput,
        onSpeakText = viewModel::speakText,
        onCopyTranslation = { text -> copyToClipboard(context, text, "translation") },
        onClearHistory = viewModel::clearHistory,
        onCopyHistoryItemToInput = viewModel::copyToInput,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextInputScreenContent(
    uiState: TextInputUiState,
    onSourceLanguageChange: (String) -> Unit,
    onTargetLanguageChange: (String) -> Unit,
    onUpdateInputText: (String) -> Unit,
    onTranslate: () -> Unit,
    onClearInput: () -> Unit,
    onSpeakText: (String, String) -> Unit,
    onCopyTranslation: (String) -> Unit,
    onClearHistory: () -> Unit,
    onCopyHistoryItemToInput: (ConversationTurn) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss errors after a short delay
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            onClearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Language selection row
        LanguageSelectionCard(
            sourceLanguage = uiState.sourceLanguage,
            targetLanguage = uiState.targetLanguage,
            onSourceLanguageChange = onSourceLanguageChange,
            onTargetLanguageChange = onTargetLanguageChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Warning banner for invalid language pair
        if (!uiState.isValidLanguagePair) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "ML Kit requires English as source or target language",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Input section
        TextInputCard(
            inputText = uiState.inputText,
            onInputTextChange = onUpdateInputText,
            onTranslate = onTranslate,
            onClear = onClearInput,
            isTranslating = uiState.isTranslating,
            isValidLanguagePair = uiState.isValidLanguagePair,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Translation result
        uiState.currentTranslation?.let { translation ->
            TranslationResultCard(
                translation = translation,
                onSpeakOriginal = { onSpeakText(translation.originalText, translation.sourceLang) },
                onSpeakTranslation = { onSpeakText(translation.translatedText, translation.targetLang) },
                onCopyTranslation = { onCopyTranslation(translation.translatedText) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // History section
        if (uiState.translationHistory.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Translation History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                TextButton(onClick = onClearHistory) {
                    Text("Clear All")
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.translationHistory) { translation ->
                    TranslationHistoryItem(
                        translation = translation,
                        onCopyToInput = { onCopyHistoryItemToInput(translation) },
                        onCopyTranslation = { onCopyTranslation(translation.translatedText) }
                    )
                }
            }
        }

        // Error display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// ----- Compose Preview support -----

private class TextInputUiStatePreviewProvider : PreviewParameterProvider<TextInputUiState> {
    override val values: Sequence<TextInputUiState> = sequenceOf(
        // Empty input, default languages
        TextInputUiState(),
        // With a current translation and some history
        TextInputUiState(
            inputText = "Hello, how are you?",
            sourceLanguage = TranslateLanguage.ENGLISH,
            targetLanguage = TranslateLanguage.SPANISH,
            currentTranslation = ConversationTurn(
                originalText = "Hello, how are you?",
                translatedText = "Hola, ¿cómo estás?",
                sourceLang = TranslateLanguage.ENGLISH,
                targetLang = TranslateLanguage.SPANISH,
                timestamp = System.currentTimeMillis()
            ),
            translationHistory = listOf(
                ConversationTurn(
                    originalText = "Good morning",
                    translatedText = "Buenos días",
                    sourceLang = TranslateLanguage.ENGLISH,
                    targetLang = TranslateLanguage.SPANISH,
                    timestamp = System.currentTimeMillis() - 60_000
                ),
                ConversationTurn(
                    originalText = "Where is the station?",
                    translatedText = "¿Dónde está la estación?",
                    sourceLang = TranslateLanguage.ENGLISH,
                    targetLang = TranslateLanguage.SPANISH,
                    timestamp = System.currentTimeMillis() - 120_000
                )
            )
        ),
        // Invalid pair warning (no English on either side)
        TextInputUiState(
            sourceLanguage = TranslateLanguage.FRENCH,
            targetLanguage = TranslateLanguage.GERMAN,
            inputText = "Bonjour",
            error = null
        )
    )
}

@Preview(name = "Text Input - States", showBackground = true)
@PreviewScreenSizes
@MultiDevicePreview
@DesignVariantPreview
@Composable
fun TextInputScreenStatesPreview(
    @PreviewParameter(TextInputUiStatePreviewProvider::class) state: TextInputUiState = TextInputUiState()
) {
    PreviewScaffold {
        TextInputScreenContent(
                uiState = state,
                onSourceLanguageChange = {},
                onTargetLanguageChange = {},
                onUpdateInputText = {},
                onTranslate = {},
                onClearInput = {},
                onSpeakText = { _, _ -> },
                onCopyTranslation = {},
                onClearHistory = {},
                onCopyHistoryItemToInput = {},
                onClearError = {},
                modifier = Modifier.fillMaxSize()
        )
    }
}

// UI check preview with extreme font scale/RTL via UiCheckPreview
@UiCheckPreview
@Composable
fun TextInputScreenUiCheckPreview() {
    val longText = "This is a very long input line to stress wrapping and ellipsis behavior across different screen widths and font scales. " +
            "It should push the layout to ensure no clipping or overlapping occurs."
    val state = TextInputUiState(
        inputText = longText,
        sourceLanguage = TranslateLanguage.ENGLISH,
        targetLanguage = TranslateLanguage.SPANISH,
        currentTranslation = ConversationTurn(
            originalText = longText,
            translatedText = "Este es un texto de ejemplo muy largo para verificar el ajuste de línea y la accesibilidad.",
            sourceLang = TranslateLanguage.ENGLISH,
            targetLang = TranslateLanguage.SPANISH,
            timestamp = System.currentTimeMillis()
        ),
        translationHistory = List(3) { idx ->
            ConversationTurn(
                originalText = "History item ${'$'}idx — ${'$'}longText",
                translatedText = "Elemento de historial ${'$'}idx — traducido",
                sourceLang = TranslateLanguage.ENGLISH,
                targetLang = TranslateLanguage.SPANISH,
                timestamp = System.currentTimeMillis() - (idx + 1) * 60000L
            )
        }
    )
    PreviewScaffold {
        TextInputScreenContent(
            uiState = state,
            onSourceLanguageChange = {},
            onTargetLanguageChange = {},
            onUpdateInputText = {},
            onTranslate = {},
            onClearInput = {},
            onSpeakText = { _, _ -> },
            onCopyTranslation = {},
            onClearHistory = {},
            onCopyHistoryItemToInput = {},
            onClearError = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Live interactive preview to simulate typing, translating, clearing, and history interactions
@Preview(name = "Text Input - Live", showBackground = true)
@MultiDevicePreview
@DesignVariantPreview
@Composable
fun TextInputScreenLivePreview() {
    // Seed with a small history so the list renders
    val seedHistory = remember {
        listOf(
            ConversationTurn(
                originalText = "Good morning",
                translatedText = "Buenos días",
                sourceLang = TranslateLanguage.ENGLISH,
                targetLang = TranslateLanguage.SPANISH,
                timestamp = System.currentTimeMillis() - 120_000
            ),
            ConversationTurn(
                originalText = "How are you?",
                translatedText = "¿Cómo estás?",
                sourceLang = TranslateLanguage.ENGLISH,
                targetLang = TranslateLanguage.SPANISH,
                timestamp = System.currentTimeMillis() - 60_000
            )
        )
    }

    val state = remember {
        mutableStateOf(
            TextInputUiState(
                inputText = "",
                sourceLanguage = TranslateLanguage.ENGLISH,
                targetLanguage = TranslateLanguage.SPANISH,
                currentTranslation = null,
                translationHistory = seedHistory,
                isTranslating = false,
                error = null
            )
        )
    }

    fun simulateTranslate() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return
        // Fake translation result for preview purposes
        val translated = when (state.value.targetLanguage) {
            TranslateLanguage.SPANISH -> "${'$'}text → Hola"
            TranslateLanguage.FRENCH -> "${'$'}text → Salut"
            TranslateLanguage.GERMAN -> "${'$'}text → Hallo"
            else -> "${'$'}text (translated)"
        }
        val turn = ConversationTurn(
            originalText = text,
            translatedText = translated,
            sourceLang = state.value.sourceLanguage,
            targetLang = state.value.targetLanguage,
            timestamp = System.currentTimeMillis()
        )
        state.value = state.value.copy(
            currentTranslation = turn,
            translationHistory = listOf(turn) + state.value.translationHistory,
            inputText = "",
            isTranslating = false,
            error = null
        )
    }

    PreviewScaffold {
        TextInputScreenContent(
            uiState = state.value,
            onSourceLanguageChange = { code ->
                state.value = state.value.copy(sourceLanguage = code)
            },
            onTargetLanguageChange = { code ->
                state.value = state.value.copy(targetLanguage = code)
            },
            onUpdateInputText = { txt ->
                state.value = state.value.copy(inputText = txt)
            },
            onTranslate = { simulateTranslate() },
            onClearInput = {
                state.value = state.value.copy(inputText = "", currentTranslation = null)
            },
            onSpeakText = { _, _ -> /* no-op in preview */ },
            onCopyTranslation = { /* no-op in preview */ },
            onClearHistory = {
                state.value = state.value.copy(translationHistory = emptyList())
            },
            onCopyHistoryItemToInput = { turn ->
                state.value = state.value.copy(
                    inputText = turn.originalText,
                    sourceLanguage = turn.sourceLang,
                    targetLanguage = turn.targetLang
                )
            },
            onClearError = { state.value = state.value.copy(error = null) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun LanguageSelectionCard(
    sourceLanguage: String,
    targetLanguage: String,
    onSourceLanguageChange: (String) -> Unit,
    onTargetLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sameWarning by remember { mutableStateOf(false) }
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            TwoLanguagePickerButton(
                sourceLanguageCode = sourceLanguage,
                targetLanguageCode = targetLanguage,
                onLanguagesSelected = { from: String, to: String ->
                    sameWarning = from == to
                    if (!sameWarning) {
                        onSourceLanguageChange(from)
                        onTargetLanguageChange(to)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("textinput_languages")
            )
            }
            if (sameWarning) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "From and To can't be the same.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun TextInputCard(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onTranslate: () -> Unit,
    onClear: () -> Unit,
    isTranslating: Boolean,
    isValidLanguagePair: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                label = { Text("Enter text to translate") },
                placeholder = { Text("Type your message here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_text_field"),
                minLines = 3,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onTranslate() }
                ),
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier
                                .testTag("clear_btn")
                                .semantics { contentDescription = "Clear text" }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onTranslate,
                    enabled = inputText.isNotEmpty() && !isTranslating && isValidLanguagePair,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("translate_btn")
                ) {
                    if (isTranslating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translating...")
                    } else {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translate")
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationResultCard(
    translation: ConversationTurn,
    onSpeakOriginal: () -> Unit,
    onSpeakTranslation: () -> Unit,
    onCopyTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(translation.sourceLang),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = translation.originalText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                }
                IconButton(onClick = onSpeakOriginal) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak original text",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(translation.targetLang),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = translation.translatedText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                }
                Row {
                    IconButton(
                        onClick = onCopyTranslation,
                        modifier = Modifier
                            .testTag("copy_translation_btn")
                            .semantics { contentDescription = "Copy translation" }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        onClick = onSpeakTranslation,
                        modifier = Modifier
                            .testTag("speak_translation_btn")
                            .semantics { contentDescription = "Speak translation" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationHistoryItem(
    translation: ConversationTurn,
    onCopyToInput: () -> Unit,
    onCopyTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${getLanguageDisplayName(translation.sourceLang)} → ${getLanguageDisplayName(translation.targetLang)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    IconButton(
                        onClick = onCopyTranslation,
                        modifier = Modifier
                            .size(32.dp)
                            .semantics { contentDescription = "Copy translation to clipboard" }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onCopyToInput,
                        modifier = Modifier
                            .size(32.dp)
                            .semantics { contentDescription = "Copy to input" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Input,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Text(
                text = translation.originalText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = translation.translatedText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Helper function to convert language codes to display names
private fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        TranslateLanguage.ENGLISH -> "English"
        TranslateLanguage.SPANISH -> "Spanish"
        TranslateLanguage.FRENCH -> "French"
        TranslateLanguage.GERMAN -> "German"
        TranslateLanguage.ITALIAN -> "Italian"
        TranslateLanguage.PORTUGUESE -> "Portuguese"
        TranslateLanguage.CHINESE -> "Chinese"
        TranslateLanguage.JAPANESE -> "Japanese"
        TranslateLanguage.KOREAN -> "Korean"
        TranslateLanguage.RUSSIAN -> "Russian"
        TranslateLanguage.ARABIC -> "Arabic"
        TranslateLanguage.HINDI -> "Hindi"
        else -> languageCode.uppercase()
    }
}

/**
 * Copies text to the system clipboard using the modern ClipboardManager API.
 * This is the non-deprecated way to interact with the clipboard.
 */
private fun copyToClipboard(context: Context, text: String, label: String = "text") {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clip)
}

@PreviewScreenSizes
@Composable
private fun TextInputScreenPreview() {
    GlobalTranslationTheme {
        Surface {
            TextInputScreenContent(
                uiState = TextInputUiState(),
                onSourceLanguageChange = {},
                onTargetLanguageChange = {},
                onUpdateInputText = {},
                onTranslate = {},
                onClearInput = {},
                onSpeakText = { _, _ -> },
                onCopyTranslation = {},
                onClearHistory = {},
                onCopyHistoryItemToInput = {},
                onClearError = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
