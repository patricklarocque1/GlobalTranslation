package com.example.globaltranslation.ui.components

import com.example.globaltranslation.model.ConversationTurn
import com.google.mlkit.nl.translate.TranslateLanguage

val previewLanguages = listOf(
    TranslateLanguage.ENGLISH,
    TranslateLanguage.SPANISH,
    TranslateLanguage.FRENCH,
    TranslateLanguage.GERMAN,
    TranslateLanguage.ITALIAN,
    TranslateLanguage.PORTUGUESE,
    TranslateLanguage.CHINESE,
    TranslateLanguage.JAPANESE,
    TranslateLanguage.KOREAN,
    TranslateLanguage.RUSSIAN,
    TranslateLanguage.ARABIC,
    TranslateLanguage.HINDI
)

val previewConversations = listOf(
    ConversationTurn("Hello", "Hola", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH),
    ConversationTurn("How are you?", "¿Cómo estás?", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH),
    ConversationTurn("Good morning", "Buenos días", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH),
    ConversationTurn("Where is the station?", "¿Dónde está la estación?", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH),
    ConversationTurn("Thanks", "Gracias", TranslateLanguage.ENGLISH, TranslateLanguage.SPANISH)
)
