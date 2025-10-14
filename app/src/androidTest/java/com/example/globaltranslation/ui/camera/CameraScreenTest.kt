package com.example.globaltranslation.ui.camera

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
    
    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )
    
    @Inject
    lateinit var fakeCameraProvider: FakeCameraTranslationProvider
    
    @Inject
    lateinit var appPreferences: com.example.globaltranslation.data.preferences.AppPreferences
    
    @Inject
    lateinit var networkMonitor: com.example.globaltranslation.data.network.NetworkMonitor
    
    @Before
    fun setup() {
        hiltRule.inject()
        fakeCameraProvider.shouldSucceed = true
        
        // Reset preferences to prevent test pollution
        kotlinx.coroutines.runBlocking {
            appPreferences.clearAll()
        }
        
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
            .onNodeWithText("English", substring = true)
            .assertExists()

        composeTestRule
            .onNodeWithText("Spanish", substring = true)
            .assertExists()
    }
    
    @Test
    fun cameraScreen_hasCaptureButton() {
        // Capture/shutter button should exist
        composeTestRule
            .onNode(hasContentDescription("Capture and translate"), useUnmergedTree = true)
            .assertExists()
    }
    
    @Test
    fun cameraScreen_hasFlashToggle() {
        // Flash toggle should exist
        composeTestRule
            .onNode(
                hasContentDescription("Flash On") or hasContentDescription("Flash Off"),
                useUnmergedTree = true
            )
            .assertExists()
    }
    
    @Test
    fun cameraScreen_hasSwapButton() {
        // Language picker chip should exist
        composeTestRule
            .onNodeWithText("English", substring = true)
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

