package ru.rtuitlab.itlab.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.*
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserPropertyTypesUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.GetUserEventsUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.UpdateUserEventsUseCase

abstract class UserViewModel(
	private val updateUserEvents: UpdateUserEventsUseCase,
	getPropertyTypes: GetUserPropertyTypesUseCase,
	getUser: GetUserUseCase,
	getUserEvents: GetUserEventsUseCase,
	val userId: String
) : ViewModel() {

	private var eventsUpdated = false

	private var _beginEventsDate = MutableStateFlow(Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds())
	val beginEventsDate = _beginEventsDate.asStateFlow()
	private var _endEventsDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
	val endEventsDate = _endEventsDate.asStateFlow()

	val user = getUser(userId).map {
		it?.toUser()
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

	val properties = getPropertyTypes()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	private val _areEventsRefreshing = MutableStateFlow(false)
	val areEventsRefreshing = _areEventsRefreshing.asStateFlow()

	private val _eventUpdateErrorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
	val eventUpdateErrorMessage = _eventUpdateErrorMessage.asStateFlow()

	val events = getUserEvents(userId)
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	fun updateEvents() = viewModelScope.launch {
		_areEventsRefreshing.emit(true)
		_eventUpdateErrorMessage.emit(null)
		updateUserEvents(
			userId,
			beginEventsDate.value.toIsoString(false),
			endEventsDate.value.toIsoString(true)
		).handle(
			onError = {
				_eventUpdateErrorMessage.emit(it)
			},
			onSuccess = {
				_eventUpdateErrorMessage.emit(null)
			}
		)
		_areEventsRefreshing.emit(false)
	}

	fun ensureEventsUpdated() {
		if (eventsUpdated) return
		updateEvents()
		eventsUpdated = true
	}


	fun setEventsDates(begin: Long, end: Long) {
		_beginEventsDate.value = begin
		_endEventsDate.value = end
		updateEvents()
	}
}