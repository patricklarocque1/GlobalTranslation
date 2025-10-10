# GlobalTranslation Quick Reference Card

## 🏗️ Architecture

**Multi-Module Clean Architecture** (3 modules):
- `:core` - Pure Kotlin, domain models, interfaces
- `:data` - Android Library, Room + ML Kit implementations
- `:app` - Android App, Compose UI + ViewModels

**Package**: `com.example.globaltranslation`

## 🚀 Essential Build Info

```kotlin
// Versions (gradle/libs.versions.toml)
kotlin = "2.2.20"
ksp = "2.2.20-2.0.2"    // Note: 2.0.x not 1.0.x!
hilt = "2.57.2"
agp = "8.13.0"

// JVM Target (app/build.gradle.kts)
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
// Note: Old kotlinOptions is deprecated

// Required Plugins (app/build.gradle.kts)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)     // REQUIRED!
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}
```

## 📐 StateFlow Pattern (ALL ViewModels)

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val service: MyService
) : ViewModel() {
    
    // ✅ CORRECT: Private + Public with .asStateFlow()
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    fun updateState() {
        _uiState.value = _uiState.value.copy(/* changes */)
    }
    
    override fun onCleared() {
        super.onCleared()
        service.cleanup()  // Don't forget!
    }
}

data class MyUiState(
    val data: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## 🔧 Hilt Patterns

```kotlin
// Application
@HiltAndroidApp
class GloabTranslationApplication : Application()

// Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// Service
@Singleton
class MyService @Inject constructor() {
    fun cleanup() { /* cleanup */ }
}

// ViewModel (see StateFlow pattern above)
@HiltViewModel
class MyViewModel @Inject constructor(...)

// In Composable
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
}
```

## 🎨 Compose Patterns

```kotlin
// Screen Composable
@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    MyContent(
        data = uiState.data,
        onAction = viewModel::handleAction,
        modifier = modifier
    )
}

// Component Composable
@Composable
fun MyComponent(
    data: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onAction,
        modifier = modifier
    ) {
        Text(data)
    }
}
```

## 📁 Project Structure

### Multi-Module Layout
```
:core/src/main/kotlin/com/example/globaltranslation/core/
├── model/ (ConversationTurn)
├── provider/ (5 interfaces)
├── repository/ (ConversationRepository)
└── util/ (TextBlockGroupingUtil)

:data/src/main/kotlin/com/example/globaltranslation/data/
├── provider/ (ML Kit implementations)
├── repository/ (Room-based)
├── local/ (Room database)
└── di/ (Hilt modules)

:app/src/main/java/com/example/globaltranslation/
├── GloabTranslationApplication.kt    # @HiltAndroidApp
├── MainActivity.kt                   # @AndroidEntryPoint
├── model/
│   └── ConversationTurn.kt (typealias to :core)
├── services/                         # @Singleton + @Inject (being migrated)
│   ├── ServicesModule.kt
│   ├── TranslationService.kt
│   ├── SpeechRecognitionService.kt
│   └── TextToSpeechService.kt
└── ui/
    ├── components/
    │   └── LanguagePicker.kt
    ├── conversation/                 # @HiltViewModel + StateFlow
    │   ├── ConversationScreen.kt
    │   └── ConversationViewModel.kt
    ├── textinput/                    # @HiltViewModel + StateFlow
    │   ├── TextInputScreen.kt
    │   └── TextInputViewModel.kt
    ├── languages/                    # @HiltViewModel + StateFlow
    │   ├── LanguageScreen.kt
    │   └── LanguageViewModel.kt
    └── theme/
```

## ❌ Common Mistakes

```kotlin
// ❌ DON'T expose MutableStateFlow
val uiState = MutableStateFlow(MyUiState())

// ❌ DON'T forget .asStateFlow()
val uiState: StateFlow<MyUiState> = _uiState

// ❌ DON'T forget cleanup
// Missing onCleared() override

// ❌ DON'T call services from Composables
@Composable
fun MyScreen(service: MyService) {  // Wrong!
    service.doSomething()  // Use ViewModel instead
}

// ❌ DON'T use LiveData
val uiState: LiveData<MyUiState>  // Use StateFlow

// ❌ DON'T mismatch JVM targets
compileOptions { targetCompatibility = VERSION_11 }
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)  // Mismatch! Must be JVM_11
    }
}

// ❌ DON'T use deprecated kotlinOptions
kotlinOptions { jvmTarget = "11" }  // Deprecated! Use compilerOptions

// ❌ DON'T use wrong KSP version
ksp = "2.2.20-1.0.20"  // Wrong! Should be 2.0.x

// ❌ DON'T forget kotlin.android plugin
plugins {
    alias(libs.plugins.android.application)
    // Missing kotlin.android! Required!
}
```

## ✅ Correct Patterns

```kotlin
// ✅ DO use immutable StateFlow
private val _uiState = MutableStateFlow(MyUiState())
val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

// ✅ DO clean up resources
override fun onCleared() {
    super.onCleared()
    service.cleanup()
}

// ✅ DO use ViewModels in Composables
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
}

// ✅ DO use Result<T> for service returns
suspend fun translate(text: String): Result<String>

// ✅ DO use viewModelScope
fun updateData() {
    viewModelScope.launch {
        // async work
    }
}

// ✅ DO use data classes for state
data class MyUiState(
    val data: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## 🔍 Quick Troubleshooting

| Error | Solution |
|-------|----------|
| Plugin `com.google.devtools.ksp` not found | Use KSP `2.2.20-2.0.2` (not 1.0.x) |
| JVM target mismatch | Both compileOptions and kotlinOptions must be JVM 11 |
| ClassNotFoundException: Application | Missing `kotlin.android` plugin |
| Hilt_* classes not generated | 1. Add kotlin.android plugin<br>2. Run `./gradlew clean build` |
| Import errors in ViewModel | Check Hilt and StateFlow imports |

## 📚 Documentation Files

| File | Use Case |
|------|----------|
| `.cursorrules` | Quick patterns & build config |
| `copilot-instructions.md` | Detailed architecture & patterns |
| `copilot-analysis-rules.instructions.md` | Error prevention & debugging |
| `Project Plan.md` | Implementation status |
| `README.md` | Setup & user docs |

## 🎯 Before You Code

1. ✅ Read current file content
2. ✅ Check pattern matches existing code
3. ✅ Verify dependencies in `gradle/libs.versions.toml`
4. ✅ Follow StateFlow pattern
5. ✅ Use constructor injection with `@Inject`

## 🎯 After You Code

1. ✅ Verify imports valid
2. ✅ Check StateFlow pattern maintained
3. ✅ Ensure cleanup if using services
4. ✅ Verify Hilt annotations correct
5. ✅ Test build: `./gradlew build`

## 🚨 Critical Rules

1. **JVM Targets Must Match**: Both Java and Kotlin = JVM 11
2. **KSP Version Format**: `2.2.20-2.0.2` (NOT 1.0.x)
3. **kotlin.android Plugin**: REQUIRED in app/build.gradle.kts
4. **StateFlow Pattern**: Private MutableStateFlow + Public StateFlow
5. **Resource Cleanup**: Override `onCleared()` when using services
6. **Constructor Injection**: Use `@Inject constructor()` for DI

## 🔗 Quick Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew :app:assembleDebug

# Install on device
./gradlew :app:installDebug

# Check dependencies
./gradlew :app:dependencies

# Sync Gradle
./gradlew --refresh-dependencies
```

## 📱 App Info

- **Package**: `com.example.globaltranslation`
- **Architecture**: Multi-module (:core, :data, :app)
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **4 Screens**: Conversation, Text Input, Camera, Languages
- **4 ViewModels**: All use StateFlow pattern
- **Persistence**: Room database in :data module
- **Services**: Legacy in :app, new providers in :data
- **Features**: Translation, TTS, Clipboard, Model Management

## 🔍 ML Kit Translation Gotchas

```kotlin
// ✅ CORRECT: Check download status without downloading
suspend fun areModelsDownloaded(from: String, to: String): Boolean {
    val modelManager = RemoteModelManager.getInstance()
    val fromModel = TranslateRemoteModel.Builder(from).build()
    val toModel = TranslateRemoteModel.Builder(to).build()
    return modelManager.isModelDownloaded(fromModel).await() &&
           modelManager.isModelDownloaded(toModel).await()
}

// ❌ WRONG: Checking by attempting translation
suspend fun areModelsDownloaded(from: String, to: String): Boolean {
    translator.translate("test").await()  // This downloads models!
    return true
}
```

**Critical ML Kit Behaviors:**
- First-time downloads **require WiFi** (enforced by DownloadConditions)
- `translate()` auto-downloads models if missing
- Check status with `RemoteModelManager.getInstance()`
- Delete models with `deleteModel(languageCode)`
- Deletion cleans up cached translators automatically
- Error messages should mention WiFi requirement

---

**Need More Details?** See:
- `.cursorrules` for patterns
- `.github/instructions/copilot-instructions.md` for architecture
- `.github/instructions/copilot-analysis-rules.instructions.md` for debugging

