package ru.rtuitlab.itlab.data.repository

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.rtuitlab.itlab.Constants
import ru.rtuitlab.itlab.Generator
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.di.ApiModule
import ru.rtuitlab.itlab.data.di.AuthModule
import ru.rtuitlab.itlab.data.di.RepositoriesModule
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyEditRequest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(RepositoriesModule::class, ApiModule::class, AuthModule::class)
class UsersRepositoryImplTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testDispatcher: TestDispatcher

    @Inject
    lateinit var usersRepo: UsersRepositoryImpl

    @Inject
    lateinit var db: AppDatabase



    private val propertyTypes = Generator.propertyTypes

    private fun makeProps(id: Int) = Array(3) {
        UserPropertyEntity(
            value = when(id % 3) {
                0 -> "vk.com/id$id"
                1 -> "discord.gg/id$id"
                2 -> "skype.com/id$id"
                else -> ""
            },
            typeId = propertyTypes[it % 3].id,
            userId = id.toString()
        )
    }.toList()

    private val users = Generator.users

    private val props = Generator.props

    private val propsWithTypes = Generator.propsWithTypes

    private val usersWithProps = Generator.usersWithProps

    @Before
    fun setUp() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        val dao = db.usersDao


        runBlocking {
            dao.insertAll(users)

            dao.insertPropertyTypes(propertyTypes)

            dao.insertProperties(props)
        }

    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun getPropertyTypes() = runTest {
        val types = usersRepo.getPropertyTypes()
        assertThat(types).containsExactlyElementsIn(propertyTypes)
    }

    @Test
    fun getProperties() = runTest {
        val props = usersRepo.getProperties()
        assertThat(props).containsExactlyElementsIn(this@UsersRepositoryImplTest.props)
    }

    @Test
    fun getPropertiesWithTypes() = runTest {
        val propsTypes = usersRepo.getPropertiesWithTypes()
        assertThat(propsTypes).containsExactlyElementsIn(propsWithTypes)
    }

    @Test
    fun getAllUsers() = runTest {
        val flow = usersRepo.getAllUsers()
        assertThat(flow.first()).containsExactlyElementsIn(usersWithProps)
    }

    @Test
    fun searchUsers() = runTest {
        val query = "3"
        val flow = usersRepo.searchUsers(query)
        assertThat(flow.first()).containsExactlyElementsIn(
            usersWithProps.filter {
                with(it.userEntity) {
                    "$firstName $middleName $lastName".contains(query, ignoreCase = true)
                }
            }
        )
    }

    @Test
    fun getUserById() = runTest {
        val randomUser = usersWithProps.random()
        val id = randomUser.userEntity.id
        assertThat(usersRepo.getUserById(id)).isEqualTo(randomUser)
    }

    @Test
    fun getCurrentUser() = runTest {
        val currentUser = usersRepo.getCurrentUser()
        assertThat(currentUser).isEqualTo(usersWithProps.find { it.userEntity.id == Constants.CURRENT_USER_ID.toString() })
    }

    @Test
    fun insertUser() = runTest {
        val user = UserEntity(
            id = "asdasd",
            email = "sda",
            phoneNumber = "8999434434",
            firstName = "Aboba",
            middleName = "Abobovich",
            lastName = "Abobov"
        )

        usersRepo.insertUser(user, emptyList())
        assertThat(usersRepo.getAllUsers().first()).contains(
            UserWithProperties(
                userEntity = user,
                properties = emptyList()
            )
        )
    }

    @Test
    fun editUserInfo() = runTest {
        val request = UserEditRequest(
            firstName = "Abobus",
            lastName = "Abobus",
            middleName = "Abobus"
        )

        assertThat(
            (usersRepo.editUserInfo(request) as Resource.Success).data
        ).isEqualTo(
            usersRepo.getCurrentUser().toUserResponse()
        )
    }

    @Test
    fun editUserProperty() = runTest {
        val request = UserPropertyEditRequest(
            id = "1",
            value = "Blah"
        )

        val result = (usersRepo.editUserProperty(
            propertyId = request.id,
            newValue = request.value
        ) as Resource.Success).data

        assertThat(result.value).isEqualTo(request.value)

    }

    @Test
    fun updateAllUsers() = runTest {
        val result = (usersRepo.updateAllUsers() as Resource.Success).data.map {
            it.toUserWithProperties()
        }

        assertThat(result).containsExactlyElementsIn(
            db.usersDao.getUsers().first()
        )
    }

    @Test
    fun updateUser() = runTest {
        val result = (usersRepo.updateUser("4") as Resource.Success).data

        assertThat(
            usersRepo.getUserById("4").toUserResponse()
        ).isEqualTo(result)
    }

    @Test
    fun deleteUser() = runTest {
        val user = usersWithProps.random()
        usersRepo.deleteUser(user.userEntity)
        assertThat(
            usersRepo.getAllUsers().first()
        ).doesNotContain(user)
    }
}