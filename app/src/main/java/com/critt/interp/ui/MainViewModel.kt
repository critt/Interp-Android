package com.critt.interp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.critt.data.ApiResult
import com.critt.data.AudioSource
import com.critt.domain.LanguageData
import com.critt.data.SessionManager
import com.critt.domain.Speaker
import com.critt.data.TranslationRepository
import com.critt.domain.defaultLangObject
import com.critt.domain.defaultLangSubject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val translationRepo: TranslationRepository,
    private val sessionManager: SessionManager,
    private val audioSource: AudioSource
) :
    ViewModel() {

    private val builderObject = StringBuilder()
    private val builderSubject = StringBuilder()

    // StateFlow for translations
    private val _translationSubject = MutableStateFlow("")
    val translationSubject = _translationSubject.asStateFlow()
    private val _translationObject = MutableStateFlow("")
    val translationObject = _translationObject.asStateFlow()

    // StateFlow for supported languages
    private val _supportedLanguages =
        MutableStateFlow<ApiResult<List<LanguageData>>>(ApiResult.Loading)
    val supportedLanguages = _supportedLanguages.asStateFlow()

    // Compose State for selected languages
    var langSubject by mutableStateOf(defaultLangSubject)
        private set
    var langObject by mutableStateOf(defaultLangObject)
        private set

    var isConnected = MutableLiveData(false) //TODO: this makes no sense
    private var jobRecord: Job? = null
    var speakerCurr = Speaker.SUBJECT

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.getSupportedLanguages().collect { res ->
                _supportedLanguages.update { res }
            }
        }
    }

    fun updateLangSubject(lang: LanguageData) {
        langSubject = lang
    }

    fun updateLangObject(lang: LanguageData) {
        langObject = lang
    }

    fun connect(): Boolean {
        isConnected.postValue(true)  //TODO: this makes no sense
        builderSubject.clear()
        builderObject.clear()

        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.connectSubject(
                langSubject.language,
                langObject.language
            )
                .collect { res ->
                    Timber.d("speakerCurr: $speakerCurr")
                    if (res.isFinal) {
                        builderSubject.append(res.data)
                        _translationSubject.update { builderSubject.toString() }
                    } else {
                       _translationSubject.update { builderSubject.toString() + res.data }
                    }
                }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.connectObject(langSubject.language, langObject.language)
                .collect { res ->
                    Timber.d("speakerCurr: $speakerCurr")
                    if (res.isFinal) {
                        builderObject.append(res.data)
                        _translationObject.update { builderObject.toString() }
                    } else {
                        _translationObject.update { builderObject.toString() + res.data }
                    }
                }
        }

        return true
    }

    fun startRecording() {
        if (jobRecord == null) {
            jobRecord = viewModelScope.launch(context = Dispatchers.IO) {
                audioSource.startRecording(::handleInput)
            }
        }
    }

    fun stopRecording() {
        audioSource.stopRecording()
        jobRecord?.cancel()
        jobRecord = null
    }

    fun handleInput(data: ByteArray) {
        if (speakerCurr == Speaker.SUBJECT) {
            translationRepo.onData(data, ByteArray(2048))
        } else {
            translationRepo.onData(ByteArray(2048), data)
        }
    }

    fun disconnect() {
        isConnected.postValue(false)//TODO: this makes no sense
        translationRepo.disconnect()
    }
}