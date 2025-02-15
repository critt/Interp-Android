package com.critt.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val translationSource: TranslationSource,
    private val languageSource: LanguageSource
) {
    /** returns Flow<SpeechData>
     * this represents the translation of the subject language to the object language
     */
    fun connectObject(languageSubject: String, languageObject: String) =
        translationSource.connectObject(languageSubject, languageObject)

    /** returns Flow<SpeechData>
     * this represents the translation of the object language to the subject language
     */
    fun connectSubject(languageSubject: String, languageObject: String) =
        translationSource.connectSubject(languageSubject, languageObject)

    fun onData(subjectData: ByteArray?, objectData: ByteArray?) =
        translationSource.onData(subjectData, objectData)

    fun disconnect() {
        translationSource.disconnect()
    }

    /** returns Flow<ApiResult<List<LanguageData>>?>
     *  this represents all of the languages we support for voice transcription and translation
     */
    fun getSupportedLanguages(): Flow<ApiResult<List<com.critt.domain.LanguageData>>?> = toResultFlow { languageSource.getSupportedLanguages() }
}