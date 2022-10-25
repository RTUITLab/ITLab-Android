package ru.rtuitlab.itlab.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(
	private val usersApi: UsersApi,
	private val handler: ResponseHandler,
	private val authStateStorage: IAuthStateStorage,
	private val coroutineScope: CoroutineScope,
	db: AppDatabase
) {

	private val dao = db.usersDao

	private val _cachedUsersFlow = MutableStateFlow<List<User>>(emptyList())
	val cachedUsersFlow = _cachedUsersFlow.asStateFlow()

	private val _usersResponsesFlow = MutableStateFlow<Resource<List<UserResponse>>>(Resource.Empty)

	/**
	 * This is an intentional memory leak that allows caching users throughout the entire lifetime of the app.
	 * Downstream flows that ViewModels create should be transformations of this flow.
	 * For re-fetching users use [updateUsersFlow]
	 */
	val usersResponsesFlow by lazy {
		updateUsersFlow()
		_usersResponsesFlow.asStateFlow()
			.onEach {
				it.handle(
					onSuccess = {
						_cachedUsersFlow.value = it.map { it.toUser() }
					},
					onError = {
						updateUsersFlow()
					}
				)
			}
			.stateIn(coroutineScope, SharingStarted.Eagerly, Resource.Loading)
	}


	val currentUserFlow = authStateStorage.userIdFlow.map { userId ->
		cachedUsersFlow.value.find { it.id == userId }
	}.stateIn(coroutineScope, SharingStarted.Lazily, null)

	fun getAllUsers() = dao.getUsers()

	suspend fun fetchUserInfo(url: String, accessToken: String) = handler {
		usersApi.getUserInfo(url, "Bearer $accessToken")
	}

	suspend fun fetchUserCredentials(userId: String) = handler {
		usersApi.getUser(userId)
	}

	suspend fun fetchUserDevices(userId: String) = handler {
		usersApi.getUserDevices(userId)
	}

	suspend fun fetchUserEvents(userId: String, beginTime: String, endTime: String) = handler {
		usersApi.getUserEvents(userId, beginTime, endTime)
	}

	suspend fun fetchUsers() = handler {
		usersApi.getUsers()
	}

	fun updateUsersFlow() = _usersResponsesFlow.emitInIO(coroutineScope) {
		handler {
			usersApi.getUsers()
		}
	}

	suspend fun editUserInfo(info: UserEditRequest) = handler {
		usersApi.editUserInfo(info)
	}

	suspend fun fetchPropertyTypes() = handler {
		usersApi.getPropertyTypes()
	}

	suspend fun editUserProperty(id: String, value: String) = handler {
		usersApi.editUserProperty(
			UserPropertyEditRequest(id, value)
		)
	}

	suspend fun getUserById(userId:String) = handler {
		usersApi.getUser(userId)
	}
}