package com.example.globaltranslation.ui.textinput

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.globaltranslation.MainActivity
import com.example.globaltranslation.fake.FakeTranslationProvider
import com.example.globaltranslation.fake.FakeTtsProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentation tests for TextInputScreen.
 * Tests text input, translation, history, and TTS features.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TextInputScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var fakeTranslationProvider: FakeTranslationProvider
    
    @Inject
    lateinit var fakeTtsProvider: FakeTtsProvider
    
    @Inject
    lateinit var appPreferences: com.example.globaltranslation.data.preferences.AppPreferences
    
    @Inject
    lateinit var networkMonitor: com.example.globaltranslation.data.network.NetworkMonitor
    
    @Before
    fun setup() {
        hiltRule.inject()
        fakeTranslationProvider.shouldSucceed = true
        fakeTranslationProvider.translationResult = "Hola"
        
        // Reset preferences to prevent test pollution
        kotlinx.coroutines.runBlocking {
            appPreferences.clearAll()
        }
        
        // Navigate to Text Input tab
        composeTestRule.onNodeWithText("Text Input").performClick()
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun textInputScreen_displaysInNavigation() {
        composeTestRule
            .onNodeWithText("Text Input")
            .assertIsDisplayed()
    }
    
    @Test
    fun textInputScreen_displaysInputField() {
        // Verify text field exists
        composeTestRule
            .onNodeWithTag("input_text_field")
            .assertExists()
        
        // Verify field is initially empty
        // Material3 OutlinedTextField: when empty, only label text is present
        composeTestRule
            .onNodeWithTag("input_text_field")
            // Compose asserts against Text + EditableText; include empty EditableText
            .assertTextEquals("Enter text to translate", "")

        // Clear button should not exist when field is empty
        composeTestRule
            .onNodeWithTag("clear_btn")
            .assertDoesNotExist()
    }
    
    @Test
    fun textInputScreen_displaysLanguageSelectors() {
        // Should have language selection buttons
        composeTestRule
            .onNodeWithTag("source_language_chip")
            .assertExists()

        composeTestRule
            .onNodeWithTag("target_language_chip")
            .assertExists()
    }
    
    @Test
    fun textInputScreen_hasTranslateButton() {
        composeTestRule
            .onNodeWithTag("translate_btn")
            .assertExists()
    }
    
    @Test
    fun textInputScreen_hasSwapButton() {
        composeTestRule
            .onNodeWithTag("swap_languages_btn")
            .assertExists()
    }
    
    @Test
    fun textInputScreen_hasClearButton() {
        // Type some text so the clear button is rendered
        composeTestRule
            .onNodeWithTag("input_text_field")
            .performTextInput("Hello")

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("clear_btn")
            .assertExists()
    }

    @Test
    fun textInputScreen_clearButton_clearsInput() {
        composeTestRule
            .onNodeWithTag("input_text_field")
            .performTextInput("Hello")

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("clear_btn")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify field is empty after clearing
        // Check that there's no text in the editable field
        // Material3 TextField with label: use assertTextEquals with just label when empty
        composeTestRule
            .onNodeWithTag("input_text_field")
            // When focused after clearing, placeholder is present; include it and empty EditableText
            .assertTextEquals("Enter text to translate", "Type your message here...", "")
    }

    @Test
    fun textInputScreen_swapButton_swapsLanguages() {
        composeTestRule
            .onNodeWithTag("source_language_chip")
            .assert(hasText("English"))

        composeTestRule
            .onNodeWithTag("target_language_chip")
            .assert(hasText("Spanish"))

        composeTestRule
            .onNodeWithTag("swap_languages_btn")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("source_language_chip")
            .assert(hasText("Spanish"))

        composeTestRule
            .onNodeWithTag("target_language_chip")
            .assert(hasText("English"))
    }
    
    @Test
    fun textInputScreen_enterText_displaysInField() {
        // Find and type in text field
        composeTestRule
            .onNodeWithTag("input_text_field")
            .performTextInput("Hello")
        
        composeTestRule.waitForIdle()
        
        // Verify text appears in the field (Compose BOM 2025.10.00 changed semantics)
        composeTestRule
            .onNodeWithTag("input_text_field")
            .assertTextContains("Hello", substring = true)
    }
    
    @Test
    fun textInputScreen_translationDisplaysResult() {
        // Enter text
        composeTestRule
            .onNodeWithTag("input_text_field")
            .performTextInput("Hello")
        
        composeTestRule.waitForIdle()

        // Click translate button
        composeTestRule
            .onNodeWithTag("translate_btn")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Should show translated text
        composeTestRule
            .onAllNodesWithText("Hola: Hello")
            .onFirst()
            .assertExists()
    }
}

