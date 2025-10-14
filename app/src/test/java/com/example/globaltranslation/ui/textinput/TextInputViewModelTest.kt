package com.example.globaltranslation.ui.textinput

import com.example.globaltranslation.testing.MainDispatcherRule
import com.example.globaltranslation.testing.fakes.FakeConversationRepository
import com.example.globaltranslation.testing.fakes.FakeTextToSpeechProvider
import com.example.globaltranslation.testing.fakes.FakeTranslationProvider
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextInputViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(
        translation: FakeTranslationProvider = FakeTranslationProvider(),
        tts: FakeTextToSpeechProvider = FakeTextToSpeechProvider(),
        repo: FakeConversationRepository = FakeConversationRepository()
    ): Triple<TextInputViewModel, FakeTranslationProvider, FakeConversationRepository> {
        val vm = TextInputViewModel(translation, tts, repo)
        return Triple(vm, translation, repo)
    }

    @Test
    fun translate_success_updates_state_and_saves() = runTest {
        val (vm, translation, repo) = buildVm()
        translation.translatedPrefix = "Hola"

        vm.updateInputText("Hello")
        vm.setSourceLanguage(TranslateLanguage.ENGLISH)
        vm.setTargetLanguage(TranslateLanguage.SPANISH)
        vm.translateText()

        advanceUntilIdle()

        val state = vm.uiState.first()
        assertFalse(state.isTranslating)
        assertEquals("Hola: Hello", state.currentTranslation?.translatedText)
        assertTrue(repo.saved.isNotEmpty())
    }

    @Test
    fun translate_empty_input_sets_error() = runTest {
        val (vm, _, _) = buildVm()
        vm.updateInputText("")
        vm.translateText()
        advanceUntilIdle()
        val state = vm.uiState.first()
        assertTrue(state.error?.contains("enter text", ignoreCase = true) == true)
    }

    @Test
    fun translate_invalid_language_pair_sets_error() = runTest {
        val (vm, _, _) = buildVm()
        vm.updateInputText("Hola")
        vm.setSourceLanguage(TranslateLanguage.SPANISH)
        vm.setTargetLanguage(TranslateLanguage.FRENCH)
        vm.translateText()
        advanceUntilIdle()
        val state = vm.uiState.first()
        assertTrue(state.error?.contains("English", ignoreCase = true) == true)
    }

    @Test
    fun copyToInput_text_only_updates_input() = runTest {
        val (vm, _, _) = buildVm()
        vm.copyToInput("Some text")
        val state = vm.uiState.first()
        assertEquals("Some text", state.inputText)
    }

    @Test
    fun swapLanguages_swaps_and_prefills_when_available() = runTest {
        val (vm, translation, _) = buildVm()
        translation.translatedPrefix = "Bonjour"
        vm.updateInputText("Hello")
        vm.setSourceLanguage(TranslateLanguage.ENGLISH)
        vm.setTargetLanguage(TranslateLanguage.FRENCH)
        vm.translateText()
        advanceUntilIdle()

        // Now swap
        vm.swapLanguages()
        val state = vm.uiState.first()
        assertEquals(TranslateLanguage.FRENCH, state.sourceLanguage)
        assertEquals(TranslateLanguage.ENGLISH, state.targetLanguage)
        // Prefilled with last translated text if available
        assertEquals("Bonjour: Hello", state.inputText)
    }
}
