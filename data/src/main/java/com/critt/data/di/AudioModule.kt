package com.critt.data.di

import com.critt.data.AudioRecorderFactory
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
    fun provideAudioRecorderFactory(): AudioRecorderFactory =
        AudioRecorderFactory()
}