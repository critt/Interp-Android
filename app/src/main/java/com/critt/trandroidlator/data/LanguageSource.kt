package com.critt.trandroidlator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.http.GET
import timber.log.Timber

interface LanguageSource {
    @GET("getSupportedLanguages")
    suspend fun getSupportedLanguages(): Response<List<LanguageData>>
}

sealed class ApiResult <out T> {
    data class Success<out R>(val data: R?): ApiResult<R>()
    data class Error(val message: String): ApiResult<Nothing>()
    object Loading: ApiResult<Nothing>()
}

fun <T> toResultFlow(call: suspend () -> Response<T>?): Flow<ApiResult<T>?> {
    return flow {
        emit(ApiResult.Loading)

        try {
            val c = call()
            c?.let {
                if (c.isSuccessful) {
                    emit(ApiResult.Success(c.body()))
                    Timber.d("Success: ${c.body()}")
                } else {
                    c.errorBody()?.let {
                        val error = it.string()
                        it.close()
                        emit(ApiResult.Error(error))
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }

    }.flowOn(Dispatchers.IO)
}