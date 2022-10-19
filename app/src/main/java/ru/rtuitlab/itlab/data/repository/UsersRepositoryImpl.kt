package ru.rtuitlab.itlab.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.rtuitlab.itlab.common.Resource
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

    private val usersDao = db.dao

    override fun getAllUsers() = usersDao.getUsers()

    override fun searchUsers(query: String) = usersDao.searchUsers(query)

    override suspend fun getUserById(id: String) = usersDao.getUserById(id)

    override suspend fun getCurrentUser() = usersDao.getUserById(
        id = authStateStorage.userIdFlow.first()
    )

    override suspend fun getPropertyTypes(): List<UserPropertyTypeModel> =
        usersDao.getPropertyTypes()

    override suspend fun getProperties(): List<UserPropertyEntity> =
        usersDao.getProperties()

    override suspend fun getPropertiesWithTypes(): List<PropertyWithType> =
        usersDao.getPropertiesWithTypes()

    override suspend fun insertUser(
        user: UserEntity,
        properties: List<UserPropertyEntity>
    ) = usersDao.insertUser(user, properties)

    /*override suspend fun editUserInfo(info: UserEditRequest): Resource<UserResponse> {
        var resource: Resource<UserResponse> = Resource.Loading

        handler { usersApi.editUserInfo(info) }.handle(
            onSuccess = {
                resource = Resource.Success(it)
                usersDao.updateUser(it.toUserEntity())
            },
            onError = {
                resource = Resource.Error(it)
            }
        )

        return resource
    }*/

    override suspend fun editUserInfo(info: UserEditRequest) = tryUpdate(
        from = { usersApi.editUserInfo(info) },
        into = { usersDao.updateUser(it.toUserEntity()) }
    )


    /**
     * This method performs a specified [from] function, typically an API request,
     * and if successful, calls [into], typically a DB write, with result of the
     * [from] function as an argument
     * @return [Resource] indicating the status of the result
     */
    private suspend fun <T> tryUpdate(
        from: suspend () -> T,
        into: suspend (T) -> Unit
    ): Resource<T> {
        var resource: Resource<T> = Resource.Loading

        // Using SupervisorScope to ensure this request completes even if
        // parent coroutine scope is cancelled
        withContext(coroutineScope.coroutineContext) {
            handler { from() }.handle(
                onSuccess = {
                    resource = Resource.Success(it)
                    into(it)
                },
                onError = {
                    resource = Resource.Error(it)
                }
            )
        }

        return resource
    }

    /*override suspend fun editUserProperty(
        propertyId: String,
        newValue: String
    ): Resource<UserPropertyModel> {
        var resource: Resource<UserPropertyModel> = Resource.Loading

        handler { usersApi.editUserProperty(
            UserPropertyEditRequest(propertyId, newValue)
        ) }.handle(
            onSuccess = {
                resource = Resource.Success(it)
                usersDao.updateUserProperty(
                    UserPropertyEntity(
                        userId = getCurrentUser().userEntity.id,

                    )
                )
            }
        )
    }*/

    override suspend fun editUserProperty(
        propertyId: String,
        newValue: String
    ) = tryUpdate(
        from = {
            usersApi.editUserProperty(
                UserPropertyEditRequest(propertyId, newValue)
            )
        },
        into = {
            usersDao.updateUserProperty(
                UserPropertyEntity(
                    userId = getCurrentUser().userEntity.id,
                    typeId = it.userPropertyType.id,
                    value = it.value,
                    status = it.status
                )
            )
        }
    )

    /*override suspend fun updateAllUsers(): Resource<List<UserResponse>> {
        var resource: Resource<List<UserResponse>> = Resource.Loading

        // Using SupervisorScope to ensure this request completes even if
        // parent coroutine scope is cancelled
        withContext(coroutineScope.coroutineContext) {
            handler { usersApi.getUsers() }.handle(
                onSuccess = {
                    resource = Resource.Success(it)
                    val usersWithProperties = it.map { it.toUserWithProperties() }
                    usersDao.insertAll(
                        users = usersWithProperties.map { it.userEntity },
                        properties = usersWithProperties.flatMap {
                            it.properties.map {
                                it.property
                            }
                        }
                    )
                },
                onError = {
                    resource = Resource.Error(it)
                }
            )
        }

        return resource
    }*/

    override suspend fun updateAllUsers() = tryUpdate(
        from = { usersApi.getUsers() },
        into = {
            val usersWithProperties = it.map { it.toUserWithProperties() }
            usersDao.insertAll(
                users = usersWithProperties.map { it.userEntity },
                properties = usersWithProperties.flatMap {
                    it.properties.map {
                        it.property
                    }
                }
            )
        }
    )

    /*override suspend fun updateUser(id: String): Resource<UserResponse> {
        var resource: Resource<UserResponse> = Resource.Loading

        withContext(coroutineScope.coroutineContext) {
            handler { usersApi.getUser(id) }.handle(
                onSuccess = {
                    val userWithProperties = it.toUserWithProperties()
                    resource = Resource.Success(it)
                    usersDao.insertUser(
                        user = userWithProperties.userEntity,
                        properties = userWithProperties.properties.map {
                            it.property
                        }
                    )
                },
                onError = {
                    resource = Resource.Error(it)
                }
            )
        }

        return resource
    }*/

    override suspend fun updateUser(id: String) = tryUpdate(
        from = { usersApi.getUser(id) },
        into = {
            val userWithProperties = it.toUserWithProperties()
            usersDao.insertUser(
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