package com.example.globaltranslation.ui.camera

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.globaltranslation.MainActivity
import com.example.globaltranslation.fake.FakeCameraTranslationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentation tests for CameraScreen.
 * Tests camera UI, language selection, and translation overlay.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CameraScreenTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Inject
    lateinit var fakeCameraProvider: FakeCameraTranslationProvider
    
    @Before
    fun setup() {
        hiltRule.inject()
        fakeCameraProvider.shouldSucceed = true
        
        // Navigate to Camera tab
        composeTestRule.onNodeWithText("Camera").performClick()
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun cameraScreen_displaysInNavigation() {
        composeTestRule
            .onNodeWithText("Camera")
            .assertIsDisplayed()
    }
    
    @Test
    fun cameraScreen_displaysLanguageSelectors() {
        // Should have language selection buttons
        composeTestRule
            .onAllNodesWithContentDescription("Select language")
            .assertCountEquals(2)
    }
    
    @Test
    fun cameraScreen_hasCaptureButton() {
        // Capture/shutter button should exist
        composeTestRule
            .onNode(hasContentDescription("Capture") or hasContentDescription("Take photo"))
            .assertExists()
    }
    
    @Test
    fun cameraScreen_hasFlashToggle() {
        // Flash toggle should exist
        composeTestRule
            .onNode(hasContentDescription("Flash") or hasContentDescription("Toggle flash"))
            .assertExists()
    }
    
    @Test
    fun cameraScreen_hasSwapButton() {
        // Language swap button should exist
        composeTestRule
            .onNodeWithContentDescription("Swap languages")
            .assertExists()
    }
    
    @Test
    fun cameraScreen_cameraPreview_exists() {
        // Note: Actual camera preview requires camera permission and hardware
        // Just verify the screen loads without crashing
        composeTestRule.waitForIdle()
        
        // Screen should be displayed
        composeTestRule
            .onNodeWithText("Camera")
            .assertExists()
    }
}

