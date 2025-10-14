# GlobalTranslation

An Android translation app built with Jetpack Compose and ML Kit. **Multi-module clean architecture** with live conversation translation, camera translation, and language management capabilities.

## 🚀 Features

- **✅ Live Conversation Translation**: Real-time speech-to-speech translation with microphone input
- **✅ Text Input Translation**: Manual text translation with history, copy to clipboard, and TTS playback
- **✅ Camera Translation (NEW!)**: Real-time camera text recognition and translation with AR-style overlay
- **✅ Language Management**: Download and delete ML Kit translation models to manage offline storage
- **✅ Runtime Permissions**: Comprehensive camera and microphone permission handling with visual feedback
- **✅ Expressive Material3 Theme**: Modern design with lavender/purple palette and large corner radii
- **✅ Adaptive UI**: Material3 design with NavigationSuiteScaffold for different screen sizes
- **✅ Reusable Components**: Custom LanguagePicker with dialog and button variants
- **✅ Clipboard Integration**: Copy translations directly to system clipboard
- **✅ Text-to-Speech**: Speak both original and translated text in any supported language

## 🏗️ Current Status

**Development Phase**: ✅ **Core Features Complete**

- ✅ Navigation structure with adaptive NavigationSuiteScaffold
- ✅ Hilt dependency injection fully configured  
- ✅ Stable build system with AGP 8.13.0
- ✅ ML Kit translate integration with model management
- ✅ All core services implemented (Translation, Speech Recognition, TTS)
- ✅ All feature screens implemented with ViewModels
- ✅ Runtime permission management
- ✅ Modern Material3 UI with no deprecated APIs

## 🛠️ Tech Stack

- **Architecture**: Multi-module clean architecture (:core, :data, :app)
- **UI**: Jetpack Compose with Material3 Expressive Theme and adaptive navigation
- **Pattern**: MVVM with StateFlow and Hilt dependency injection
- **Translation**: ML Kit Translate API with offline model management
- **Persistence**: Room database for conversation history
- **Camera**: CameraX for preview and image analysis
- **OCR**: ML Kit Text Recognition v2 for camera text detection
- **Speech**: Android SpeechRecognizer + TextToSpeech integration
- **Navigation**: NavigationSuiteScaffold (adaptive for phone/tablet/desktop)
- **Build**: Gradle with Version Catalogs and KSP
- **Permissions**: Runtime permission handling with Accompanist Permissions
- **Testing**: JUnit + Hilt Testing + Compose UI Testing

## 📱 16KB Page Size Support

This app supports Android devices with **16KB memory pages** (ARM64):

- **ML Kit libraries**: Latest versions with 16KB compatibility
- **Room database**: 2.7+ with automatic page size handling  
- **Native libraries**: Verified for 16KB alignment
- **Tested on**: Android 15+ ARM64 emulators with 16KB pages
- **Compliance**: Ready for Google Play 16KB page size requirements

**Data Preservation**: Existing user data remains intact - no migration required. Room 2.7+ automatically handles page size differences.

## 🔧 Build Requirements

- **Android Studio**: Latest stable (tested with Ladybug+)
- **AGP**: 8.13.0 (stable build, Hilt-compatible)
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Kotlin**: 2.2.20 (latest stable)
- **KSP**: 2.2.20-2.0.2 (matching Kotlin version)
- **Hilt**: 2.57.2
- **JVM Target**: 11 (Java & Kotlin aligned)

### Stable Build Configuration

This project uses a stable, tested build configuration:

```kotlin
// All plugins properly configured in app/build.gradle.kts:
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)        // Required for Kotlin compilation
    alias(libs.plugins.kotlin.compose)        // Compose compiler plugin
    alias(libs.plugins.ksp)                   // For Hilt annotation processing
    alias(libs.plugins.hilt)                  // Hilt dependency injection
}

// JVM target aligned between Java and Kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

// All dependencies managed through libs.versions.toml version catalog
```

## 🚀 Getting Started

1. **Clone the repository**

   ```bash
   git clone https://github.com/patricklarocque1/GlobalTranslation.git
   cd GlobalTranslation
   ```

2. **Build the project**

   ```bash
   # PowerShell
   .\gradlew build
   
   # Bash
   ./gradlew build
   ```

3. **Run on device/emulator**

   ```bash
   .\gradlew installDebug
   ```

## 📁 Multi-Module Project Structure

**New Architecture**: Clean separation into :core (domain), :data (implementation), :app (UI)

```text
GlobalTranslation/
├── :core (Pure Kotlin) ✅ NEW
│   Domain models, interfaces, and business logic
│   - ConversationTurn model
│   - Provider interfaces (Translation, Speech, TTS, OCR, Camera)
│   - ConversationRepository interface
│   - TextBlockGroupingUtil + unit tests
│
├── :data (Android Library) ✅ NEW
│   Data layer with Room persistence and ML Kit implementations
│   - Provider implementations (ML Kit, Android APIs)
│   - Room database (ConversationDatabase, DAO, entities)
│   - RoomConversationRepository
│   - Hilt modules (DataModule, ProviderModule)
│
└── :app (Android App)
    UI layer with Compose screens and ViewModels
    - MainActivity with NavigationSuiteScaffold
    - 4 feature screens (Conversation, Text Input, Camera, Languages)
    - All ViewModels using :data providers ✅ Migration Complete
    - Material3 Expressive Theme
```

### Module Dependencies
- `:app` depends on `:core` and `:data`
- `:data` depends on `:core`
- `:core` has no dependencies (pure Kotlin)

### Benefits of Multi-Module Architecture
- **Testability**: Pure Kotlin :core module enables fast unit tests
- **Separation of Concerns**: Clear boundaries between domain, data, and UI
- **Reusability**: :core and :data can be shared with Wear OS or other platforms
- **Build Performance**: Parallel module compilation
- **Maintainability**: Enforced architecture through module boundaries

### Architecture Highlights

- **StateFlow Best Practices**: All ViewModels use proper immutable StateFlow exposure
  ```kotlin
  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()
  ```
- **Dependency Injection**: Constructor injection with Hilt throughout
- **Resource Management**: Proper cleanup in ViewModel `onCleared()`
- **Coroutines**: All async operations use `viewModelScope`

## 📚 Development Guide

### For Developers
- **Quick Reference**: See `.github/instructions/QUICK-REFERENCE.md` - Essential patterns and commands
- **Architecture Patterns**: See `.github/instructions/copilot-instructions.md` - Detailed implementation guide
- **Code Analysis Rules**: See `.github/instructions/copilot-analysis-rules.instructions.md` - Error prevention
- **Testing Strategy**: Comprehensive ViewModels with StateFlow testing patterns
- **Hilt Setup**: Complete dependency injection with services module

### For AI Assistants (Cursor, Copilot, etc.)
- **Cursor Rules**: See `.cursorrules` in root - Immediate patterns and build config
- **Copilot Instructions**: See `.github/instructions/copilot-instructions.md` - Comprehensive guide
- **Integration Guide**: See `.github/instructions/AI-AGENT-INTEGRATION.md` - How all docs work together
- **Analysis Rules**: See `.github/instructions/copilot-analysis-rules.instructions.md` - Debugging workflows

## ✅ **Completed Implementation (Verified)**

### Provider Architecture (All Implemented & Verified)

The app uses a clean provider pattern with interfaces in :core and implementations in :data:

- **TranslationProvider** (MlKitTranslationProvider) ✅
  - ML Kit integration with model download and deletion
  - Caches active translators for performance
  - Handles model download with WiFi conditions
  - Properly checks model download status using `RemoteModelManager`
  - Auto-downloads models on first translation (WiFi required)
  - Delete downloaded models to free storage space
  
- **SpeechProvider** (AndroidSpeechProvider) ✅
  - Android SpeechRecognizer with permission handling
  - Flow-based API for reactive speech recognition
  - Proper error handling and cleanup
  
- **TextToSpeechProvider** (AndroidTextToSpeechProvider) ✅
  - TTS with language-specific initialization
  - Flow-based speech events
  - Lifecycle-aware cleanup

- **TextRecognitionProvider** (MlKitTextRecognitionProvider) ✅
  - ML Kit Text Recognition for OCR
  - Processes images and extracts text blocks with bounding boxes
  - Returns hierarchical DetectedText structure (blocks > lines)
  - Proper resource cleanup

- **CameraTranslationProvider** (MlKitCameraTranslationProvider) ✅
  - Combined OCR + Translation pipeline
  - Processes camera frames through recognition pipeline
  - Translates detected text blocks in parallel (async + awaitAll)
  - Returns TranslatedTextBlock with original + translated text
  - Model availability checking before translation

**Architecture**: All ViewModels inject provider interfaces from :core, Hilt provides :data implementations

### UI Screens (All Implemented & Verified)

- **ConversationScreen**: Live voice translation with Room persistence ✅
  - Uses `ConversationViewModel` with providers from :data
  - Real-time speech recognition feedback
  - Auto-play translation support
  - Conversation history persisted to Room database
  
- **TextInputScreen**: Manual text translation with full features ✅
  - Uses `TextInputViewModel` with providers from :data
  - Translation history with timestamps
  - Copy to clipboard and copy to input functionality
  - Text-to-speech for both original and translated text
  - Speak button integration matching conversation screen

- **CameraScreen**: Real-time camera translation with OCR ✅
  - Uses `CameraViewModel` with CameraTranslationProvider from :data
  - CameraX preview with lifecycle management
  - Permission request UI with runtime handling
  - Real-time text detection and translation with throttling
  - Flash toggle and language selection controls
  - Processing indicator and error handling
  - Document-style translation display
  
- **LanguageScreen**: ML Kit model management ✅
  - Uses `LanguageViewModel` with TranslationProvider from :data
  - Dynamic download status checking
  - Download models for offline translation
  - Delete models to free storage space
  - 20+ supported languages

**Migration Complete**: All ViewModels now use :data providers instead of legacy :app services

### Architecture & Best Practices

- **StateFlow Pattern**: All ViewModels follow immutable state exposure best practices
  - Private `MutableStateFlow` for internal updates
  - Public `StateFlow` with `.asStateFlow()` for external consumption
  - Single source of truth maintained across all features
  
- **Reusable Components**: LanguagePicker dialog and button variants
- **Runtime Permissions**: Comprehensive RECORD_AUDIO permission handling
- **Modern APIs**: Material3 throughout with no deprecated API usage
- **Resource Management**: Proper cleanup in `onCleared()` prevents memory leaks

## 🚀 **Ready for Production**

The app is feature-complete and follows Android best practices:

✅ **Complete Features - 100% Production-Ready**
- Live conversation translation with Room persistence
- Manual text input translation with history, TTS, and clipboard
- Camera translation with real-time OCR (CameraX + ML Kit)
- Offline translation model management (download/delete)
- Material3 Expressive Theme with lavender/purple palette
- Multi-module clean architecture (:core, :data, :app)
- 16KB page size support for Google Play compliance
- Modern, adaptive Material3 UI
- Comprehensive error handling and permissions

✅ **Architecture Quality**
- Multi-module clean architecture with provider pattern
- All ViewModels migrated to :data providers (Oct 10, 2025)
- StateFlow best practices in all ViewModels
- Room database for conversation persistence
- Proper Hilt dependency injection throughout
- Resource cleanup preventing memory leaks
- Coroutine-based async operations with automatic cancellation
- Type-safe state management with data classes

✅ **Verified Implementation**
- All 4 ViewModels using provider pattern from :data
- Navigation uses adaptive NavigationSuiteScaffold
- No deprecated API usage
- Test infrastructure with fake providers
- Full build pipeline working (debug, release, sixteenKB variants)

### Future Enhancement Opportunities

The app is feature-complete for core translation needs. See **FEATURE_PLAN.md** for potential future enhancements (not currently planned):
- Face-to-Face Mode (split-screen conversation)
- AI Practice with Gemini (conversational learning)
- Image Translation (upload/translate images)
- Phrasebook (saved translations with categories)
- Enhanced UI/UX (promotional cards, advanced animations)

*Note: These are optional future features. Current implementation is 100% complete and production-ready.*

### Recent Bug Fixes

**Model Download Status Accuracy** (Fixed)
- **Issue**: Languages screen showed incorrect download status
- **Cause**: Checking models by attempting translation (which auto-downloaded)
- **Fix**: Now uses `RemoteModelManager.getInstance()` to check actual status
- **Impact**: Accurate download status, better error messages, clear WiFi guidance

**Text Input Copy/Speak Functionality** (Fixed)
- **Issue**: Copy and speak buttons were TODO placeholders, not functional
- **Cause**: TextToSpeechService not injected, clipboard not integrated
- **Fix**: Added TTS injection, clipboard manager, and proper callbacks
- **Impact**: Full feature parity with conversation screen, improved UX

**Model Deletion Feature** (Implemented)
- **Issue**: Remove button was a TODO placeholder
- **Cause**: No deleteModel() method in TranslationService
- **Fix**: Added deletion support using RemoteModelManager
- **Impact**: Users can free storage space by removing unused models

## 🛠️ Troubleshooting

### Common Build Issues

#### KSP Plugin Not Found
**Error**: `Plugin [id: 'com.google.devtools.ksp', version: '2.2.20-1.0.20'] was not found`

**Solution**: KSP changed their versioning scheme from `1.0.x` to `2.0.x`. For Kotlin 2.2.20, use KSP `2.2.20-2.0.2`:
```toml
# In gradle/libs.versions.toml
ksp = "2.2.20-2.0.2"  # Not 1.0.20!
```

#### JVM Target Mismatch
**Error**: `Inconsistent JVM-target compatibility detected`

**Solution**: Ensure both Java and Kotlin target the same JVM version:
```kotlin
// In app/build.gradle.kts
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
```

#### Application Class Not Found at Runtime
**Error**: `ClassNotFoundException: com.example.globaltranslation.GloabTranslationApplication`

**Solution**: Ensure the `kotlin.android` plugin is present:
```kotlin
// In app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)  // This is required!
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}
```

Then do a clean rebuild:
```bash
.\gradlew clean
.\gradlew :app:assembleDebug
```

## 🤝 Contributing

This project includes comprehensive documentation for both developers and AI coding assistants:

### Developer Documentation
- **Quick Reference Card**: `.github/instructions/QUICK-REFERENCE.md` - Print-friendly patterns cheat sheet
- **Project Plan**: `Project Plan.md` - Implementation status and history
- **This README**: Build setup, troubleshooting, and getting started

### AI Assistant Documentation
- **Cursor Rules**: `.cursorrules` - Quick patterns and critical build config
- **Copilot Instructions**: `.github/instructions/copilot-instructions.md` - Comprehensive architecture guide
- **Analysis Rules**: `.github/instructions/copilot-analysis-rules.instructions.md` - Error prevention and debugging
- **Integration Guide**: `.github/instructions/AI-AGENT-INTEGRATION.md` - How all instruction files work together

All documentation is kept synchronized and verified against the actual codebase.

## 📄 License

This project is available under the MIT License.
