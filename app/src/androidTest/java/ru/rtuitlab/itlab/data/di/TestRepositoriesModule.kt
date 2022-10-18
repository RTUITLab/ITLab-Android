package ru.rtuitlab.itlab.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import ru.rtuitlab.itlab.data.local.AppDatabase
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object TestRepositoriesModule {
    @Provides
    fun provideRepositoryCoroutineScope() =
        CoroutineScope(StandardTestDispatcher() + SupervisorJob())

    @Provides
    @Singleton
    fun provideUsersDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        val dispatcher = StandardTestDispatcher()
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .allowMainThreadQueries()
            .build()
    }
}