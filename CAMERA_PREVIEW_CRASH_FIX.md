# Camera Preview Crash Fix

## Issue Summary
The app was crashing when Android Studio attempted to render the `CameraOverlayStatesPreview` Compose preview function. This was causing the preview rendering system to crash with a `NullPointerException`.

## Crash Details

### Error Message
```
FATAL EXCEPTION: main
Process: com.example.globaltranslation.debug, PID: 7166
java.lang.RuntimeException: java.lang.reflect.InvocationTargetException
...
Caused by: java.lang.NullPointerException: Parameter specified as non-null is null: 
    method com.example.globaltranslation.ui.camera.CameraScreenKt.CameraOverlayStatesPreview, 
    parameter state
```

### Key Log Entry
```
Previewing 'CameraOverlayStatesPreview' without a parameter provider.
```

This message indicates that Android Studio's preview system tried to invoke the preview function **without** using the `CameraUiStatePreviewProvider`, which meant it passed `null` to the non-nullable `state` parameter.

## Root Cause

The `CameraOverlayStatesPreview` function was defined as:

```kotlin
@Composable
fun CameraOverlayStatesPreview(
    @PreviewParameter(CameraUiStatePreviewProvider::class) state: CameraUiState  // ❌ No default value!
) {
    // Preview content...
}
```

**The Problem:**
1. The `state` parameter is **non-nullable** (`CameraUiState` not `CameraUiState?`)
2. The parameter has `@PreviewParameter` annotation pointing to `CameraUiStatePreviewProvider`
3. BUT there's **no default value** for the parameter
4. When Android Studio's preview system invokes this without a parameter provider (which it sometimes does), it passes `null`
5. Passing `null` to a non-nullable parameter causes a `NullPointerException`

## The Fix

Added a default value to the parameter:

```kotlin
@Composable
fun CameraOverlayStatesPreview(
    @PreviewParameter(CameraUiStatePreviewProvider::class) state: CameraUiState = CameraUiState()  // ✅ Default value!
) {
    // Preview content...
}
```

**Why This Works:**
- When the parameter provider is available, it uses the provided values (the 4 different states defined in `CameraUiStatePreviewProvider`)
- When the parameter provider is NOT available, it falls back to the default value `CameraUiState()` instead of `null`
- This prevents the `NullPointerException` while maintaining the intended preview functionality

## Technical Context

### About @PreviewParameter
`@PreviewParameter` is a Compose UI annotation that allows you to provide multiple parameter values for a single preview function, creating multiple preview variations. In this case:

```kotlin
private class CameraUiStatePreviewProvider : PreviewParameterProvider<CameraUiState> {
    override val values: Sequence<CameraUiState> = sequenceOf(
        CameraUiState(),                          // Default state
        CameraUiState(isFlashOn = true),         // Flash on state
        CameraUiState(/* with text blocks */),   // With detected text
        CameraUiState(error = "...")             // Error state
    )
}
```

This provider creates **4 different previews**, each showing the camera overlay in a different state.

### Why Android Studio Sometimes Skips the Provider
Android Studio's preview system has multiple rendering modes and optimization strategies. Sometimes it:
- Tries to render a "quick preview" without full parameter processing
- Falls back to simpler rendering when the full preview system has issues
- Uses different rendering paths during development vs. stable state

Without a default value, these fallback paths fail because they can't pass `null` to a non-nullable parameter.

## Similar Issues in the Codebase

You should check other preview functions that use `@PreviewParameter` to ensure they all have default values:

```bash
# Search for potential similar issues
grep -n "@PreviewParameter" app/src/main/java/**/*.kt
```

Any preview function with `@PreviewParameter` and a non-nullable parameter should have a default value.

## Best Practice

**Always provide default values for @PreviewParameter parameters:**

```kotlin
// ✅ GOOD
@Preview
@Composable
fun MyPreview(
    @PreviewParameter(MyProvider::class) data: MyData = MyData()
) { /* ... */ }

// ❌ BAD
@Preview
@Composable
fun MyPreview(
    @PreviewParameter(MyProvider::class) data: MyData  // No default!
) { /* ... */ }
```

## Testing the Fix

After applying this fix:
1. Android Studio's preview panel should render without crashing
2. You should see multiple preview variations (default, flash on, with text, error state)
3. The app should run normally on devices/emulators
4. No functionality is changed - only the preview rendering is fixed

## Impact

- **Changed Files**: `app/src/main/java/com/example/globaltranslation/ui/camera/CameraScreen.kt`
- **Lines Changed**: 1 line (added default parameter value)
- **Functionality Impact**: None - this only affects preview rendering
- **Runtime Impact**: None - the app behavior is unchanged
- **Preview Impact**: Previews now render correctly in all scenarios

## References

- Kotlin null safety: https://kotlinlang.org/docs/null-safety.html
- Compose Preview: https://developer.android.com/jetpack/compose/tooling/previews
- PreviewParameter: https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameter
