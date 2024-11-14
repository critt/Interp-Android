package com.critt.trandroidlator.data

import kotlinx.serialization.Serializable

@Serializable
data class SpeechData(
    val text: String,
    val isFinal: Boolean
)
