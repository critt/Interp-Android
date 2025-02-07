package com.critt.trandroidlator.di

import com.critt.trandroidlator.data.SessionManager
import com.critt.trandroidlator.data.TranslationSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketsModule {
    @Provides
    @Singleton
    fun provideSocketsService(sessionManager: SessionManager): TranslationSource = TranslationSource(sessionManager)
}