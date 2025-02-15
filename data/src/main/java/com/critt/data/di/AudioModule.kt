package com.critt.data.di

import com.critt.data.AudioSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    @Provides
    @Singleton
    fun provideAudioSource(): AudioSource =
        AudioSource()
}