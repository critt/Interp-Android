package com.critt.trandroidlator.di


import com.critt.trandroidlator.BuildConfig
import com.critt.trandroidlator.data.AudioSource
import com.critt.trandroidlator.data.LanguageSource
import com.critt.trandroidlator.data.TranslationSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideBaseUrl() = BuildConfig.API_BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideRESTService(retrofit: Retrofit): LanguageSource = retrofit.create(LanguageSource::class.java)

    @Provides
    @Singleton
    fun provideSocketsService(): TranslationSource = TranslationSource()

    @Provides
    @Singleton
    fun provideAudioSource(): AudioSource = AudioSource()
}