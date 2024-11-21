package com.critt.trandroidlator.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val translationSource: TranslationSource,
    private val languageSource: LanguageSource
) {
    /** returns Flow<SpeechData>
     * this represents the translation of the subject language to the object language
     */
    fun connectObject(languageObject: String, languageSubject: String) =
        translationSource.connectObject(languageObject, languageSubject)

    /** returns Flow<SpeechData>
     * this represents the translation of the object language to the subject language
     */
    fun connectSubject(languageObject: String, languageSubject: String) =
        translationSource.connectSubject(languageObject, languageSubject)

    fun onData(subjectData: ByteArray?, objectData: ByteArray?) =
        translationSource.onData(subjectData, objectData)

    fun disconnect() {
        translationSource.disconnect()
    }

    /** returns Flow<ApiResult<List<LanguageData>>?>
     *  this represents all of the languages we support for voice transcription and translation
     */
    fun getSupportedLanguages(): Flow<ApiResult<List<LanguageData>>?> = toResultFlow { languageSource.getSupportedLanguages() }
}