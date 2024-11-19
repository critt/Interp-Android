package com.critt.trandroidlator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.critt.trandroidlator.data.ApiResult
import com.critt.trandroidlator.data.LanguageData
import com.critt.trandroidlator.data.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TranslationRepository) :
    ViewModel() {

    val objectTranslation: MutableLiveData<String> = MutableLiveData<String>()
    val subjectTranslation: MutableLiveData<String> = MutableLiveData<String>()
    val supportedLanguages: LiveData<ApiResult<List<LanguageData>>?>
    var isConnected = false //TODO: this makes no sense

    init {
        supportedLanguages = repository.getSupportedLanguages().asLiveData()
    }

    var subjectLanguage = LanguageData("en", "English")
    var objectLanguage = LanguageData("de", "German")

    fun connect(languageObject: String, languageSubject: String) {
        isConnected = true //TODO: this makes no sense
        viewModelScope.launch(context = Dispatchers.IO) {
            repository.connectObject(languageObject, languageSubject).collect {
                objectTranslation.postValue(it.text)
            }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            repository.connectSubject(languageSubject, languageObject).collect {
                subjectTranslation.postValue(it.text)
            }
        }
    }

    fun disconnect() {
        isConnected = false //TODO: this makes no sense
        repository.disconnect()
    }
}