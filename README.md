# GlobalTranslation

An Android translation app built with Jetpack Compose and ML Kit. Currently in early development, transitioning from a navigation template to a full-featured translation application.

## 🚀 Planned Features

- **Live Conversation Translation**: Real-time speech-to-speech translation
- **Text Input Translation**: Manual text translation with copy/share functionality  
- **Language Management**: Download and manage ML Kit translation models
- **Offline Support**: Works without internet once language models are downloaded
- **Adaptive UI**: Material3 design that adapts to different screen sizes

## 🏗️ Current Status

**Development Phase**: Template transformation and core setup

- ✅ Navigation structure with adaptive UI
- ✅ Hilt dependency injection configured
- ✅ Build system working with AGP 9.0 alpha
- ✅ ML Kit translate dependency added
- 🔄 Core services (planned - see Project Plan.md)
- 🔄 Feature screens (planned - see Project Plan.md)

## 🛠️ Tech Stack

- **UI**: Jetpack Compose with Material3
- **Architecture**: MVVM with StateFlow
- **Dependency Injection**: Hilt
- **Translation**: ML Kit Translate API
- **Speech Recognition**: Android SpeechRecognizer (planned)
- **Navigation**: NavigationSuiteScaffold (adaptive)
- **Build**: Gradle with Version Catalogs

## 🔧 Build Requirements

- **Android Studio**: Latest stable or preview
- **AGP**: 9.0.0-alpha09 (with compatibility workarounds)
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36
- **Kotlin**: 2.2.20

### Critical Build Setup

This project uses AGP 9.0 alpha which requires specific workarounds:

```properties
# In gradle.properties (already configured)
android.newDsl=false  # Required for Hilt compatibility
```

The `org.jetbrains.kotlin.android` plugin is removed from build files due to AGP 9.0's built-in Kotlin support.

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

## 📁 Project Structure

```text
app/src/main/java/com/example/gloabtranslation/
├── MainActivity.kt                 # Navigation host with AppDestinations
├── GloabTranslationApplication.kt  # Hilt application class
├── ui/theme/                       # Material3 theme configuration
└── model/                          # Data models (ConversationTurn)

# Planned structure (see Project Plan.md):
├── services/                       # Translation, speech, TTS services
├── ui/conversation/               # Live translation screen
├── ui/textinput/                  # Manual translation screen
└── ui/languages/                  # Language model management
```

## 📚 Development Guide

- **Architecture Patterns**: See `.github/copilot-instructions.md`
- **Implementation Roadmap**: See `Project Plan.md`
- **Testing Strategy**: Included in copilot instructions
- **Hilt Setup**: Complete dependency injection examples provided

## 🎯 Next Steps

1. Implement core translation service (see Project Plan.md Phase 1)
2. Create conversation screen UI
3. Add text input translation functionality
4. Implement language model management
5. Add speech recognition integration

## 🤝 Contributing

This project includes comprehensive AI agent guidelines in `.github/copilot-instructions.md` with:

- Build setup requirements
- Architecture constraints  
- Testing patterns
- Implementation examples

## 📄 License

This project is available under the MIT License.
