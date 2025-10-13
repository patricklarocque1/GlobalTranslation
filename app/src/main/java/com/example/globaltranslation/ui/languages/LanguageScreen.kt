package com.example.globaltranslation.ui.languages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.globaltranslation.ui.theme.GlobalTranslationTheme
import com.google.mlkit.nl.translate.TranslateLanguage

/**
 * Screen for managing translation language models.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        LanguageScreenHeader(
            isLoading = uiState.isLoading,
            onRefresh = viewModel::refreshLanguages
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Language Models",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Download language models to enable offline translation. All models are paired with English (e.g., English↔Spanish).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Network status indicator
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (uiState.networkState) {
                            is com.example.globaltranslation.data.network.NetworkState.WiFi -> Icons.Default.Wifi
                            is com.example.globaltranslation.data.network.NetworkState.Cellular -> Icons.Default.SignalCellularAlt
                            else -> Icons.Default.SignalWifiOff
                        },
                        contentDescription = "Network status",
                        modifier = Modifier.size(16.dp),
                        tint = when (uiState.networkState) {
                            is com.example.globaltranslation.data.network.NetworkState.WiFi -> MaterialTheme.colorScheme.primary
                            is com.example.globaltranslation.data.network.NetworkState.Cellular -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (uiState.networkState) {
                            is com.example.globaltranslation.data.network.NetworkState.WiFi -> "WiFi connected"
                            is com.example.globaltranslation.data.network.NetworkState.Cellular -> "Cellular connection"
                            is com.example.globaltranslation.data.network.NetworkState.Connected -> "Connected"
                            else -> "No connection"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Settings card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Allow Cellular Downloads",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (uiState.allowCellularDownloads) 
                            "Models can download on mobile data" 
                        else 
                            "Models require WiFi connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Switch(
                    checked = uiState.allowCellularDownloads,
                    onCheckedChange = { viewModel.toggleCellularDownloads() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Language list
        if (uiState.isLoading && uiState.availableLanguages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
            items(uiState.availableLanguages) { language ->
                LanguageModelItem(
                    language = language,
                    onDownload = { viewModel.downloadLanguage(language.code) },
                    onDelete = { viewModel.deleteLanguage(language.code) },
                    onCancel = { viewModel.cancelDownload(language.code) }
                )
            }
            }
        }
        
        // Error display
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
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
private fun LanguageScreenHeader(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Language Models",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage offline translation models",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = onRefresh,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
    }
}

@Composable
private fun LanguageModelItem(
    language: LanguageModel,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Language flag or icon placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = language.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when {
                                language.isDownloaded -> "Downloaded"
                                language.isDownloading -> "Downloading..."
                                else -> "Not downloaded"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                language.isDownloaded -> MaterialTheme.colorScheme.primary
                                language.isDownloading -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                
                // Action button
                when {
                    language.code == TranslateLanguage.ENGLISH -> {
                        // English is always available
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Always available",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    language.isDownloading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = onCancel) {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = "Cancel",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel")
                            }
                        }
                    }
                    language.isDownloaded -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Downloaded",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = onDelete
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove")
                            }
                        }
                    }
                    else -> {
                        Button(
                            onClick = onDownload,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download")
                        }
                    }
                }
            }
        
        // Show progress bar with actual percentage and status
        if (language.isDownloading || language.downloadStatus != com.example.globaltranslation.ui.languages.DownloadStatus.IDLE) {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (language.downloadStatus) {
                            com.example.globaltranslation.ui.languages.DownloadStatus.PREPARING -> "Preparing download..."
                            com.example.globaltranslation.ui.languages.DownloadStatus.DOWNLOADING -> "Downloading model..."
                            com.example.globaltranslation.ui.languages.DownloadStatus.FINALIZING -> "Finalizing..."
                            com.example.globaltranslation.ui.languages.DownloadStatus.COMPLETE -> "Complete!"
                            com.example.globaltranslation.ui.languages.DownloadStatus.PAUSED -> "Paused (waiting for network)"
                            com.example.globaltranslation.ui.languages.DownloadStatus.FAILED -> "Failed"
                            else -> "Downloading..."
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (language.downloadStatus) {
                            com.example.globaltranslation.ui.languages.DownloadStatus.COMPLETE -> MaterialTheme.colorScheme.primary
                            com.example.globaltranslation.ui.languages.DownloadStatus.PAUSED -> MaterialTheme.colorScheme.tertiary
                            com.example.globaltranslation.ui.languages.DownloadStatus.FAILED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                    
                    // Show percentage if available
                    if (language.downloadProgress != null) {
                        Text(
                            text = "${(language.downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Text(
                            text = "~10-30 MB",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                // Progress bar - determinate if we have progress, indeterminate otherwise
                if (language.downloadProgress != null) {
                    LinearProgressIndicator(
                        progress = { language.downloadProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = when (language.downloadStatus) {
                            com.example.globaltranslation.ui.languages.DownloadStatus.COMPLETE -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun LanguageScreenPreview() {
    GlobalTranslationTheme {
        Surface {
            // Preview would go here
        }
    }
}
