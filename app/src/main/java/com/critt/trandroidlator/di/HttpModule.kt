package com.critt.trandroidlator.di


import com.critt.trandroidlator.BuildConfig
import com.critt.trandroidlator.data.LanguageSource
import com.critt.trandroidlator.data.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpModule {
    @Provides
    fun provideBaseUrl() = BuildConfig.API_BASE_URL

    @Provides
    @Singleton
    fun provideHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${sessionManager.getAuthToken()}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, httpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideLanguageSource(retrofit: Retrofit): LanguageSource = retrofit.create(LanguageSource::class.java)
}