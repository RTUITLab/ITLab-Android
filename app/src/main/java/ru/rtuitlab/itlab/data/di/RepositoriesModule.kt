package ru.rtuitlab.itlab.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.devices.DevicesApi
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.repository.DevicesRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideRepositoryCoroutineScope() =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Singleton
    @Provides
    fun provideRepository(usersApi: UsersApi, responseHandler: ResponseHandler, repoScope: CoroutineScope) =
        UsersRepository(usersApi, responseHandler, repoScope)
    @Singleton
    @Provides
    fun provideDevicesRepository( devicesApi: DevicesApi, responseHandler: ResponseHandler) =
        DevicesRepository(devicesApi, responseHandler)
}