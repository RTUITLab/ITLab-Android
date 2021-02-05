package ru.rtuitlab.itlab.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.rtuitlab.itlab.repositories.UserRepository
import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UserApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideRepository(userApi: UserApi, responseHandler: ResponseHandler) =
        UserRepository(userApi, responseHandler)
}