package ru.rtuitlab.itlab.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.repository.UsersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideRepository(usersApi: UsersApi, responseHandler: ResponseHandler) =
        UsersRepository(usersApi, responseHandler)
}