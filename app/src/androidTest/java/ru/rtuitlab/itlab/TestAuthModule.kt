package ru.rtuitlab.itlab

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.common.persistence.MockAuthStateStorage
import ru.rtuitlab.itlab.data.di.AuthModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthModule::class]
)
object TestAuthModule {
    @Singleton
    @Provides
    fun provideAuthStateStorage(): IAuthStateStorage = MockAuthStateStorage()@Singleton

    @Provides
    fun provideAuthService(@ApplicationContext context: Context) = AuthorizationService(context, AppAuthConfiguration.DEFAULT)
}