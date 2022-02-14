package ru.rtuitlab.itlab.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.ui.extensions.minus
import ru.rtuitlab.itlab.presentation.ui.extensions.toMoscowDateTime

abstract class UserViewModel (
	private val usersRepo: UsersRepository,
	private val eventsRepo: EventsRepository,
	val userId: String
) : ViewModel() {


	private var _beginEventsDate = MutableStateFlow(Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds())
	val beginEventsDate = _beginEventsDate.asStateFlow()
	private var _endEventsDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
	val endEventsDate = _endEventsDate.asStateFlow()

	protected val _userCredentialsFlow = MutableStateFlow<Resource<UserResponse>>(Resource.Loading)
	val userCredentialsFlow = _userCredentialsFlow.asStateFlow().also { fetchUserCredentials() }

	private val _userDevicesFlow = MutableStateFlow<Resource<List<DeviceModel>>>(Resource.Empty)
	val userDevicesFlow = _userDevicesFlow.asStateFlow()//.also { fetchUserDevices() }

	private val _userEventsFlow = MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Empty)
	val userEventsFlow = _userEventsFlow.asStateFlow()//.also { fetchUserEvents() }

	private val _properties = MutableStateFlow<Resource<List<UserPropertyTypeModel>>>(Resource.Empty)
	val properties = _properties.asStateFlow().also { fetchPropertyTypes() }

	private fun fetchUserCredentials() = _userCredentialsFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUserCredentials(userId)
	}

	/*private fun fetchUserDevices() = _userDevicesFlow.emitInIO(viewModelScope) {
		usersRepo.fetchUserDevices(userId)
	}*/

	private fun fetchUserEvents() = _userEventsFlow.emitInIO(viewModelScope) {
		eventsRepo.fetchUserEvents(
			userId,
			beginEventsDate.value.toMoscowDateTime().date.toString(),
			endEventsDate.value.toMoscowDateTime().toString()
		)
	}

	private fun fetchPropertyTypes() = _properties.emitInIO(viewModelScope) {
		usersRepo.fetchPropertyTypes()
	}

	fun setEventsDates(begin: Long, end: Long) {
		_beginEventsDate.value = begin
		_endEventsDate.value = end
		fetchUserEvents()
	}
}