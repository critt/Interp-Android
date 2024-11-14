package com.critt.trandroidlator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.http.GET

interface LanguageSource {
    @GET("getSupportedLanguages")
    suspend fun getSupportedLanguages(): Response<List<LanguageData>>
}

sealed class ApiResult <out T> (val status: ApiStatus, val data: T?, val message:String?) {
    data class Success<out R>(val _data: R?): ApiResult<R>(
        status = ApiStatus.SUCCESS,
        data = _data,
        message = null
    )

    data class Error(val exception: String): ApiResult<Nothing>(
        status = ApiStatus.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): ApiResult<R>(
        status = ApiStatus.LOADING,
        data = _data,
        message = null
    )
}

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}

fun <T> toResultFlow(call: suspend () -> Response<T>?) : Flow<ApiResult<T>?> {
    return flow {
        emit(ApiResult.Loading(null, true))
        val c = call()  /* have to initialize the call method first*/
        c?.let {
            try {
                if (c.isSuccessful && c.body() != null) {
                    c.body()?.let {
                        emit(ApiResult.Success(it))
                    }
                } else {
                    c.errorBody()?.let {
                        emit(ApiResult.Error(it.string()))
                    }
                }
            }catch (e: Exception){
                emit(ApiResult.Error(e.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)
}