package ru.rtuitlab.itlab.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.repository.EventsRepositoryImpl
import ru.rtuitlab.itlab.data.repository.ReportsRepositoryImpl
import ru.rtuitlab.itlab.data.repository.UsersRepositoryImpl
import ru.rtuitlab.itlab.domain.repository.EventsRepositoryInterface
import ru.rtuitlab.itlab.domain.repository.ReportsRepositoryInterface
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    fun provideRepositoryCoroutineScope() =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.NAME
    ).build()

    @Provides
    @Singleton
    fun provideUsersRepository(
        usersRepo: UsersRepositoryImpl
    ) = usersRepo as UsersRepositoryInterface

    @Provides
    @Singleton
    fun provideEventsRepository(
        eventsRepo: EventsRepositoryImpl
    ) = eventsRepo as EventsRepositoryInterface

    @Provides
    @Singleton
    fun provideReportsRepository(
        reportsRepo: ReportsRepositoryImpl
    ) = reportsRepo as ReportsRepositoryInterface
}