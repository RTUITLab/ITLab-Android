package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.api.users.models.UserEventModel
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.repositories.UsersRepository
import ru.rtuitlab.itlab.utils.emitInIO
import ru.rtuitlab.itlab.utils.minus
import ru.rtuitlab.itlab.utils.toMoscowDateTime

abstract class UserViewModel (
	private val usersRepo: UsersRepository,
	private val userId: String
) : ViewModel() {

	var beginEventsDate = Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds()
		private set
	var endEventsDate = Clock.System.now().toEpochMilliseconds()
		private set

	private val _userCredentialsFlow = MutableStateFlow<Resource<UserModel>>(Resource.Loading)
	val userCredentialsFlow = _userCredentialsFlow.asStateFlow().also { fetchUserCredentials() }

	private val _userDevicesFlow = MutableStateFlow<Resource<List<DeviceModel>>>(Resource.Loading)
	val userDevicesFlow = _userDevicesFlow.asStateFlow().also { fetchUserDevices() }

	private val _userEventsFlow = MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Loading)
	val userEventsFlow = _userEventsFlow.asStateFlow().also { fetchUserEvents() }

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