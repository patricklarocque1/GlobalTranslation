# TextField Label Text Fix for Material3 Tests

## Problem
Tests were failing with:
```
java.lang.AssertionError: Failed to assert the following: (Text + EditableText = [])
Semantics of the node:
```

Affected tests:
- `textInputScreen_displaysInputField`
- `textInputScreen_clearButton_clearsInput`

## Root Cause
Material3's `OutlinedTextField` includes the `label` parameter in its **Text** semantics. When using `assertTextEquals(..., includeEditableText = true)`, the assertion checks:
1. **Text semantics** (which includes the label)
2. **EditableText semantics** (which contains the actual input value)

So for a TextField defined as:
```kotlin
OutlinedTextField(
    value = inputText,
    label = { Text("Enter text to translate") },
    // ...
)
```

The semantics tree looks like:
- Text: "Enter text to translate"
- EditableText: "" (empty when no input)

## Solution
Change the assertion from:
```kotlin
// ❌ WRONG - Only checks EditableText, ignores label
composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextEquals("", includeEditableText = true)
```

To:
```kotlin
// ✅ CORRECT - Checks both label (Text) and input (EditableText)
composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextEquals("Enter text to translate", "", includeEditableText = true)
```

## The Pattern
When testing an empty OutlinedTextField with a label:
```kotlin
assertTextEquals(
    "Label text here",           // First parameter: label (Text semantics)
    "",                          // Second parameter: editable text (EditableText semantics)
    includeEditableText = true   // Must be true to check both
)
```

When testing a filled OutlinedTextField with a label:
```kotlin
assertTextEquals(
    "Label text here",           // First parameter: label (Text semantics)
    "User input text",           // Second parameter: editable text (EditableText semantics)
    includeEditableText = true   // Must be true to check both
)
```

## Alternative Approach
If you don't want to deal with the label, use `assertTextContains()`:
```kotlin
// Only checks the EditableText, ignores label
composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextContains("", substring = false)  // For empty field

composeTestRule
    .onNodeWithTag("input_text_field")
    .assertTextContains("Hello", substring = true)  // For filled field
```

## Files Changed
- `app/src/androidTest/java/com/example/globaltranslation/ui/textinput/TextInputScreenTest.kt`
  - Fixed `textInputScreen_displaysInputField()` test
  - Fixed `textInputScreen_clearButton_clearsInput()` test
- `COMPOSE_BOM_2025_10_00_FIX.md` - Updated with new pattern
- `TESTING_IMPROVEMENTS_SUMMARY.md` - Documented best practice

## Key Takeaway
**Always remember**: `assertTextEquals(..., includeEditableText = true)` checks **ALL** text in the semantics node, not just the editable text. For Material3's OutlinedTextField, this includes the label!
