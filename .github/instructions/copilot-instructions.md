---
applyTo: '**/*'
---

# Copilot Instructions for GlobalTranslation Android App

## Project Overview
**PRODUCTION-READY** Android translation app with **multi-module clean architecture** using Jetpack Compose and ML Kit. Core features complete: live conversation translation, text input translation, camera translation, and language management. Architecture refactored into :core, :data, and :app modules for optimal testability and maintainability.

## Critical Build Setup (MUST READ FIRST)

### Stable Build Configuration
- **AGP Version**: Uses stable AGP 8.13.0 with reliable Hilt integration
- **Kotlin Version**: Kotlin 2.2.20 (latest stable)
- **KSP Version**: 2.2.20-2.0.2 (matches Kotlin version - note: KSP versioning changed from 1.0.x to 2.0.x)
- **Hilt Version**: 2.57.2
- **JVM Target**: 11 (must be aligned between Java and Kotlin compileOptions)
- **Build command**: Use `.\gradlew build` (PowerShell) or `./gradlew build` (bash)

### Dependency Management
- **Version Catalogs**: All dependencies in `gradle/libs.versions.toml` using `libs.` references
- **Required Plugins**: `kotlin.android`, `kotlin.compose`, `ksp`, `hilt` all properly configured
- **ML Kit**: Only translation (`mlkit-translate:17.0.3`) - speech recognition removed due to version conflicts
- **Critical**: JVM target must match in both `compileOptions` and `kotlinOptions` to avoid compatibility errors

## Current Architecture - Multi-Module Clean Architecture

### Module Structure ✅
```
:core (Pure Kotlin, no Android)
├── model/ (ConversationTurn)
├── provider/ (5 interfaces: Translation, Speech, TTS, OCR, Camera)
├── repository/ (ConversationRepository)
└── util/ (TextBlockGroupingUtil + tests)

:data (Android Library)
├── provider/ (ML Kit & Android implementations)
├── repository/ (Room-based ConversationRepository)
├── local/ (Room database, DAO, entities)
└── di/ (Hilt modules: DataModule, ProviderModule)

:app (Android App)
├── ui/ (Compose screens + ViewModels)
├── services/ (Legacy - being migrated to :data)
└── navigation/ (NavigationSuiteScaffold)
```

**Package Name**: `com.example.globaltranslation` (fixed from gloabtranslation)

### Navigation Structure
- **Single Activity**: `MainActivity.kt` with `NavigationSuiteScaffold`
- **Destinations**: Defined in `AppDestinations` enum (CONVERSATION, TEXT_INPUT, CAMERA, LANGUAGES)
- **Adaptive UI**: Uses Material3 adaptive navigation suite for different screen sizes
- **Current state**: ✅ All screens fully implemented and functional

### Hilt Setup ✅ COMPLETE
```kotlin
// Application class configured
@HiltAndroidApp
class GloabTranslationApplication : Application()  // Note: Class name unchanged for backward compatibility

// MainActivity is Hilt-enabled
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// Providers available via :data module
// Legacy services in :app (being migrated)
```

## ✅ Implemented Features - FULLY FUNCTIONAL

### Package Structure (in :app module)
All features have been successfully implemented:
- `services/` - ✅ Translation, SpeechRecognition, TTS, TextRecognition, CameraTranslation + ServicesModule
- `ui/conversation/` - ✅ Live conversation translation with voice I/O
- `ui/textinput/` - ✅ Manual text translation with history management
- `ui/camera/` - ✅ Real-time OCR translation with CameraX
- `ui/languages/` - ✅ ML Kit model download and management
- `ui/components/` - ✅ Reusable LanguagePicker dialog and button components
- `ui/theme/` - ✅ Material3 Expressive Theme (lavender/purple palette)

### Key Implementation Patterns (When Building Features)

#### ML Kit Integration
```kotlin
// Translation service pattern (fully implemented in services/)
@Singleton
class TranslationService @Inject constructor() {
    
    // Translates text, auto-downloads models on WiFi if needed
    suspend fun translate(
        text: String, 
        from: String, 
        to: String
    ): Result<String>
    
    // CRITICAL: Checks actual download status without triggering downloads
    // Uses RemoteModelManager.getInstance() to check model availability
    suspend fun areModelsDownloaded(
        fromLanguage: String,
        toLanguage: String
    ): Boolean
    
    // Explicitly downloads models (requires WiFi)
    suspend fun downloadModels(
        fromLanguage: String,
        toLanguage: String
    ): Result<Unit>
    
    // Deletes downloaded models to free storage space
    suspend fun deleteModel(languageCode: String): Result<Unit>
}
```

**ML Kit Translation Best Practices:**
- ✅ **Model Checking**: Use `areModelsDownloaded()` to check status without downloading
- ✅ **WiFi Requirement**: First-time downloads require WiFi connection
- ✅ **Auto-Download**: `translate()` automatically downloads models if missing (on WiFi)
- ✅ **Error Messages**: Include guidance about WiFi and model downloads
- ❌ **DON'T**: Check models by attempting translation (triggers download)
- ❌ **DON'T**: Assume models are available without checking
- ❌ **DON'T**: Forget to handle cellular network errors

#### Speech Recognition Alternative
- **Do NOT use** ML Kit speech - use Android's built-in `SpeechRecognizer`
- No additional dependencies needed, more stable than ML Kit version
- Pattern: `SpeechRecognizer.createSpeechRecognizer(context)`

#### Hilt Module Pattern (When Implementing Services)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    
    @Provides
    @Singleton
    fun provideTranslationService(): TranslationService = TranslationService()
    
    @Provides
    @Singleton
    fun provideSpeechRecognitionService(@ApplicationContext context: Context): SpeechRecognitionService = 
        SpeechRecognitionService(context)
}
```

#### ViewModel Pattern (Verified Implementation)

**All ViewModels in this project follow StateFlow best practices:**

```kotlin
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val translationService: TranslationService,
    private val speechService: SpeechRecognitionService
) : ViewModel() {
    // BEST PRACTICE: Private MutableStateFlow for internal updates
    private val _uiState = MutableStateFlow(ConversationUiState())
    // BEST PRACTICE: Public immutable StateFlow with .asStateFlow()
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()
    
    // Always use viewModelScope for automatic cancellation
    fun startTranslation(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslating = true)
            try {
                val result = translationService.translate(text, fromLang, toLang)
                result.fold(
                    onSuccess = { translatedText ->
                        _uiState.value = _uiState.value.copy(
                            translatedText = translatedText,
                            isTranslating = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = exception.message,
                            isTranslating = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isTranslating = false
                )
            }
        }
    }
    
    // BEST PRACTICE: Clean up resources in onCleared()
    override fun onCleared() {
        super.onCleared()
        speechService.cleanup()
    }
}

// Data class for immutable state
data class ConversationUiState(
    val translatedText: String = "",
    val isTranslating: Boolean = false,
    val error: String? = null
)
```

**Why This Pattern?**
- ✅ Immutable state exposure prevents external modification
- ✅ Single source of truth maintained
- ✅ Thread-safe state updates
- ✅ Compose automatically recomposes on state changes
- ✅ Resource cleanup prevents memory leaks

## Development Workflow

### Build System
- **Kotlin DSL**: All build files use `.gradle.kts` format
- **Namespace**: `com.example.globaltranslation` (typo fixed in architecture refactoring)
- **Target SDK**: 36 (latest Android)
- **Min SDK**: 29 (Android 10+)
- **Plugin Configuration**: All plugins properly declared in `app/build.gradle.kts`:
  ```kotlin
  plugins {
      alias(libs.plugins.android.application)
      alias(libs.plugins.kotlin.android)     // Required!
      alias(libs.plugins.kotlin.compose)
      alias(libs.plugins.ksp)
      alias(libs.plugins.hilt)
  }
  ```
- **JVM Target Alignment**: Both Java and Kotlin must target JVM 11:
  ```kotlin
  compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
  }
  kotlin {
      compilerOptions {
          jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
      }
  }
  // Note: kotlinOptions is deprecated, use kotlin.compilerOptions instead
  ```

### File Organization
- Follow the refactoring plan in `Project Plan.md` when implementing features
- Template files should be renamed: `ui/home` → `ui/conversation`, `ui/gallery` → `ui/textinput`, etc.
- Services go in dedicated `services/` package, not mixed with UI code

### Compose Patterns
- Use `@PreviewScreenSizes` for responsive design testing
- Theme defined in `ui/theme/` - leverage existing `GloabTranslationTheme`
- Material3 with adaptive navigation suite for cross-device compatibility

## Project-Specific Conventions

### Current Navigation Pattern
```kotlin
// AppDestinations enum drives navigation in MainActivity.kt
enum class AppDestinations(val label: String, val icon: ImageVector) {
    CONVERSATION("Conversation", Icons.Filled.Mic),
    TEXT_INPUT("Text Input", Icons.Filled.Translate),
    LANGUAGES("Languages", Icons.Filled.Language),
}
```

### Required Permissions ✅ IMPLEMENTED
Already configured in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```

## ✅ Implementation Complete - Production Ready

1. **All planned features implemented** - Translation app is fully functional
2. **Complete architecture in place** - Services, ViewModels, UI screens all working
3. **Modern Material3 UI** - Adaptive navigation with custom components
4. **ML Kit fully integrated** - Translation models, download management implemented
5. **Runtime permissions handled** - Microphone permission with visual feedback

## 🎯 Current Codebase Status (Verified)

### Implementation Complete ✅

1. ✅ **Complete service layer** with Hilt dependency injection
   - All services use @Singleton and @Inject
   - Proper resource cleanup in ViewModels
   - Flow-based reactive APIs
   - Model deletion support

2. ✅ **All three main screens** with full functionality
   - ConversationScreen: Live voice translation with TTS
   - TextInputScreen: Manual text translation with history, clipboard copy, and TTS
   - LanguageScreen: Model download, deletion, and status management

3. ✅ **Voice translation** with speech recognition and TTS
   - Real-time speech recognition with Flow
   - Auto-play translation support
   - Comprehensive error handling

4. ✅ **Manual text translation** with complete feature set
   - Translation history with timestamps
   - Copy to clipboard functionality
   - Copy to input functionality (separate buttons with distinct icons)
   - Text-to-speech for original and translated text
   - Language swapping

5. ✅ **Language model management** interface
   - 20+ supported languages
   - Dynamic download status checking
   - WiFi-only model downloads
   - Delete models to free storage space
   - Protected English model (cannot be deleted)

### StateFlow Verification ✅

All ViewModels implement StateFlow best practices:

```kotlin
// ✅ ConversationViewModel - 282 lines
private val _uiState = MutableStateFlow(ConversationUiState())
val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

// ✅ TextInputViewModel - 176 lines  
private val _uiState = MutableStateFlow(TextInputUiState())
val uiState: StateFlow<TextInputUiState> = _uiState.asStateFlow()

// ✅ LanguageViewModel - 209 lines
private val _uiState = MutableStateFlow(LanguageUiState())
val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()
```

### Architecture Verified ✅

```
✅ Project Structure Matches Documentation
✅ All Services Properly Injected
✅ StateFlow Pattern Consistent Across All ViewModels
✅ Resource Cleanup in onCleared()
✅ Coroutines Use viewModelScope
✅ No Deprecated API Usage
✅ Material3 Throughout
```
```

#### Compose UI Testing
```kotlin
// Test specific translation app UI behaviors
@Test
fun `conversation screen shows microphone button when not listening`() {
    composeTestRule.setContent {
        ConversationScreen(
            isListening = false,
            onStartListening = { }
        )
    }
    
    composeTestRule
        .onNodeWithContentDescription("Start listening")
        .assertIsDisplayed()
}

### Required Permissions (When Implementing)
Add to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```

## Project-Specific Conventions

### File Organization
- Follow the refactoring plan in `Project Plan.md` when implementing features
- Template files should be renamed: `ui/home` → `ui/conversation`, `ui/gallery` → `ui/textinput`, etc.
- Services go in dedicated `services/` package, not mixed with UI code

### Compose Patterns
- Use `@PreviewScreenSizes` for responsive design testing
- Theme defined in `ui/theme/` - leverage existing `GloabTranslationTheme`
- Material3 with adaptive navigation suite for cross-device compatibility

## Testing Strategy

### Service Testing Pattern
```kotlin
@ExtendWith(MockitoExtension::class)
class TranslationServiceTest {
    
    @Test
    fun `translate returns success for valid input`() = runTest {
        val service = TranslationService()
        val result = service.translate("Hello", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH)
        
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }
    
    @Test
    fun `translate handles ML Kit model not downloaded`() = runTest {
        // Test offline behavior and model download prompts
    }
}
```

### ViewModel Testing with StateFlow
```kotlin
@ExtendWith(MockitoExtension::class)
class ConversationViewModelTest {
    
    @Mock private lateinit var translationService: TranslationService
    
    @Test
    fun `state updates correctly during translation`() = runTest {
        val viewModel = ConversationViewModel(translationService)
        val testCollector = viewModel.state.test {
            
            viewModel.startTranslation("Hello")
            
            // Verify loading state
            expectItem().isLoading shouldBe true
            // Verify success state
            expectItem().let { state ->
                state.isLoading shouldBe false
                state.translatedText shouldBe "Hola"
            }
        }
    }
}
```

### Compose UI Testing
```kotlin
@Test
fun `conversation screen shows microphone button when not listening`() {
    composeTestRule.setContent {
        ConversationScreen(
            isListening = false,
            onStartListening = { }
        )
    }
    
    composeTestRule
        .onNodeWithContentDescription("Start listening")
        .assertIsDisplayed()
}
```

## Architecture Constraints

### State Management Rules
- **Single Source of Truth**: Each screen has one ViewModel holding all UI state
- **No Direct Service Access**: Compose screens never call services directly, only through ViewModels  
- **Immutable State**: Use data classes with copy() for state updates
- **Error Handling**: Always wrap service calls in try/catch with proper error states

### ML Kit Integration Constraints
- **Model Management**: Check model availability before translation attempts
- **Offline Handling**: Gracefully handle missing language models with download prompts
- **Memory Management**: Properly close ML Kit clients in ViewModel onCleared()

### Navigation Architecture
- **No Navigation in ViewModels**: Navigation events must be handled at Compose level
- **Shared State**: Use SavedStateHandle for data that survives process death
- **Deep Links**: Plan for deep link support in navigation structure

### Performance Guidelines  
- **LazyColumn**: Use for any list with >10 items (conversation history, language list)
- **Image Loading**: Use Coil for any future image features
- **Background Work**: ML Kit operations must run on background threads via viewModelScope

## 🚀 Implementation Guidelines for Future Development

When extending this completed app:

1. **Architecture is established** - Follow existing MVVM + Hilt patterns
2. **Services are injectable** - Use existing ServicesModule for new dependencies
3. **UI follows Material3** - Extend existing component patterns for consistency
4. **Testing patterns documented** - Follow established ViewModel and service testing patterns
5. **Build system stable** - AGP 8.13.0 + Kotlin 2.1.0 configuration is production-ready

## Future Enhancement Areas
Potential areas for expansion:
1. Add unit tests for ViewModels and services
2. Implement UI tests for complex user flows  
3. Add more language pairs or specialized translation features
4. Integrate additional ML Kit features (handwriting, etc.)