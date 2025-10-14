package com.example.globaltranslation.data.provider

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.globaltranslation.data.preferences.LanguageModelPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for MlKitTranslationProvider.
 * Tests input validation logic in real Android environment.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MlKitTranslationProviderTest {

    private lateinit var languageModelPreferences: LanguageModelPreferences
    private lateinit var provider: MlKitTranslationProvider

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        languageModelPreferences = LanguageModelPreferences(context)
        provider = MlKitTranslationProvider(languageModelPreferences)
    }

    @Test
    fun translate_with_empty_text_returns_failure() = runTest {
        val result = provider.translate("", "en", "es")
        
        assertTrue("Empty text should fail translation", result.isFailure)
        assertTrue("Should throw IllegalArgumentException", result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Text cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun translate_with_blank_text_returns_failure() = runTest {
        val result = provider.translate("   ", "en", "es")
        
        assertTrue("Blank text should fail translation", result.isFailure)
        assertTrue("Should throw IllegalArgumentException", result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun cleanup_does_not_throw_exceptions() {
        // Verify cleanup method can be called safely
        try {
            provider.cleanup()
            // If we get here, no exception was thrown - success
        } catch (e: Exception) {
            fail("Cleanup should not throw exceptions but got: ${e.message}")
        }
    }
}