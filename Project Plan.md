# Project Plan: Global Translation App

**App Name:** GlobalTranslation  
**Template:** Android Studio "Navigation Drawer Activity"  
**Architecture:** Model-View-ViewModel (MVVM) with Single-Activity, Multi-Fragment pattern

## 1. Re-purposing the Template Files

First, we will rename and re-purpose the boilerplate files from the template to fit our app's features. This provides a clean foundation.

| Original Template File | New Purpose | Rename To (/app/src/main/java/com/gloabtranslation/ui/) |
|------------------------|-------------|--------------------------------------------------------|
| ui/home/HomeFragment | Live Conversation Mode | conversation/ConversationFragment |
| ui/home/HomeViewModel | ViewModel for Conversation Mode | conversation/ConversationViewModel |
| ui/gallery/GalleryFragment | Text Input Translation | textinput/TextInputFragment |
| ui/gallery/GalleryViewModel | ViewModel for Text Input | textinput/TextInputViewModel |
| ui/slideshow/SlideshowFragment | Language Management | languages/LanguageFragment |
| ui/slideshow/SlideshowViewModel | ViewModel for Languages | languages/LanguageViewModel |

## 2. Feature Breakdown & Implementation Plan

This is the task list you will use to direct your AI agents.

### Module: Core Services (The "Brains")

These are non-UI classes. They will be created in a new package: `com.gloabtranslation.services`.

#### Task 2.1: TranslationService.kt
- **Description:** A self-contained class using ML Kit to translate text strings.
- **Method:** `translate(text: String, from: TranslateLanguage, to: TranslateLanguage, callback: (Result<String>) -> Unit)`
- **Test Cases:** Must handle successful translation, empty strings, and errors if a language model is not downloaded.

#### Task 2.2: TranscriptionService.kt
- **Description:** A class using ML Kit Speech Recognition to convert audio into text.
- **Method:** `startListening(languageHints: List<TranslateLanguage>, onResult: (String) -> Unit)`
- **Test Cases:** Must handle successful transcription, silence, and permission errors.

#### Task 2.3: SpeechService.kt
- **Description:** A wrapper for Android's built-in TextToSpeech (TTS) engine.
- **Method:** `speak(text: String, language: Locale)`
- **Test Cases:** Must verify that the TTS engine initializes correctly for a given language and that the `.speak()` method is called without errors.

### Module: UI Fragments (The "Screens")

#### Task 2.4: ConversationFragment.kt (Formerly Home)
- **Data Model Definition:** Create a new data class: 
  ```kotlin
  data class ConversationTurn(
      val originalText: String, 
      val translatedText: String, 
      val sourceLang: TranslateLanguage
  )
  ```
- **Description:** The main screen for live, two-way conversation translation.
- **ViewModel:** Will use ConversationViewModel.

#### Task 2.5: ConversationViewModel.kt (Formerly HomeViewModel)
- **Description:** Manages the state and logic for the ConversationFragment.
- **Dependencies:** Will hold instances of TranslationService, TranscriptionService, and SpeechService.
- **StateFlow:**
  - `transcript: StateFlow<List<ConversationTurn>>` - A running list of what was said and its translation.
  - `isListening: StateFlow<Boolean>` - To update the UI of the microphone button.

#### Task 2.6: LanguageFragment.kt (Formerly Slideshow)
- **Description:** Allows the user to manage downloadable language models.
- **UI Components:** A RecyclerView that lists all available languages. Each item should show the language name, a "download" icon if not downloaded, and a "delete" icon if it is.
- **ViewModel:** LanguageViewModel will interact with ML Kit's model manager.

## Phase 1: Project Setup & Foundation

Here is how you start. Give these tasks to your AI agents one by one.

### Project Setup:
1. Create the "Navigation Drawer Activity" project in Android Studio. Ensure the package name is `com.gloabtranslation`.
2. Add ML Kit dependencies (`com.google.mlkit:translate`, `com.google.mlkit:speech-recognition`) to your `build.gradle` file.
3. Add permissions for `RECORD_AUDIO` and `INTERNET` to `AndroidManifest.xml`.

### Refactor the Template:
**Prompt (Cursor):** "Rename the package ui.home to ui.conversation and update all imports. Do the same for ui.gallery -> ui.textinput and ui.slideshow -> ui.languages."

### Update Navigation & UI:
**Prompt (Cursor):** "In mobile_navigation.xml, update the IDs and labels to match the new fragment names. In activity_main_drawer.xml, update the menu item IDs and titles to 'Conversation', 'Text Input', and 'Languages'."

### Build Your First Module:
**Prompt (Aider/Cursor):** "Create a new file services/TranslationService.kt. Make it an injectable singleton using Hilt (@Singleton and @Inject constructor()). Using the plan, implement the translate method. Then, create a TranslationServiceTest.kt file and write a unit test to verify that 'Hello' in English translates to 'Hola' in Spanish."