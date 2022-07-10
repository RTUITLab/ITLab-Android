package ru.rtuitlab.itlab.data.repository

import android.graphics.Bitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject

class UsersRepository @Inject constructor(
	private val usersApi: UsersApi,
	private val handler: ResponseHandler,
	private val authStateStorage: AuthStateStorage,
	private val coroutineScope: CoroutineScope,
	private val picasso: Picasso
) {

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
						var userList= arrayListOf<User>()
						it.forEach { user ->
							getGravatars(user).handle(
								onSuccess = { grav ->
									userList.add(user.toUser(grav))
								}
							)
						}
						_cachedUsersFlow.value = userList

					}
				)
			}
			.stateIn(coroutineScope, SharingStarted.Eagerly, Resource.Loading)
	}
	fun toMd5(text:String):String{
		val email = text.trim().lowercase(Locale.getDefault())
		val md = MessageDigest.getInstance("MD5")
		val hashInBytes = md.digest(email.toByteArray(StandardCharsets.UTF_8))
		val sb = StringBuilder()
		for (b in hashInBytes) {
			sb.append(String.format("%02x", b))
		}
		return sb.toString()
	}

	val currentUserFlow = authStateStorage.userIdFlow.map { userId ->
		cachedUsersFlow.value.find { it.id == userId }
	}.stateIn(coroutineScope, SharingStarted.Lazily, null)

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
	fun getGravatars(user:UserResponse) = runBlocking(Dispatchers.IO) {
		handler{
			lateinit var grav: Bitmap
			if(user.email!=null){
				grav = picasso.load("https://www.gravatar.com/avatar/"+toMd5(user.email)+"?s=800").get()
			}else{
				grav = picasso.load("https://www.gravatar.com/avatar/"+toMd5("default")+"?s=800").get()

			}
			grav
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