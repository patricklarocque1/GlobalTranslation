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
    
    @Before
    fun setup() {
        hiltRule.inject()
        fakeTranslationProvider.shouldSucceed = true
        fakeTranslationProvider.translationResult = "Hola"
        
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
            .onNodeWithText("Enter text to translate", substring = true)
            .assertExists()
    }
    
    @Test
    fun textInputScreen_displaysLanguageSelectors() {
        // Should have language selection buttons
        composeTestRule
            .onAllNodesWithContentDescription("Select language")
            .assertCountEquals(2)
    }
    
    @Test
    fun textInputScreen_hasTranslateButton() {
        composeTestRule
            .onNode(hasText("Translate") or hasContentDescription("Translate"))
            .assertExists()
    }
    
    @Test
    fun textInputScreen_hasSwapButton() {
        composeTestRule
            .onNodeWithContentDescription("Swap languages")
            .assertExists()
    }
    
    @Test
    fun textInputScreen_hasClearButton() {
        composeTestRule
            .onNode(hasText("Clear") or hasContentDescription("Clear"))
            .assertExists()
    }
    
    @Test
    fun textInputScreen_enterText_displaysInField() {
        // Find and type in text field
        composeTestRule
            .onNodeWithText("Enter text to translate", substring = true)
            .performTextInput("Hello")
        
        composeTestRule.waitForIdle()
        
        // Verify text appears
        composeTestRule
            .onNodeWithText("Hello")
            .assertExists()
    }
    
    @Test
    fun textInputScreen_translationDisplaysResult() {
        // Enter text
        composeTestRule
            .onNodeWithText("Enter text to translate", substring = true)
            .performTextInput("Hello")
        
        // Click translate button
        composeTestRule
            .onNode(hasText("Translate") or hasContentDescription("Translate"))
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Should show translated text
        composeTestRule
            .onNodeWithText("Translated Text: Hello", substring = true)
            .assertExists()
    }
}

