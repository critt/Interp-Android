package com.critt.interp.ui

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

    val translationObject = MutableLiveData("")
    private val builderObject = StringBuilder()
    val translationSubject = MutableLiveData("")
    private val builderSubject = StringBuilder()

    var langSubject = MutableLiveData(defaultLangSubject)
    var langObject = MutableLiveData(defaultLangObject)

    private val _supportedLanguages = MutableStateFlow<ApiResult<List<LanguageData>>>(ApiResult.Loading)
    val supportedLanguages = _supportedLanguages.asStateFlow()

    var isConnected = MutableLiveData(false) //TODO: this makes no sense
    private var jobRecord: Job? = null
    var speakerCurr = Speaker.SUBJECT

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.getSupportedLanguages().collect {
                _supportedLanguages.value = it
            }
        }
    }

    fun connect(): Boolean {
        if (langSubject.value == null || langObject.value == null) {
            return false
        }

        isConnected.postValue(true)  //TODO: this makes no sense
        builderSubject.clear()
        builderObject.clear()

        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.connectSubject(
                langSubject.value!!.language,
                langObject.value!!.language
            )
                .collect {
                    Timber.d("speakerCurr: $speakerCurr")
                    if (it.isFinal) {
                        builderSubject.append(it.data)
                        translationSubject.postValue(builderSubject.toString())
                    } else {
                        translationSubject.postValue(builderSubject.toString() + it.data)
                    }
                }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            translationRepo.connectObject(langSubject.value!!.language, langObject.value!!.language)
                .collect {
                    Timber.d("speakerCurr: $speakerCurr")
                    if (it.isFinal) {
                        builderObject.append(it.data)
                        translationObject.postValue(builderObject.toString())
                    } else {
                        translationObject.postValue(builderObject.toString() + it.data)
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