package com.example.gloabtranslation.ui.textinput

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gloabtranslation.ui.components.LanguagePickerButton
import com.example.gloabtranslation.ui.theme.GloabTranslationTheme
import com.google.mlkit.nl.translate.TranslateLanguage

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
    
    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
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
            onSourceLanguageChange = viewModel::setSourceLanguage,
            onTargetLanguageChange = viewModel::setTargetLanguage,
            onSwapLanguages = viewModel::swapLanguages,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input section
        TextInputCard(
            inputText = uiState.inputText,
            onInputTextChange = viewModel::updateInputText,
            onTranslate = {
                viewModel.translateText()
                keyboardController?.hide()
            },
            onClear = viewModel::clearInput,
            isTranslating = uiState.isTranslating,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Translation result
        uiState.currentTranslation?.let { translation ->
            TranslationResultCard(
                translation = translation,
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
                TextButton(onClick = viewModel::clearHistory) {
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
                        onCopyToInput = { viewModel.copyToInput(translation) }
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

@Composable
private fun LanguageSelectionCard(
    sourceLanguage: String,
    targetLanguage: String,
    onSourceLanguageChange: (String) -> Unit,
    onTargetLanguageChange: (String) -> Unit,
    onSwapLanguages: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source language
            LanguagePickerButton(
                selectedLanguageCode = sourceLanguage,
                onLanguageSelected = { language ->
                    onSourceLanguageChange(language)
                },
                modifier = Modifier.weight(1f)
            )
            
            // Swap button
            IconButton(onClick = onSwapLanguages) {
                Icon(
                    Icons.Default.SwapHoriz,
                    contentDescription = "Swap languages"
                )
            }
            
            // Target language
            LanguagePickerButton(
                selectedLanguageCode = targetLanguage,
                onLanguageSelected = { language ->
                    onTargetLanguageChange(language)
                },
                modifier = Modifier.weight(1f)
            )
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
                modifier = Modifier.fillMaxWidth(),
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
                        IconButton(onClick = onClear) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear text"
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
                    enabled = inputText.isNotEmpty() && !isTranslating,
                    modifier = Modifier.fillMaxWidth()
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
    translation: TextTranslation,
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
                        text = getLanguageDisplayName(translation.sourceLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = translation.originalText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
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
                        text = getLanguageDisplayName(translation.targetLanguage),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = translation.translatedText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = { /* TODO: Add TTS */ }) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak translation",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TranslationHistoryItem(
    translation: TextTranslation,
    onCopyToInput: () -> Unit,
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
                    text = "${getLanguageDisplayName(translation.sourceLanguage)} → ${getLanguageDisplayName(translation.targetLanguage)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onCopyToInput,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy to input",
                        modifier = Modifier.size(16.dp)
                    )
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

@PreviewScreenSizes
@Composable
private fun TextInputScreenPreview() {
    GloabTranslationTheme {
        Surface {
            // Preview would go here with mock data
        }
    }
}