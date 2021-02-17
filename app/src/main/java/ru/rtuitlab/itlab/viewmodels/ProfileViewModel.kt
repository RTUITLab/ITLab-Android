package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.api.users.models.UserEventModel
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.UsersRepository
import ru.rtuitlab.itlab.utils.emitInIO
import ru.rtuitlab.itlab.utils.minus
import ru.rtuitlab.itlab.utils.toMoscowDateTime
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	private val usersRepo: UsersRepository,
	private val authStateStorage: AuthStateStorage
) : ViewModel() {

	private val _userCredentialsFlow = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
	val userCredentialsFlow = _userCredentialsFlow.asStateFlow()

	private val _userDevicesFlow = MutableStateFlow<Resource<List<DeviceModel>>>(Resource.Loading)
	val userDevicesFlow = _userDevicesFlow.asStateFlow()

	private val _userEventsFlow = MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Loading)
	val userEventsFlow = _userEventsFlow.asStateFlow()

	var beginEventsDate = Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds()
		private set
	var endEventsDate = Clock.System.now().toEpochMilliseconds()
		private set

	init {
		loadUserCredentials()
		loadUserDevices()
		loadUserEvents()
	}

	private fun loadUserCredentials() = _userCredentialsFlow.emitInIO(viewModelScope) {
		usersRepo.loadUserCredentials(authStateStorage.userIdFlow.first())
	}

	private fun loadUserDevices() = _userDevicesFlow.emitInIO(viewModelScope) {
		usersRepo.loadUserDevices(authStateStorage.userIdFlow.first())
	}

	private fun loadUserEvents() = _userEventsFlow.emitInIO(viewModelScope) {
		usersRepo.loadUserEvents(
			authStateStorage.userIdFlow.first(),
			beginEventsDate.toMoscowDateTime().date.toString(),
			endEventsDate.toMoscowDateTime().toString()
		)
	}

	fun setEventsDates(beginDate: Long, endDate: Long) {
		beginEventsDate = beginDate
		endEventsDate = endDate
		loadUserEvents()
	}
}