package com.example.globaltranslation.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.globaltranslation.R
import com.example.globaltranslation.model.ConversationTurn
import com.example.globaltranslation.ui.components.LanguagePickerButton
import com.example.globaltranslation.ui.theme.GlobalTranslationTheme
import com.google.mlkit.nl.translate.TranslateLanguage

/**
 * Main conversation screen for live translation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Permission handling
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            // Permission denied, show error
            viewModel.clearError() // Clear any existing error first
        }
    }
    
    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // In a real app, you'd show a SnackbarHostState here
            // For now, we'll auto-clear after showing
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Saved history toggle button
        if (uiState.savedHistory.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleSavedHistory() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saved History (${uiState.savedHistory.size})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Icon(
                        if (uiState.showSavedHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (uiState.showSavedHistory) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        // Expandable saved history
        if (uiState.showSavedHistory && uiState.savedHistory.isNotEmpty()) {
            SavedHistorySection(
                savedHistory = uiState.savedHistory,
                onSpeakText = viewModel::speakText,
                onDelete = viewModel::deleteSavedConversation,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp) // Max height instead of weight for better display
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Warning banner for invalid language pair
        if (!uiState.isValidLanguagePair) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
        }
        
        // Language selection and controls
        LanguageSelectionRow(
            sourceLanguage = uiState.sourceLanguage,
            targetLanguage = uiState.targetLanguage,
            onSourceLanguageChange = viewModel::setSourceLanguage,
            onTargetLanguageChange = viewModel::setTargetLanguage,
            onSwapLanguages = viewModel::swapLanguages,
            autoPlayEnabled = uiState.autoPlayTranslation,
            onAutoPlayToggle = viewModel::toggleAutoPlay,
            modifier = Modifier.padding(16.dp)
        )
        
        // Conversation history
        ConversationHistory(
            conversationHistory = uiState.conversationHistory,
            onSpeakText = viewModel::speakText,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        
        // Speech input area
        SpeechInputArea(
            isListening = uiState.isListening,
            isListeningReady = uiState.isListeningReady,
            isDetectingSpeech = uiState.isDetectingSpeech,
            partialText = uiState.partialSpeechText,
            isTranslating = uiState.isTranslating,
            hasPermission = hasAudioPermission,
            isValidLanguagePair = uiState.isValidLanguagePair,
            onStartListening = { 
                if (hasAudioPermission) {
                    viewModel.startListening(uiState.sourceLanguage)
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            onStopListening = viewModel::stopListening,
            onClearHistory = viewModel::clearConversation,
            modifier = Modifier.padding(16.dp)
        )
        
        // Error display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
private fun LanguageSelectionRow(
    sourceLanguage: String,
    targetLanguage: String,
    onSourceLanguageChange: (String) -> Unit,
    onTargetLanguageChange: (String) -> Unit,
    onSwapLanguages: () -> Unit,
    autoPlayEnabled: Boolean,
    onAutoPlayToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
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
            
            // Auto-play toggle
            IconButton(onClick = onAutoPlayToggle) {
                Icon(
                    if (autoPlayEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (autoPlayEnabled) "Auto-play enabled" else "Auto-play disabled",
                    tint = if (autoPlayEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LanguageChip(
    language: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = getLanguageDisplayName(language),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier
    )
}

@Composable
private fun ConversationHistory(
    conversationHistory: List<ConversationTurn>,
    onSpeakText: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new items are added
    LaunchedEffect(conversationHistory.size) {
        if (conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(conversationHistory.size - 1)
        }
    }
    
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(conversationHistory) { turn ->
            ConversationTurnItem(
                turn = turn,
                onSpeakOriginal = { onSpeakText(turn.originalText, turn.sourceLang) },
                onSpeakTranslation = { onSpeakText(turn.translatedText, turn.targetLang) }
            )
        }
    }
}

@Composable
private fun ConversationTurnItem(
    turn: ConversationTurn,
    onSpeakOriginal: () -> Unit,
    onSpeakTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Original text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(turn.sourceLang),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = turn.originalText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
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
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Translated text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(turn.targetLang),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = turn.translatedText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = onSpeakTranslation) {
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
private fun SpeechInputArea(
    isListening: Boolean,
    isListeningReady: Boolean,
    isDetectingSpeech: Boolean,
    partialText: String,
    isTranslating: Boolean,
    hasPermission: Boolean,
    isValidLanguagePair: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Partial text display
        if (partialText.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = partialText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Status text
        Text(
            text = when {
                !isValidLanguagePair -> "Invalid language pair (English required)"
                !hasPermission -> "Microphone permission required"
                isTranslating -> "Translating..."
                isDetectingSpeech -> "Listening..."
                isListeningReady -> "Say something"
                isListening -> "Starting..."
                else -> "Tap to start speaking"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (isValidLanguagePair && hasPermission) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear history button
            OutlinedIconButton(
                onClick = onClearHistory,
                enabled = !isListening && !isTranslating
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear conversation"
                )
            }
            
            // Main microphone button
            FloatingActionButton(
                onClick = if (isListening) onStopListening else onStartListening,
                modifier = Modifier.size(72.dp),
                containerColor = when {
                    !isValidLanguagePair -> MaterialTheme.colorScheme.errorContainer
                    !hasPermission -> MaterialTheme.colorScheme.error
                    isDetectingSpeech -> MaterialTheme.colorScheme.error
                    isListening -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                when {
                    !isValidLanguagePair -> {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Invalid language pair",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    isTranslating -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    isListening -> {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop listening",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    !hasPermission -> {
                        Icon(
                            Icons.Default.MicOff,
                            contentDescription = "Grant microphone permission",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                    else -> {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Start listening",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedHistorySection(
    savedHistory: List<ConversationTurn>,
    onSpeakText: (String, String) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Translations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${savedHistory.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Scrollable list of saved items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = savedHistory,
                    key = { it.timestamp }
                ) { turn ->
                    SavedHistoryItem(
                        turn = turn,
                        onSpeakOriginal = { onSpeakText(turn.originalText, turn.sourceLang) },
                        onSpeakTranslation = { onSpeakText(turn.translatedText, turn.targetLang) },
                        onDelete = { onDelete(turn.timestamp) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedHistoryItem(
    turn: ConversationTurn,
    onSpeakOriginal: () -> Unit,
    onSpeakTranslation: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Original text with delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(turn.sourceLang),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = turn.originalText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Row {
                    IconButton(onClick = onSpeakOriginal) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            // Translated text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getLanguageDisplayName(turn.targetLang),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = turn.translatedText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(onClick = onSpeakTranslation) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak translation",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
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
private fun ConversationScreenPreview() {
    GlobalTranslationTheme {
        Surface {
            // Preview with mock data would go here
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Conversation Screen Preview")
            }
        }
    }
}
