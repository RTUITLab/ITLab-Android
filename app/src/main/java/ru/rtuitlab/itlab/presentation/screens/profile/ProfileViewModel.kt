package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	eventsRepo: EventsRepository,
	authStateStorage: AuthStateStorage
) : UserViewModel(
	usersRepo,
	eventsRepo,
	runBlocking { authStateStorage.userIdFlow.first() }
) {

	fun editUserInfo(
		info: UserEditRequest,
		userResponse: UserResponse,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		usersRepo.editUserInfo(info).handle(
			onSuccess = {
				_userCredentialsFlow.value = Resource.Success(it.copy(properties = userResponse.properties))
				onFinish()
			},
			onError = {
				onFinish()
			}
		)
	}

	fun editUserProperty(
		id: String,
		value: String,
		credentials: UserResponse,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		usersRepo.editUserProperty(id, value).handle(
			onSuccess = { newProp ->
				_userCredentialsFlow.value = Resource.Success(
					credentials.copy(
						properties = credentials.properties.map {
							if (it.userPropertyType.id == newProp.userPropertyType.id) newProp
							else it
						}
					)
				)
				onFinish()
			},
			onError = {
				onFinish()
			}
		)
	}
}