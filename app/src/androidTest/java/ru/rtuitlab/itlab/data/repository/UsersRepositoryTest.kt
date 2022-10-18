package ru.rtuitlab.itlab.data.repository

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.rtuitlab.itlab.data.di.RepositoriesModule
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class)
class UsersRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    val testDispatcher = StandardTestDispatcher()

    @Inject
    lateinit var usersRepo: UsersRepository

    @Inject
    lateinit var db: AppDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        val dao = db.dao
        runTest {
            repeat(3) {
                dao.insertUser(
                    UserEntity(
                        id = it.toString(),
                        email = "example$it@test.test",
                        phoneNumber = "899${it}6665544",
                        firstName = "Whatever$it",
                        middleName = "Whatever$it",
                        lastName = "Whatever$it",
                    )
                )
            }
        }
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAllUsers_ReturnsFlowOf3() = runTest {
        val flow = usersRepo.getAllUsers().first()
        assertThat(flow.size).isEqualTo(3)
    }
}