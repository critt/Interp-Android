package com.critt.trandroidlator.data

import retrofit2.Response
import retrofit2.http.GET

interface LanguageSource {
    @GET("getSupportedLanguages")
    fun getSupportedLanguages(): Response<List<LanguageData>>
}

