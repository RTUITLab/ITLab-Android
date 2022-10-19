package ru.rtuitlab.itlab

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import ru.rtuitlab.itlab.data.di.RepositoriesModule
import ru.rtuitlab.itlab.data.local.AppDatabase
import java.util.concurrent.Executors
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoriesModule::class]
)
object TestRepositoriesModule {

    @Provides
    @Singleton
    fun provideTestDispatcher() = StandardTestDispatcher()

    @Provides
    fun provideRepositoryCoroutineScope(
        dispatcher: TestDispatcher
    ) =
        CoroutineScope(dispatcher + SupervisorJob())

    @Provides
    @Singleton
    fun provideUsersDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
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