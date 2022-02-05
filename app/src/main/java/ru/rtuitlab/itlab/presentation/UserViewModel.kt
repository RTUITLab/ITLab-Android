package ru.rtuitlab.itlab.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.utils.minus
import ru.rtuitlab.itlab.utils.toMoscowDateTime

abstract class UserViewModel (
	private val usersRepo: UsersRepository,
	val userId: String
) : ViewModel() {

	var beginEventsDate = Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds()
		private set
	var endEventsDate = Clock.System.now().toEpochMilliseconds()
		private set

	private val _userCredentialsFlow = MutableStateFlow<Resource<UserResponse>>(Resource.Loading)
	val userCredentialsFlow = _userCredentialsFlow.asStateFlow().also { fetchUserCredentials() }

	private val _userDevicesFlow = MutableStateFlow<Resource<List<DeviceModel>>>(Resource.Loading)
	val userDevicesFlow = _userDevicesFlow.asStateFlow()//.also { fetchUserDevices() }

	private val _userEventsFlow = MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Loading)
	val userEventsFlow = _userEventsFlow.asStateFlow()//.also { fetchUserEvents() }

	private fun fetchUserCredentials() = _userCredentialsFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUserCredentials(userId)
	}

	private fun fetchUserDevices() = _userDevicesFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUserDevices(userId)
	}

	private fun fetchUserEvents() = _userEventsFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUserEvents(
			userId,
			beginEventsDate.toMoscowDateTime().date.toString(),
			endEventsDate.toMoscowDateTime().toString()
		)
	}

	fun setEventsDates(beginDate: Long, endDate: Long) {
		beginEventsDate = beginDate
		endEventsDate = endDate
		fetchUserEvents()
	}
}