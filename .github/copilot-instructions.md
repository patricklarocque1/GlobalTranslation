# Copilot Instructions for GlobalTranslation Android App

## Project Overview
Android translation app using Jetpack Compose, currently in early development stage. The app will provide live conversation translation, text input translation, and language management using ML Kit. Currently based on a simple navigation template with placeholder UI.

## Critical Build Setup (MUST READ FIRST)

### AGP 9.0 + Hilt Compatibility Issue
- **CRITICAL**: Project uses AGP 9.0.0-alpha09 which breaks Hilt plugin compatibility
- **Required workaround**: `android.newDsl=false` in `gradle.properties` (already set)
- **Do NOT remove** the `kotlin.android` plugin from version catalog - it's removed from build files but needed in catalog
- **Build command**: Use `.\gradlew build` (PowerShell) or `./gradlew build` (bash)

### Dependency Management
- **Version Catalogs**: All dependencies in `gradle/libs.versions.toml` using `libs.` references
- **No kotlin.android plugin**: Removed from build files due to AGP 9.0 built-in Kotlin support
- **ML Kit**: Only translation (`mlkit-translate:17.0.3`) - speech recognition removed due to version conflicts

## Current Architecture

### Navigation Structure
- **Single Activity**: `MainActivity.kt` with `NavigationSuiteScaffold`
- **Destinations**: Defined in `AppDestinations` enum (CONVERSATION, TEXT_INPUT, LANGUAGES)
- **Adaptive UI**: Uses Material3 adaptive navigation suite for different screen sizes
- **Current state**: Navigation shell exists, content screens are placeholder `Greeting` composable

### Hilt Setup (Already Configured)
```kotlin
// Application class already exists
@HiltAndroidApp
class GloabTranslationApplication : Application()

// MainActivity is Hilt-enabled
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

## Development Roadmap (See Project Plan.md)

### Planned Package Structure
Following the template transformation plan in `Project Plan.md`:
- `services/` - Translation, speech, and TTS services (not yet implemented)
- `ui/conversation/` - Live conversation mode (planned)  
- `ui/textinput/` - Manual text translation (planned)
- `ui/languages/` - Language model management (planned)

### Key Implementation Patterns (When Building Features)

#### ML Kit Integration
```kotlin
// Translation service pattern (implement in services/)
class TranslationService @Inject constructor() {
    suspend fun translate(
        text: String, 
        from: TranslateLanguage, 
        to: TranslateLanguage
    ): Result<String>
}
```

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

#### ViewModel Pattern
```kotlin
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val translationService: TranslationService,
    private val speechService: SpeechRecognitionService
) : ViewModel() {
    private val _state = MutableStateFlow(ConversationState())
    val state: StateFlow<ConversationState> = _state.asStateFlow()
    
    // Always use viewModelScope for coroutines
    fun startTranslation(text: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = translationService.translate(text, fromLang, toLang)
                _state.value = _state.value.copy(
                    translatedText = result,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
```

## Development Workflow

### Build System
- **Kotlin DSL**: All build files use `.gradle.kts` format
- **Namespace**: `com.example.gloabtranslation` (note: should be updated from "gloabtranslation" typo)
- **Target SDK**: 36 (latest Android)
- **Min SDK**: 29 (Android 10+)

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

### Required Permissions (When Implementing)
Add to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />
```

## Critical Implementation Notes

1. **Project is in transformation phase** - Many planned features don't exist yet
2. **Follow Project Plan.md** - It contains the complete implementation roadmap
3. **Template refactoring required** - Current navigation destinations are placeholders
4. **ML Kit not yet integrated** - Will need dependencies added to `libs.versions.toml`
5. **Package naming inconsistency** - "gloabtranslation" typo should be addressed

## Next Development Steps
Refer to `Project Plan.md` Phase 1 for immediate tasks:
1. Add ML Kit dependencies to version catalog
2. Refactor template packages to match planned structure  
3. Update navigation to reflect translation app features
4. Implement core service classes with proper dependency injection
    )
}
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

## Critical Implementation Notes

1. **Project is in transformation phase** - Many planned features don't exist yet
2. **Follow Project Plan.md** - It contains the complete implementation roadmap
3. **Template refactoring required** - Current navigation destinations are placeholders
4. **ML Kit not yet integrated** - Will need dependencies added to `libs.versions.toml`
5. **Package naming inconsistency** - "gloabtranslation" typo should be addressed

## Next Development Steps
Refer to `Project Plan.md` Phase 1 for immediate tasks:
1. Add ML Kit dependencies to version catalog
2. Refactor template packages to match planned structure  
3. Update navigation to reflect translation app features
4. Implement core service classes with proper dependency injection