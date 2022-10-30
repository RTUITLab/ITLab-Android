package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.domain.use_cases.users.EditUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val editUser: EditUserUseCase,
	eventsRepo: EventsRepository,
	getUser: GetUserUseCase,
	getPropertyTypes: GetUserPropertyTypesUseCase,
	authStateStorage: IAuthStateStorage
) : UserViewModel(
	eventsRepo,
	getUser,
	getPropertyTypes,
	runBlocking { authStateStorage.userIdFlow.first() }
) {

	fun editUserInfo(
		info: UserEditRequest,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		editUser.info(info).handle(
			onSuccess = {onFinish()},
			onError = {onFinish()},
		)
	}

	fun editUserProperty(
		id: String,
		value: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		editUser.property(
			propertyId = id,
			newValue = value
		)
		onFinish()
	}
}