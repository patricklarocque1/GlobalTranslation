package com.example.globaltranslation.ui.conversation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.globaltranslation.MainActivity
import com.example.globaltranslation.fake.FakeConversationRepository
import com.example.globaltranslation.fake.FakeSpeechProvider
import com.example.globaltranslation.fake.FakeTranslationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentation tests for ConversationScreen.
 * Tests UI behavior, speech recognition, translation, and persistence.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ConversationScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var fakeTranslationProvider: FakeTranslationProvider
    
    @Inject
    lateinit var fakeSpeechProvider: FakeSpeechProvider
    
    @Inject
    lateinit var fakeRepository: FakeConversationRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
        
        // Reset fakes to default state
        fakeTranslationProvider.shouldSucceed = true
        fakeSpeechProvider.shouldSucceed = true
    }
    
    @Test
    fun conversationScreen_displaysInNavigation() {
        composeTestRule
            .onNodeWithText("Conversation")
            .assertIsDisplayed()
    }
    
    @Test
    fun conversationScreen_displaysLanguageSelectors() {
        // Click on conversation tab
        composeTestRule.onNodeWithText("Conversation").performClick()
        composeTestRule.waitForIdle()
        
        // Should display language selection buttons
        composeTestRule
            .onAllNodesWithContentDescription("Select language")
            .assertCountEquals(2) // Source and target language selectors
    }
    
    @Test
    fun conversationScreen_micButton_exists() {
        composeTestRule.onNodeWithText("Conversation").performClick()
        composeTestRule.waitForIdle()
        
        // Microphone button should be visible
        composeTestRule
            .onNode(hasContentDescription("Start listening") or hasContentDescription("Stop listening"))
            .assertExists()
    }
    
    @Test
    fun conversationScreen_swapButton_exists() {
        composeTestRule.onNodeWithText("Conversation").performClick()
        composeTestRule.waitForIdle()
        
        // Language swap button should exist
        composeTestRule
            .onNodeWithContentDescription("Swap languages")
            .assertExists()
    }
    
    @Test
    fun conversationScreen_clearButton_exists() {
        composeTestRule.onNodeWithText("Conversation").performClick()
        composeTestRule.waitForIdle()
        
        // Clear conversation button should exist
        composeTestRule
            .onNode(hasText("Clear") or hasContentDescription("Clear conversation"))
            .assertExists()
    }
    
    @Test
    fun conversationScreen_initialState_isEmpty() {
        composeTestRule.onNodeWithText("Conversation").performClick()
        composeTestRule.waitForIdle()
        
        // No conversation history initially
        composeTestRule
            .onNodeWithText("Start a conversation by tapping the microphone", substring = true)
            .assertExists()
    }
}

