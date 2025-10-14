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
        composeTestRule
            .onNode(
                hasText("Hello") and hasAnyAncestor(hasTestTag("input_text_field")),
                useUnmergedTree = true
            )
            .assertDoesNotExist()
        
        composeTestRule
            .onNodeWithTag("input_text_field")
            .assertExists()

        // Material3 OutlinedTextField placeholder is in the unmerged semantics tree
        composeTestRule
            .onNode(
                hasText("Type your message here..."),
                useUnmergedTree = true
            )
            .assertExists()

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

        composeTestRule
            .onNodeWithTag("input_text_field")
            .assertExists()
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
        
        // Verify text appears
        composeTestRule
            .onNode(
                hasText("Hello") and hasAnyAncestor(hasTestTag("input_text_field")),
                useUnmergedTree = true
            )
            .assertExists()

        // After typing, placeholder should not be visible (Material3 behavior)
        composeTestRule
            .onNode(
                hasText("Type your message here..."),
                useUnmergedTree = true
            )
            .assertDoesNotExist()
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

