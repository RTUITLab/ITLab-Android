package ru.rtuitlab.itlab.presentation.screens.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.events.models.EventDetailDto
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.data.remote.api.events.models.EventSalary
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
	private val eventsRepository: EventsRepository,
	private val savedState: SavedStateHandle
) : ViewModel() {
	private val eventId: String = savedState["eventId"]!!

	private var _eventResourceFlow = MutableStateFlow<Resource<Pair<EventDetailDto, EventSalary?>>>(Resource.Loading)
	val eventResourceFlow = _eventResourceFlow.asStateFlow().also {
		fetchEventData()
	}

	private fun fetchEventData() = _eventResourceFlow.emitInIO(viewModelScope) {
		var resource: Resource<Pair<EventDetailDto, EventSalary?>> = Resource.Loading
		eventsRepository.fetchEvent(eventId).handle(
			onSuccess = { details ->
				resource = Resource.Success(details to null)
				eventsRepository.fetchEventSalary(eventId).handle(
					onSuccess = { resource = Resource.Success(details to it) }
				)
			},
			onError = { resource = Resource.Error(it) }
		)
		resource
	}
}