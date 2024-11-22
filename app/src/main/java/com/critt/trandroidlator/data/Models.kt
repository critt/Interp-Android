package com.critt.trandroidlator.data

import kotlinx.serialization.Serializable

enum class Speaker {
    OBJECT,
    SUBJECT
}

@Serializable
data class SpeechData(
    val data: String,
    val isFinal: Boolean
)
@Serializable
data class LanguageData(
    val language: String,
    val name: String
)

val defaultLangObject = LanguageData("de", "German")
val defaultLangSubject = LanguageData("en", "English")