# GlobalTranslation

An Android translation app built with Jetpack Compose and ML Kit. **Now feature-complete** with live conversation translation, text input translation, and language management capabilities.

## 🚀 Features

- **✅ Live Conversation Translation**: Real-time speech-to-speech translation with microphone input
- **✅ Text Input Translation**: Manual text translation with history and language swapping  
- **✅ Language Management**: Download and manage ML Kit translation models offline
- **✅ Runtime Permissions**: Comprehensive microphone permission handling with visual feedback
- **✅ Adaptive UI**: Material3 design with NavigationSuiteScaffold for different screen sizes
- **✅ Reusable Components**: Custom LanguagePicker with dialog and button variants

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

- **UI**: Jetpack Compose with Material3 and adaptive navigation
- **Architecture**: MVVM with StateFlow and Hilt dependency injection
- **Translation**: ML Kit Translate API with offline model management
- **Speech**: Android SpeechRecognizer + TextToSpeech integration
- **Navigation**: NavigationSuiteScaffold (adaptive for phone/tablet/desktop)
- **Build**: Gradle with Version Catalogs and KSP
- **Permissions**: Runtime permission handling with visual feedback

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
kotlinOptions {
    jvmTarget = "11"
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

## 📁 Project Structure (Verified)

```text
app/src/main/java/com/example/gloabtranslation/
├── GloabTranslationApplication.kt     # @HiltAndroidApp ✅
├── MainActivity.kt                    # @AndroidEntryPoint with NavigationSuiteScaffold ✅
├── model/
│   └── ConversationTurn.kt           # Data model for conversation history ✅
├── services/                         # All @Singleton with @Inject ✅
│   ├── ServicesModule.kt             # Hilt dependency injection module ✅
│   ├── TranslationService.kt         # ML Kit translation + model management ✅
│   ├── SpeechRecognitionService.kt   # Android SpeechRecognizer wrapper ✅
│   └── TextToSpeechService.kt        # TTS service with language support ✅
├── ui/
│   ├── components/
│   │   └── LanguagePicker.kt         # Reusable language dialog/button ✅
│   ├── conversation/                 # Live conversation translation ✅
│   │   ├── ConversationScreen.kt     # Voice translation UI + permissions ✅
│   │   └── ConversationViewModel.kt  # @HiltViewModel with StateFlow ✅
│   ├── textinput/                    # Manual text translation ✅
│   │   ├── TextInputScreen.kt        # Text input UI with history ✅
│   │   └── TextInputViewModel.kt     # @HiltViewModel with StateFlow ✅
│   ├── languages/                    # ML Kit model management ✅
│   │   ├── LanguageScreen.kt         # Model download/status UI ✅
│   │   └── LanguageViewModel.kt      # @HiltViewModel with StateFlow ✅
│   └── theme/                        # Material3 theme configuration ✅
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

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

### Core Services (All Implemented & Verified)

- **TranslationService**: ML Kit integration with model download management ✅
  - Singleton service with proper resource cleanup
  - Caches active translators for performance
  - Handles model download with WiFi conditions
  
- **SpeechRecognitionService**: Android SpeechRecognizer with permission handling ✅
  - Flow-based API for reactive speech recognition
  - Proper error handling and cleanup
  
- **TextToSpeechService**: TTS with language-specific initialization ✅
  - Flow-based speech events
  - Lifecycle-aware cleanup

### UI Screens (All Implemented & Verified)

- **ConversationScreen**: Live voice translation with microphone input ✅
  - Uses `ConversationViewModel` with StateFlow
  - Real-time speech recognition feedback
  - Auto-play translation support
  
- **TextInputScreen**: Manual text translation with history management ✅
  - Uses `TextInputViewModel` with StateFlow
  - Translation history with timestamps
  - Copy-to-input functionality
  
- **LanguageScreen**: ML Kit model download and status tracking ✅
  - Uses `LanguageViewModel` with StateFlow
  - Dynamic download status checking
  - 20+ supported languages

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

✅ **Complete Features**
- Live conversation translation capabilities
- Manual text input translation with history
- Offline translation model management
- Modern, adaptive Material3 UI
- Comprehensive error handling and permissions

✅ **Code Quality**
- StateFlow best practices in all ViewModels
- Proper Hilt dependency injection throughout
- Resource cleanup preventing memory leaks
- Coroutine-based async operations with automatic cancellation
- Type-safe state management with data classes

✅ **Verified Implementation**
- All 3 ViewModels implement immutable StateFlow exposure
- All services use @Singleton and @Inject correctly
- Navigation uses adaptive NavigationSuiteScaffold
- No deprecated API usage

### Future Enhancements (Optional)

While production-ready, these enhancements could be considered:
- Unit tests for ViewModel StateFlow emissions
- Room database for persistent translation history
- Performance optimizations (lazy loading, caching)
- Enhanced accessibility features
- Analytics integration

*Note: These are enhancements, not bugs. Current implementation is complete and functional.*

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
kotlinOptions {
    jvmTarget = "11"  // Must match compileOptions!
}
```

#### Application Class Not Found at Runtime
**Error**: `ClassNotFoundException: com.example.gloabtranslation.GloabTranslationApplication`

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
