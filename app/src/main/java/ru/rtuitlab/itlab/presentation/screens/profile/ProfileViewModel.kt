package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEditRequest
import ru.rtuitlab.itlab.domain.use_cases.events.GetUserEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.UpdateUserEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.EditUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetCurrentUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.presentation.UserViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val editUser: EditUserUseCase,
	getUser: GetUserUseCase,
	getPropertyTypes: GetUserPropertyTypesUseCase,
	getCurrentUser: GetCurrentUserUseCase,
	getUserEvents: GetUserEventsUseCase,
	updateUserEvents: UpdateUserEventsUseCase,
	authStateStorage: IAuthStateStorage
) : UserViewModel(
	updateUserEvents,
	getPropertyTypes,
	getUser,
	getUserEvents,
    runBlocking { authStateStorage.userIdFlow.first() }
) {

	override val user = getCurrentUser().map {
		it?.toUser()
	}.stateIn(viewModelScope, SharingStarted.Eagerly, null)

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