package com.critt.trandroidlator.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.critt.trandroidlator.data.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TranslationRepository) : ViewModel() {

    val objectTranslation : MutableLiveData<String> = MutableLiveData<String>()
    val subjectTranslation : MutableLiveData<String> = MutableLiveData<String>()
    fun getSupportedLanguages() = repository.getSupportedLanguages().asLiveData()

    fun connect(languageObject: String, languageSubject: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            repository.connectObject(languageObject, languageSubject).collect {
                objectTranslation.postValue(it.text)
            }

            repository.connectSubject(languageSubject, languageObject).collect {
                subjectTranslation.postValue(it.text)
            }
        }
    }
}