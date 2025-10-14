package com.example.globaltranslation.ui.languages

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.globaltranslation.MainActivity
import com.example.globaltranslation.fake.FakeTranslationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentation tests for LanguageScreen.
 * Tests language model management UI.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LanguageScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var fakeTranslationProvider: FakeTranslationProvider
    
    @Inject
    lateinit var appPreferences: com.example.globaltranslation.data.preferences.AppPreferences
    
    @Inject
    lateinit var networkMonitor: com.example.globaltranslation.data.network.NetworkMonitor
    
    @Before
    fun setup() {
        hiltRule.inject()
        fakeTranslationProvider.modelsDownloaded = false
        
        // Reset preferences to default state to prevent test pollution
        kotlinx.coroutines.runBlocking {
            appPreferences.clearAll()
        }
        
        // Navigate to Languages tab
        composeTestRule.onNodeWithText("Languages").performClick()
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun languageScreen_displaysInNavigation() {
        composeTestRule
            .onNodeWithText("Languages")
            .assertIsDisplayed()
    }
    
    @Test
    fun languageScreen_displaysLanguageList() {
        // Should display multiple languages
        composeTestRule
            .onNodeWithText("English")
            .assertExists()
        
        composeTestRule
            .onNodeWithText("Spanish")
            .assertExists()
    }
    
    @Test
    fun languageScreen_loadsWithoutCrashing() {
        // Verify the screen loads successfully
        // Language management UI is displayed
        composeTestRule.waitForIdle()
        
        // Should have English language visible
        composeTestRule
            .onNodeWithText("English")
            .assertExists()
    }
    
    @Test
    fun languageScreen_hasRefreshButton() {
        // Refresh button should exist
        composeTestRule
            .onNodeWithTag("languages_refresh_btn")
            .assertExists()
    }
    
    @Test
    fun languageScreen_displaysTitle() {
        composeTestRule
            .onAllNodesWithText("Language Models")
            .onFirst()
            .assertExists()
    }

    @Test
    fun languageScreen_cellularToggle_updatesStatusText() {
        composeTestRule
            .onNodeWithText("Models require WiFi connection", substring = true)
            .assertExists()

        composeTestRule
            .onNodeWithTag("cellular_downloads_switch")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Models can download on mobile data", substring = true)
            .assertExists()
    }

    @Test
    fun languageScreen_spanishItem_hasDownloadButton() {
        composeTestRule
            .onNodeWithTag("languages_list")
            .performScrollToNode(hasTestTag("language_item_es"))

        composeTestRule
            .onNodeWithTag("download_language_es")
            .assertExists()
    }
}

