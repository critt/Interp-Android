package com.critt.data

import com.critt.domain.LanguageData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    private val languageSource: LanguageSource
) {
    /** returns Flow<ApiResult<List<LanguageData>>?>
     *  this represents all of the languages we support for voice transcription and translation
     */
    fun getSupportedLanguages(): Flow<ApiResult<List<LanguageData>>> = toResultFlow { languageSource.getSupportedLanguages() }
}