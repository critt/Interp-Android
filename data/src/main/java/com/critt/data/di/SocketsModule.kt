package com.critt.data.di

import com.critt.data.SessionManager
import com.critt.data.TranslationSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SocketsModule {
    @Provides
    @ViewModelScoped
    fun provideSocketsService(
        sessionManager: SessionManager
    ): TranslationSource =
        TranslationSource(sessionManager)
}