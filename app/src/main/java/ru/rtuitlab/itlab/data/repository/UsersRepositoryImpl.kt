package ru.rtuitlab.itlab.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.users.models.PropertyWithType
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel
import ru.rtuitlab.itlab.data.repository.util.tryUpdate
import ru.rtuitlab.itlab.domain.repository.UsersRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val authStateStorage: IAuthStateStorage,
    private val usersApi: UsersApi,
    private val handler: ResponseHandler,
    private val coroutineScope: CoroutineScope,
    db: AppDatabase
): UsersRepositoryInterface {

    private val usersDao = db.usersDao

    init {
        coroutineScope.launch {
            updateAllUsers()
        }
    }

    override fun getAllUsers() = usersDao.getUsers()

    override fun searchUsers(query: String) = usersDao.searchUsers(query)

    override suspend fun getUserById(id: String) = usersDao.getUserById(id)

    override fun observeUserById(id: String) = usersDao.observeUserById(id)

    override suspend fun getCurrentUser() = usersDao.getUserById(
        id = authStateStorage.userIdFlow.first()
    )

    override fun observeCurrentUser() = usersDao.observeUserById(
        id = runBlocking { authStateStorage.userIdFlow.first() }
    )

    override suspend fun getPropertyTypes(): List<UserPropertyTypeModel> =
        usersDao.getPropertyTypes()

    override fun observePropertyTypes() = usersDao.observePropertyTypes()

    override suspend fun getProperties(): List<UserPropertyEntity> =
        usersDao.getProperties()

    override suspend fun getPropertiesWithTypes(): List<PropertyWithType> =
        usersDao.getPropertiesWithTypes()

    override suspend fun insertUser(
        user: UserEntity,
        properties: List<UserPropertyEntity>
    ) = usersDao.upsertUser(user, properties)

    override suspend fun fetchUserInfo(url: String, accessToken: String) = handler {
        usersApi.getUserInfo(url, "Bearer $accessToken")
    }

    override suspend fun editUserInfo(info: UserEditRequest) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { usersApi.editUserInfo(info) },
        into = { usersDao.updateUser(it.toUserEntity()) }
    )

    override suspend fun editUserProperty(
        propertyId: String,
        newValue: String
    ) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = {
            usersApi.editUserProperty(
                UserPropertyEditRequest(propertyId, newValue)
            )
        },
        into = { prop ->
            getCurrentUser()?.let {
                usersDao.updateUserProperty(
                    UserPropertyEntity(
                        userId = it.userEntity.id,
                        typeId = prop.userPropertyType.id,
                        value = prop.value,
                        status = prop.status
                    )
                )
            }
        }
    )

    override suspend fun updateAllUsers() = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { usersApi.getUsers() },
        into = {
            updatePropertyTypes()
            val usersWithProperties = it.map { it.toUserWithProperties() }
            usersDao.upsertAll(
                users = usersWithProperties.map { it.userEntity },
                properties = usersWithProperties.flatMap {
                    it.properties.map {
                        it.property
                    }
                }
            )
        }
    )

    override suspend fun updatePropertyTypes() = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { usersApi.getPropertyTypes() },
        into = { usersDao.upsertPropertyTypes(it) }
    )

    override suspend fun updateUser(id: String) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { usersApi.getUser(id) },
        into = {
            val userWithProperties = it.toUserWithProperties()
            usersDao.upsertUser(
                user = userWithProperties.userEntity,
                properties = userWithProperties.properties.map {
                    it.property
                }
            )
        }
    )

    override suspend fun deleteUser(
        user: UserEntity
    ) = usersDao.deleteUser(user)

}