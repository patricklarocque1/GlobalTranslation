package com.example.globaltranslation.ui.camera

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Camera screen for real-time text recognition and translation.
 * Uses CameraX for camera preview and ML Kit for text recognition.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    Box(modifier = modifier.fillMaxSize()) {
        when {
            cameraPermissionState.status.isGranted -> {
                // State to hold image capture controller
                var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
                
                CameraPreview(
                    onImageCaptureReady = { capture -> imageCapture = capture },
                    isFlashOn = uiState.isFlashOn,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Overlay UI
                var showLanguagePicker by remember { mutableStateOf(false) }
                
                val context = LocalContext.current
                val executor = remember { Executors.newSingleThreadExecutor() }
                
                DisposableEffect(Unit) {
                    onDispose {
                        executor.shutdown()
                    }
                }
                
                CameraOverlay(
                    uiState = uiState,
                    onFlashToggle = { viewModel.toggleFlash() },
                    onLanguagePickerClick = { showLanguagePicker = true },
                    onClearResults = { viewModel.clearResults() },
                    onCaptureClick = {
                        // Capture photo when button is tapped
                        imageCapture?.let { capture ->
                            capture.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    @androidx.camera.core.ExperimentalGetImage
                                    override fun onCaptureSuccess(imageProxy: androidx.camera.core.ImageProxy) {
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val inputImage = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            viewModel.processCapturedImage(inputImage)
                                        }
                                        imageProxy.close()
                                    }
                                    
                                    override fun onError(exception: ImageCaptureException) {
                                        exception.printStackTrace()
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Language picker dialog
                if (showLanguagePicker) {
                    CameraLanguagePickerDialog(
                        currentSourceLanguage = uiState.sourceLanguageCode,
                        currentTargetLanguage = uiState.targetLanguageCode,
                        onLanguagesSelected = { sourceCode, targetCode ->
                            viewModel.setSourceLanguage(sourceCode)
                            viewModel.setTargetLanguage(targetCode)
                            showLanguagePicker = false
                        },
                        onDismiss = { showLanguagePicker = false }
                    )
                }
            }
            else -> {
                // Permission request UI
                CameraPermissionRequest(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Camera preview component using CameraX.
 * Sets up preview and image capture (no continuous analysis).
 */
@Composable
private fun CameraPreview(
    onImageCaptureReady: (ImageCapture) -> Unit,
    isFlashOn: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    
    // Update flash when isFlashOn changes
    LaunchedEffect(isFlashOn) {
        camera?.cameraControl?.enableTorch(isFlashOn)
    }
    
    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            // Preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            
            // Image capture use case (for taking photos)
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                // Apply initial flash state
                camera?.cameraControl?.enableTorch(isFlashOn)
                
                // Notify that image capture is ready
                onImageCaptureReady(imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
        
        onDispose {
            camera = null
        }
    }
    
    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

/**
 * Overlay UI for camera controls and detected text display.
 */
@Composable
private fun CameraOverlay(
    uiState: CameraUiState,
    onFlashToggle: () -> Unit,
    onLanguagePickerClick: () -> Unit,
    onClearResults: () -> Unit,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash toggle
            IconButton(
                onClick = onFlashToggle,
                modifier = Modifier
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (uiState.isFlashOn) "Flash On" else "Flash Off",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Language selector - Shows source → target
            FilterChip(
                selected = false,
                onClick = onLanguagePickerClick,
                label = { 
                    Text("${getLanguageName(uiState.sourceLanguageCode)} → ${getLanguageName(uiState.targetLanguageCode)}")
                }
            )
        }
        
        // Document-style translation display (merged content)
        if (uiState.detectedTextBlocks.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Take remaining space
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header with clear button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Translation",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = onClearResults,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear results",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Scrollable document view - merge all translations
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Merge all original text into one document
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Original",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = uiState.detectedTextBlocks
                                        .joinToString("\n\n") { it.originalText },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        // Merge all translations into one document
                        item {
                            HorizontalDivider()
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Translation",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = uiState.detectedTextBlocks
                                        .mapNotNull { it.translatedText }
                                        .joinToString("\n\n"),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Processing indicator
        if (uiState.isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Error message or info card
        if (uiState.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Column {
                            Text(
                                text = uiState.error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Download models for: ${getLanguageName(uiState.sourceLanguageCode)} and ${getLanguageName(uiState.targetLanguageCode)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        } else if (uiState.detectedTextBlocks.isEmpty() && !uiState.isProcessing && !uiState.isFrozen) {
            // Help message when no text is detected and camera is live
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Point camera at text",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the button below to capture and translate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Capture/Resume button at the bottom (Google Lens style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (!uiState.isFrozen) {
                // Capture button
                FloatingActionButton(
                    onClick = onCaptureClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Capture and translate",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                // Resume camera button
                FloatingActionButton(
                    onClick = onClearResults,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Resume camera",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

/**
 * Camera permission request UI.
 */
@Composable
private fun CameraPermissionRequest(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "To use camera translation, please grant camera permission.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

/**
 * Language picker dialog for camera translation.
 * Allows selecting source and target languages.
 */
@Composable
private fun CameraLanguagePickerDialog(
    currentSourceLanguage: String,
    currentTargetLanguage: String,
    onLanguagesSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSource by remember { mutableStateOf(currentSourceLanguage) }
    var selectedTarget by remember { mutableStateOf(currentTargetLanguage) }
    
    val languages = mapOf(
        com.google.mlkit.nl.translate.TranslateLanguage.ENGLISH to "English",
        com.google.mlkit.nl.translate.TranslateLanguage.SPANISH to "Spanish",
        com.google.mlkit.nl.translate.TranslateLanguage.FRENCH to "French",
        com.google.mlkit.nl.translate.TranslateLanguage.GERMAN to "German",
        com.google.mlkit.nl.translate.TranslateLanguage.ITALIAN to "Italian",
        com.google.mlkit.nl.translate.TranslateLanguage.PORTUGUESE to "Portuguese",
        com.google.mlkit.nl.translate.TranslateLanguage.CHINESE to "Chinese",
        com.google.mlkit.nl.translate.TranslateLanguage.JAPANESE to "Japanese",
        com.google.mlkit.nl.translate.TranslateLanguage.KOREAN to "Korean",
        com.google.mlkit.nl.translate.TranslateLanguage.RUSSIAN to "Russian",
        com.google.mlkit.nl.translate.TranslateLanguage.ARABIC to "Arabic",
        com.google.mlkit.nl.translate.TranslateLanguage.HINDI to "Hindi"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Camera Translation Languages",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Translate from",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                // Source language selector
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    items(languages.entries.toList()) { (code, name) ->
                        FilterChip(
                            selected = code == selectedSource,
                            onClick = { selectedSource = code },
                            label = { Text(name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }
                }
                
                Text(
                    text = "Translate to",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
                
                // Target language selector
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    items(languages.entries.toList()) { (code, name) ->
                        FilterChip(
                            selected = code == selectedTarget,
                            onClick = { selectedTarget = code },
                            label = { Text(name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }
                }
                
                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onLanguagesSelected(selectedSource, selectedTarget) }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

/**
 * Helper function to get language display name.
 */
private fun getLanguageName(languageCode: String): String {
    return when (languageCode) {
        com.google.mlkit.nl.translate.TranslateLanguage.ENGLISH -> "English"
        com.google.mlkit.nl.translate.TranslateLanguage.SPANISH -> "Spanish"
        com.google.mlkit.nl.translate.TranslateLanguage.FRENCH -> "French"
        com.google.mlkit.nl.translate.TranslateLanguage.GERMAN -> "German"
        com.google.mlkit.nl.translate.TranslateLanguage.ITALIAN -> "Italian"
        com.google.mlkit.nl.translate.TranslateLanguage.PORTUGUESE -> "Portuguese"
        com.google.mlkit.nl.translate.TranslateLanguage.CHINESE -> "Chinese"
        com.google.mlkit.nl.translate.TranslateLanguage.JAPANESE -> "Japanese"
        com.google.mlkit.nl.translate.TranslateLanguage.KOREAN -> "Korean"
        com.google.mlkit.nl.translate.TranslateLanguage.RUSSIAN -> "Russian"
        com.google.mlkit.nl.translate.TranslateLanguage.ARABIC -> "Arabic"
        com.google.mlkit.nl.translate.TranslateLanguage.HINDI -> "Hindi"
        else -> languageCode
    }
}

