package com.example.gloabtranslation.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gloabtranslation.ui.languages.SupportedLanguage
import com.example.gloabtranslation.ui.languages.getSupportedLanguages

/**
 * Language picker dialog component that can be reused across screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerDialog(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Select Language",
    modifier: Modifier = Modifier
) {
    val supportedLanguages = remember { getSupportedLanguages() }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Language list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(supportedLanguages) { language ->
                        LanguagePickerItem(
                            language = language,
                            isSelected = language.code == selectedLanguageCode,
                            onClick = {
                                onLanguageSelected(language.code)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePickerItem(
    language: SupportedLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Language icon placeholder
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Language picker button that opens the dialog when clicked.
 */
@Composable
fun LanguagePickerButton(
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    title: String = "Select Language",
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    
    val selectedLanguageName = remember(selectedLanguageCode) {
        getSupportedLanguages().find { it.code == selectedLanguageCode }?.name 
            ?: selectedLanguageCode.uppercase()
    }
    
    FilterChip(
        selected = false,
        onClick = { showDialog = true },
        label = {
            Text(
                text = selectedLanguageName,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier
    )
    
    if (showDialog) {
        LanguagePickerDialog(
            selectedLanguageCode = selectedLanguageCode,
            onLanguageSelected = onLanguageSelected,
            onDismiss = { showDialog = false },
            title = title
        )
    }
}