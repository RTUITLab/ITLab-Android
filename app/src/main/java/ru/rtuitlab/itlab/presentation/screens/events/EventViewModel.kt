package ru.rtuitlab.itlab.presentation.screens.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.domain.use_cases.events.ApplyForPlaceUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.GetEventRolesUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.GetEventUseCase
import ru.rtuitlab.itlab.domain.use_cases.events.UpdateEventDetailsUseCase
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
	private val updateEvent: UpdateEventDetailsUseCase,
	private val applyForPlace: ApplyForPlaceUseCase,
	getEventRoles: GetEventRolesUseCase,
	getEvent: GetEventUseCase,
	savedState: SavedStateHandle
) : ViewModel() {
	private val eventId: String = savedState["eventId"]!!


	val event = getEvent(eventId)
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

	val roles = getEventRoles().map {
		it.map {
			it.toUiRole()
		}
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	private val _uiEvents = MutableSharedFlow<UiEvent>()
	val uiEvents = _uiEvents.asSharedFlow()

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	init {
		viewModelScope.launch {
			if (event.first() == null) {
				updateDetails()
			}
		}
	}

	fun updateDetails() = viewModelScope.launch {
		_isRefreshing.emit(true)
		updateEvent(eventId).handle(
			onError = {
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
		_isRefreshing.emit(false)
	}

	fun onPlaceApply(
		placeId: String,
		roleId: String,
		successMessage: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		applyForPlace(placeId, roleId).handle(
			onSuccess = {
				onFinish()
				updateDetails()
				queueSnackbar(it.errorBody()?.string() ?: successMessage)
			},
			onError = {
				onFinish()
				queueSnackbar(it)
			}
		)
	}

	private suspend fun queueSnackbar(text: String) {
		_uiEvents.emit(
			UiEvent.Snackbar(text)
		)
	}
}