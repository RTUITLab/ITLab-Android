package ru.rtuitlab.itlab.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.presentation.ui.extensions.minus
import ru.rtuitlab.itlab.presentation.ui.extensions.toMoscowDateTime

abstract class UserViewModel (
	private val eventsRepo: EventsRepository,
	private val getUser: GetUserUseCase,
	private val getPropertyTypes: GetUserPropertyTypesUseCase,
	val userId: String
) : ViewModel() {


	private var _beginEventsDate = MutableStateFlow(Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds())
	val beginEventsDate = _beginEventsDate.asStateFlow()
	private var _endEventsDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
	val endEventsDate = _endEventsDate.asStateFlow()

	private val _userEventsFlow = MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Empty)
	val userEventsFlow = _userEventsFlow.asStateFlow()//.also { fetchUserEvents() }

	val user = getUser(userId).map {
		it?.toUser()
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

	val properties = getPropertyTypes()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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


	fun setEventsDates(begin: Long, end: Long) {
		_beginEventsDate.value = begin
		_endEventsDate.value = end
		fetchUserEvents()
	}
}