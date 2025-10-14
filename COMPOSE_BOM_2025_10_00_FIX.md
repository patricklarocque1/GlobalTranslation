# Fix for Compose BOM 2025.10.00 Material3 TextField Test Failures

## Issue Summary
The `TextInputScreenTest` was failing with errors like:
```
AssertionError: Failed: assertExists.
Reason: Expected exactly '1' node but could not find any node that satisfies: 
((Text + InputText + EditableText contains 'Hello' (ignoreCase: false)) && 
(hasAnyAncestorThat(TestTag = 'input_text_field')))
```

## Root Cause
Compose BOM 2025.10.00 introduced breaking changes to Material3's `OutlinedTextField` semantics:
- Placeholder text is no longer accessible via `hasText()` matcher
- Entered text is no longer searchable in the semantics tree (even with `useUnmergedTree = true`)
- The previous workaround documented in `TESTING_IMPROVEMENTS_SUMMARY.md` stopped working

## Solution
Changed test assertions to use direct TextField property access instead of node searches:

### Before (Broken):
```kotlin
// Failed to find text in semantics tree
composeTestRule
    .onNode(
        hasText("Hello") and hasAnyAncestor(hasTestTag("input_text_field")),
        useUnmergedTree = true
    )
    .assertExists()

// Failed to find placeholder
composeTestRule
    .onNode(
        hasText("Type your message here..."),
        useUnmergedTree = true
    )
    .assertExists()
```

### After (Fixed):
```kotlin
// Directly assert on TextField's text property
composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextContains("Hello", substring = true)

// Check empty state (must include label text when using includeEditableText=true)
composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextEquals("Enter text to translate", "", includeEditableText = true)
```

## Tests Fixed
1. **`textInputScreen_displaysInputField`** - Now verifies empty field state with `assertTextEquals("label", "", includeEditableText = true)`
2. **`textInputScreen_enterText_displaysInField`** - Now uses `assertTextContains()` to verify entered text
3. **`textInputScreen_clearButton_clearsInput`** - Now verifies cleared state with `assertTextEquals("label", "", includeEditableText = true)`

## Key Learning
When using `assertTextEquals(..., includeEditableText = true)` on Material3's OutlinedTextField:
- The first parameter is the **label text** (from the `label` parameter)
- The second parameter is the **editable text** (the actual input value)
- This is because Material3 includes the label in the Text semantics of the TextField

## Why This Works
The new approach uses `assertTextEquals()` and `assertTextContains()` which:
- Access the `EditableText` semantics property directly
- Are stable across Material3 versions
- Don't depend on the internal semantics tree structure
- Are the recommended way to test text fields in Compose

**Important**: When using `includeEditableText = true`, you must account for ALL text in the semantics node:
- Material3's `OutlinedTextField` includes the `label` parameter in its Text semantics
- Therefore, `assertTextEquals("label text", "editable text", includeEditableText = true)` checks both components
- For an empty field with a label, use: `assertTextEquals("Label", "", includeEditableText = true)`

## Files Changed
- `app/src/androidTest/java/com/example/globaltranslation/ui/textinput/TextInputScreenTest.kt`
- `TESTING_IMPROVEMENTS_SUMMARY.md` (updated documentation)

## Testing
Run the tests with:
```bash
./gradlew :app:connectedAndroidTest
```

The failing tests should now pass:
- `textInputScreen_enterText_displaysInField`
- `textInputScreen_displaysInputField`

## Future Considerations
When testing Material3 TextFields:
1. ✅ Use `assertTextEquals("label", "editable", includeEditableText = true)` for exact text matching with labels
2. ✅ Use `assertTextContains()` for substring matching (ignores label)
3. ✅ Always use test tags to identify fields
4. ✅ Remember to include label text in `assertTextEquals` when using `includeEditableText = true`
5. ❌ Don't search for text nodes in the semantics tree
6. ❌ Don't rely on `useUnmergedTree` for TextField text
7. ❌ Don't use `assertTextEquals("", includeEditableText = true)` on fields with labels

This pattern is now documented in `TESTING_IMPROVEMENTS_SUMMARY.md` for future reference.
