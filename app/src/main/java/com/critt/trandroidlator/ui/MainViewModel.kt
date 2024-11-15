package com.critt.trandroidlator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.critt.trandroidlator.data.TranslationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TranslationRepository) : ViewModel() {

    fun getSupportedLanguages() = repository.getSupportedLanguages().asLiveData()

}