package com.critt.interp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.critt.data.ApiResult
import com.critt.data.AudioSource
import com.critt.data.LanguageRepository
import com.critt.domain.LanguageData
import com.critt.domain.Speaker
import com.critt.data.TranslationSource
import com.critt.domain.SpeechData
import com.critt.domain.defaultLangObject
import com.critt.domain.defaultLangSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioSource: AudioSource,
    private val translationSource: TranslationSource,
    private val languageRepo: LanguageRepository
) : ViewModel() {
    // Supported languages state
    private val _supportedLanguages =
        MutableStateFlow<ApiResult<List<LanguageData>>>(ApiResult.Loading)
    val supportedLanguages = _supportedLanguages.asStateFlow()

    // Current speaker state
    private val _speakerCurr = MutableStateFlow(Speaker.OBJECT)
    val speakerCurr = _speakerCurr.asStateFlow()

    // Translation output state
    private val _translationSubject = MutableStateFlow("")
    val translationSubject = _translationSubject.asStateFlow()
    private val _translationObject = MutableStateFlow("")
    val translationObject = _translationObject.asStateFlow()
    private val builderObject = StringBuilder()
    private val builderSubject = StringBuilder()

    // Selected languages state
    private val _langSubject = MutableStateFlow(defaultLangSubject)
    val langSubject = _langSubject.asStateFlow()
    private val _langObject = MutableStateFlow(defaultLangObject)
    val langObject = _langObject.asStateFlow()

    // Streaming state
    private val _streamingState = MutableStateFlow<AudioStreamingState>(AudioStreamingState.Idle)
    val streamingState = _streamingState.asStateFlow()

    // onAudioData lambdas
    private var onAudioDataSubject: (ByteArray?) -> Unit = {}
    private var onAudioDataObject: (ByteArray?) -> Unit = {}

    /**
     * Initializes the [MainViewModel].
     *
     * It fetches the supported languages from the [LanguageRepository] and
     * sets up coroutines to collect translated text from the [TranslationSource].
     */
    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            languageRepo.getSupportedLanguages().collect { res ->
                _supportedLanguages.update { res }
            }
        }

        viewModelScope.launch {
            translationSource.speechDataSubject.collect {
                it?.let {
                    onTextData(
                        it,
                        _translationSubject,
                        builderSubject,
                        Locale(langObject.value.language)
                    )
                }
            }
        }

        viewModelScope.launch {
            translationSource.speechDataObject.collect {
                it?.let {
                    onTextData(
                        it,
                        _translationObject,
                        builderObject,
                        Locale(langSubject.value.language)
                    )
                }
            }
        }
    }

    /**
     * Cleans up resources when the ViewModel is cleared.
     *
     * It disconnects all sockets from the [translationSource].
     */
    override fun onCleared() {
        super.onCleared()
        // Clean up sockets
        translationSource.disconnectAllSockets()
    }

    /**
     * Processes incoming text data and updates the corresponding translation state.
     *
     * @param textData The incoming speech data.
     * @param translationState The state flow to update with the translated text.
     * @param builder The StringBuilder to accumulate the translated text.
     * @param locale The locale to use for capitalization.
     */
    private fun onTextData(
        textData: SpeechData,
        translationState: MutableStateFlow<String>,
        builder: StringBuilder,
        locale: Locale
    ) {
        if (textData.isFinal) {
            builder.append(textData.data)
            translationState.update {
                builder.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            }
        } else {
            translationState.update { builder.toString() + textData.data }
        }
    }

    /**
     * Updates the current speaker.
     *
     * @param subjectSpeaking True if the SUBJECT speaker is speaking, false otherwise.
     */
    fun updateSpeaker(subjectSpeaking: Boolean) {
        when (subjectSpeaking) {
            true -> _speakerCurr.update { Speaker.SUBJECT }
            false -> _speakerCurr.update { Speaker.OBJECT }
        }
    }

    /**
     * Selects a language for the SUBJECT speaker.
     *
     * @param lang The selected language.
     */
    fun selectLangSubject(lang: LanguageData) {
        _langSubject.update { lang }
    }

    /**
     * Selects a language for the OBJECT speaker.
     *
     * @param lang The selected language.
     */
    fun selectLangObject(lang: LanguageData) {
        _langObject.update { lang }
    }

    /**
     * Toggles the audio streaming state.
     *
     * It starts or stops recording and streaming based on the current state.
     */
    fun toggleStreaming() {
        when (_streamingState.value) {
            is AudioStreamingState.Idle -> startRecordingAndStreaming()
            is AudioStreamingState.Streaming -> {
                stopRecordingAndStreaming()
                _streamingState.update { AudioStreamingState.Idle }
            }

            is AudioStreamingState.Error -> {
                stopRecordingAndStreaming()
                startRecordingAndStreaming()
            }
        }
    }

    /**
     * Starts recording and streaming audio.
     *
     * It clears the translation output StringBuilders, opens socket connections,
     * and starts the audio recording process.
     */
    private fun startRecordingAndStreaming() {
        // clear the translation output StringBuilders
        builderSubject.clear()
        builderObject.clear()

        // open socket connection on the "subject" namespace
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                onAudioDataSubject = translationSource.connect(
                    languageSubject = langSubject.value.language,
                    languageObject = langObject.value.language,
                    Speaker.SUBJECT
                )
            } catch (e: Exception) {
                Timber.e(e.message)
                stopRecordingAndStreaming()
                _streamingState.update {
                    AudioStreamingState.Error(
                        e.message ?: "subject socket: unknown error"
                    )
                }
            }
        }

        // open socket connection on the "object" namespace
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                onAudioDataObject = translationSource.connect(
                    languageSubject = langSubject.value.language,
                    languageObject = langObject.value.language,
                    Speaker.OBJECT
                )
            } catch (e: Exception) {
                Timber.e(e.message)
                stopRecordingAndStreaming()
                _streamingState.update {
                    AudioStreamingState.Error(
                        e.message ?: "object socket: unknown error"
                    )
                }
            }
        }

        // start recording
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                audioSource.startRecording(viewModelScope, onData = ::onAudioData)
            } catch (e: Exception) {
                Timber.e(e.message)
                stopRecordingAndStreaming()
                _streamingState.update {
                    AudioStreamingState.Error(
                        e.message ?: "audioSource job: unknown error"
                    )
                }
            }
        }

        _streamingState.update { AudioStreamingState.Streaming }
    }

    /**
     * Stops the audio recording and disconnects all sockets.
     *
     * This function is responsible for cleaning up resources related to audio
     * streaming and translation. It stops the audio recording process and
     * disconnects all active socket connections.
     *
     * This function should be called when audio streaming is no longer needed,
     * or when an error occurs that requires stopping the streaming process.
     */
    private fun stopRecordingAndStreaming() {
        audioSource.stopRecording()
        translationSource.disconnectAllSockets()
    }

    /**
     * Processes incoming audio data and routes it to the appropriate speaker's
     * audio data handler.
     *
     * This function is called when new audio data is available from the
     * [AudioSource]. It determines the current speaker based on the
     * [speakerCurr] state and then calls the corresponding `onAudioData`
     * lambda function to handle the data.
     *
     * If the current speaker is [Speaker.SUBJECT], the data is sent to
     * [onAudioDataSubject], and an empty byte array is sent to
     * [onAudioDataObject].
     *
     * If the current speaker is [Speaker.OBJECT], the data is sent to
     * [onAudioDataObject], and an empty byte array is sent to
     * [onAudioDataSubject].
     *
     * @param data The incoming audio data as a ByteArray.
     */
    fun onAudioData(data: ByteArray) {
        when (speakerCurr.value) {
            Speaker.SUBJECT -> {
                onAudioDataSubject(data)
                onAudioDataObject(ByteArray(2048))
            }

            Speaker.OBJECT -> {
                onAudioDataSubject(ByteArray(2048))
                onAudioDataObject(data)
            }
        }
    }
}

/**
 * Represents the different states of audio streaming.
 *
 * This sealed class is used to represent the possible states of the audio
 * streaming process within the application. It can be in one of three states:
 * [Idle], [Streaming], or [Error].
 */
sealed class AudioStreamingState {
    /**
     * Represents the idle state of audio streaming.
     *
     * This state indicates that audio streaming is not currently active.
     * No audio is being recorded or streamed.
     */
    object Idle : AudioStreamingState()

    /**
     * Represents the streaming state of audio streaming.
     *
     * This state indicates that audio is currently being recorded and streamed.
     */
    object Streaming : AudioStreamingState()

    /**
     * Represents the error state of audio streaming.
     *
     * This state indicates that an error has occurred during the audio
     * streaming process. The [message] property contains a description of
     * the error.
     *
     * @property message A description of the error that occurred.
     */
    data class Error(val message: String) : AudioStreamingState()
}