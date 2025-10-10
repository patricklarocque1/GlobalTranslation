---
applyTo: '**/*'
---

# Copilot Code Analysis & Debugging Rules

## Overview
These rules ensure thorough code analysis, proper context understanding, and mistake detection when working with the **COMPLETED** GlobalTranslation Android app codebase. All core features are implemented and functional.

## Core Analysis Principles

### 1. ALWAYS Read Full File Context First

- **Never assume** - Read the entire file before making any changes
- **Understand the complete scope** of each file before editing
- **Check imports, dependencies, and class structure** to understand relationships
- **Read related files** when changes might affect multiple components

### Current Project Structure (All Implemented)

```
app/src/main/java/com/example/globaltranslation/
├── MainActivity.kt                    # NavigationSuiteScaffold host
├── GloabTranslationApplication.kt     # @HiltAndroidApp
├── model/ConversationTurn.kt          # Data model for conversation history
├── services/                          # ✅ ALL SERVICES IMPLEMENTED
│   ├── ServicesModule.kt             # Hilt DI module
│   ├── TranslationService.kt         # ML Kit translation + model mgmt
│   ├── SpeechRecognitionService.kt   # Android SpeechRecognizer wrapper
│   └── TextToSpeechService.kt        # TTS with language support
├── ui/components/
│   └── LanguagePicker.kt             # Reusable dialog/button components
├── ui/conversation/                   # ✅ LIVE TRANSLATION COMPLETE
│   ├── ConversationScreen.kt         # Voice I/O with permissions
│   └── ConversationViewModel.kt      # State management + services
├── ui/textinput/                      # ✅ TEXT TRANSLATION COMPLETE
│   ├── TextInputScreen.kt            # Manual input with history
│   └── TextInputViewModel.kt         # Translation history management
├── ui/languages/                      # ✅ MODEL MANAGEMENT COMPLETE
│   ├── LanguageScreen.kt             # Download/status UI
│   └── LanguageViewModel.kt          # ML Kit model operations
└── ui/theme/                          # Material3 theme configuration
```

### 2. Complete Context Gathering Rules

```text
BEFORE making ANY code change:
1. Read the target file completely (start to end)
2. Read related files (ViewModels, Services, Dependencies)
3. Check build.gradle.kts for dependency versions
4. Verify import statements match available dependencies
5. Understand the existing architecture patterns
```

### 3. Cross-Reference Validation

- **Check version compatibility** in `gradle/libs.versions.toml` before suggesting dependencies
- **Verify existing patterns** match suggested solutions
- **Confirm Hilt setup** is correctly configured before adding @Inject annotations
- **Validate navigation structure** matches AppDestinations enum

## Debugging & Error Detection Rules

### 1. Common Mistake Patterns to Watch For

#### Build System Issues

```text
❌ WRONG: Adding dependencies without checking version catalog
✅ CORRECT: Check libs.versions.toml first, then add to version catalog

❌ WRONG: Using outdated dependency syntax
✅ CORRECT: Use libs.dependency.name format from version catalog

❌ WRONG: Missing kotlin.android plugin in app/build.gradle.kts
✅ CORRECT: kotlin.android plugin IS required for proper Kotlin compilation

❌ WRONG: KSP version format 2.2.20-1.0.x
✅ CORRECT: KSP changed versioning to 2.2.20-2.0.x (note the 2.0 not 1.0)

❌ WRONG: Mismatched JVM targets between Java and Kotlin
✅ CORRECT: Both compileOptions and kotlinOptions must target JVM 11
```

#### Hilt Integration Mistakes

```text
❌ WRONG: Adding @HiltViewModel without @Inject constructor
✅ CORRECT: Always pair @HiltViewModel with @Inject constructor

❌ WRONG: Injecting services without proper Hilt modules
✅ CORRECT: Ensure services are provided in Hilt modules first

❌ WRONG: Using @AndroidEntryPoint without Application class setup
✅ CORRECT: Verify GloabTranslationApplication is properly configured
```

#### Architecture Violations

```text
❌ WRONG: Direct service calls from Compose UI
✅ CORRECT: All service calls go through ViewModels

❌ WRONG: Navigation logic in ViewModels
✅ CORRECT: Navigation handled at Compose level only

❌ WRONG: Mutable state exposed directly
✅ CORRECT: Use StateFlow/MutableStateFlow pattern with private _state
```

### 2. Pre-Change Validation Checklist

Before suggesting ANY code change, verify:

- [ ] Does the target file actually exist?
- [ ] Are all imports available and correct?
- [ ] Is the suggested pattern consistent with existing code?
- [ ] Will this change break existing functionality?
- [ ] Are all dependencies properly declared?
- [ ] Is the Hilt setup correctly configured for this change?

### 3. Post-Change Validation

After making changes, always:

- [ ] Check that imports are still valid
- [ ] Verify no circular dependencies were introduced
- [ ] Confirm the change follows project architecture
- [ ] Ensure build.gradle.kts has required dependencies
- [ ] Test that the change aligns with existing patterns

## File-Specific Analysis Rules

### MainActivity.kt Analysis

When reading/editing MainActivity:

1. Check current AppDestinations enum values
2. Verify NavigationSuiteScaffold setup
3. Confirm @AndroidEntryPoint annotation
4. Understand current navigation state management

### ViewModel Analysis

When working with ViewModels:

1. Verify @HiltViewModel and @Inject constructor pairing
2. Check StateFlow/MutableStateFlow pattern usage
3. Confirm proper viewModelScope usage for coroutines
4. Validate error handling patterns

### Service Class Analysis

When creating/editing services:

1. Check if Hilt module exists for the service
2. Verify proper dependency injection setup
3. Confirm interface contracts match usage
4. Validate error handling and async patterns

### Build File Analysis

When modifying build files:

1. Check libs.versions.toml for existing versions
2. Verify AGP 8.13.0 and Kotlin 2.2.20 compatibility
3. Confirm KSP version matches Kotlin (2.2.20-2.0.2 format, not 1.0.x)
4. Validate plugin application order (kotlin.android is required!)
5. Ensure JVM target alignment between compileOptions and kotlinOptions
6. Verify all required plugins are present:
   - android.application
   - kotlin.android (REQUIRED)
   - kotlin.compose
   - ksp (for Hilt)
   - hilt

## Context Understanding Rules

### 1. Project State Awareness

Always remember:

- Project is in **transformation phase** - template code being replaced
- Many features are **planned but not implemented**
- Follow **Project Plan.md** for implementation order
- Current code may be **placeholder/template** code

### 2. Architecture Pattern Recognition

Identify and maintain consistency with:

- **Hilt dependency injection** throughout
- **StateFlow pattern** for UI state
- **Single Activity architecture** with Compose navigation
- **Material3 with adaptive navigation** for UI
- **Version catalog dependency management**

### 3. Error Pattern Recognition

Watch for these common issues:

- **Template code confusion** - Don't assume template features are final
- **Dependency version mismatches** - Always check version catalog
- **KSP version format errors** - Use 2.0.x not 1.0.x for Kotlin 2.2.20
- **Missing kotlin.android plugin** - Required for Kotlin compilation
- **JVM target mismatch** - Must align compileOptions and kotlinOptions
- **Hilt setup incomplete** - Verify full dependency chain
- **Navigation structure assumptions** - Check current AppDestinations

## Debugging Workflow

### When Code Doesn't Work:

1. **Read the full error message** - don't assume the cause
2. **Check build.gradle.kts dependencies** - verify all required libs are present
3. **Verify Hilt configuration** - check Application class and modules
4. **Validate import statements** - ensure all imports are available
5. **Check version compatibility** - ensure AGP 8.13.0, Kotlin 2.2.20, and KSP 2.2.20-2.0.2 compatibility
6. **Verify JVM target alignment** - both Java and Kotlin must target JVM 11
7. **Review existing patterns** - ensure consistency with current codebase

### When Suggesting Solutions:

1. **Explain the full context** of why the solution works
2. **Reference existing code patterns** when possible
3. **Mention any dependencies** that need to be added
4. **Note any configuration changes** required
5. **Highlight potential side effects** of the change

## Critical Reminders

### Before Every Code Change:

- ✅ Read the complete target file
- ✅ Understand the existing architecture
- ✅ Check dependency availability
- ✅ Verify Hilt configuration
- ✅ Confirm pattern consistency

### ML Kit Translation Specific:

- ✅ Use `RemoteModelManager.getInstance()` to check download status
- ✅ Never check models by attempting translation (triggers download)
- ✅ Always mention WiFi requirement in error messages
- ✅ Remember: First-time downloads require WiFi connection
- ✅ `translate()` auto-downloads, `areModelsDownloaded()` only checks

### Red Flags to Stop and Investigate:

- 🚨 Import errors after changes
- 🚨 Hilt compilation errors
- 🚨 Navigation structure changes without planning
- 🚨 Dependency version conflicts
- 🚨 Architecture pattern violations

### Success Criteria:

- ✅ Code compiles successfully
- ✅ Follows existing patterns
- ✅ Maintains architecture consistency
- ✅ All dependencies properly configured
- ✅ Hilt injection works correctly

## Integration with Main Instructions

These analysis rules work alongside the main `copilot-instructions.md`:

- **Main instructions** provide project overview and patterns
- **Analysis rules** ensure thorough investigation and mistake prevention
- **Together** they provide complete guidance for reliable code changes

Use both files together - main instructions for "what to build", analysis rules for "how to build it safely".