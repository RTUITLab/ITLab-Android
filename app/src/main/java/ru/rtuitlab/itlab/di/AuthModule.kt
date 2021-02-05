package ru.rtuitlab.itlab.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthStateStorage(@ApplicationContext context: Context) = AuthStateStorage(context)

    @Singleton
    @Provides
    fun provideAuthService(@ApplicationContext context: Context) = AuthorizationService(context)
}