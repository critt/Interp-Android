package com.critt.interp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.critt.data.ApiResult
import com.critt.data.AudioSource
import com.critt.data.LanguageRepository
import com.critt.domain.LanguageData
import com.critt.data.SessionManager
import com.critt.domain.Speaker
import com.critt.data.TranslationSource
import com.critt.domain.SpeechData
import com.critt.domain.defaultLangObject
import com.critt.domain.defaultLangSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
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

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            languageRepo.getSupportedLanguages().collect { res ->
                _supportedLanguages.update { res }
            }
        }

        viewModelScope.launch {
            translationSource.speechDataSubject.collect {
                it?.let {
                    onTextData(it, _translationSubject, builderSubject)
                }
            }
        }

        viewModelScope.launch {
            translationSource.speechDataObject.collect {
                it?.let {
                    onTextData(it, _translationObject, builderObject)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up sockets
        translationSource.disconnectAllSockets()
    }

    private fun onTextData(
        textData: SpeechData,
        translationState: MutableStateFlow<String>,
        builder: StringBuilder
    ) {
        if (textData.isFinal) {
            builder.append(textData.data)
            translationState.update { builder.toString() }
        } else {
            translationState.update { builder.toString() + textData.data }
        }
    }

    fun updateSpeaker(subjectSpeaking: Boolean) {
        when (subjectSpeaking) {
            true -> _speakerCurr.update { Speaker.SUBJECT }
            false -> _speakerCurr.update { Speaker.OBJECT }
        }
    }

    fun selectLangSubject(lang: LanguageData) {
        _langSubject.update { lang }
    }

    fun selectLangObject(lang: LanguageData) {
        _langObject.update { lang }
    }

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
                audioSource.startRecording(onData = ::onAudioData)
            } catch (e: Exception) {
                _streamingState.update {
                    AudioStreamingState.Error(
                        e.message ?: "audioSource job: unknown error"
                    )
                }
            }
        }.invokeOnCompletion { cause ->
            stopRecordingAndStreaming()
            when (cause) {
                null, is CancellationException -> _streamingState.update { AudioStreamingState.Idle }
                else -> _streamingState.update {
                    AudioStreamingState.Error(
                        cause.message ?: "audioSource job completion handler: unknown error"
                    )
                }
            }
        }

        _streamingState.update { AudioStreamingState.Streaming }
    }

    private fun stopRecordingAndStreaming() {
        audioSource.stopRecording()
        translationSource.disconnectAllSockets()
    }

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

sealed class AudioStreamingState {
    object Idle : AudioStreamingState()
    object Streaming : AudioStreamingState()
    data class Error(val message: String) : AudioStreamingState()
}