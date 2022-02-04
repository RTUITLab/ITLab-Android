package ru.rtuitlab.itlab.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.rtuitlab.itlab.repositories.EventsRepository
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
	eventsRepository: EventsRepository,
	savedState: SavedStateHandle
) : ViewModel() {
	val eventId: String = savedState["eventId"]!!
}