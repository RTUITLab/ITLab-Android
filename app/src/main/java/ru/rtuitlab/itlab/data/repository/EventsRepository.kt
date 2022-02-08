package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.presentation.ui.extensions.nowAsIso8601
import javax.inject.Inject

class EventsRepository @Inject constructor(
	private val eventsApi: EventsApi,
	private val handler: ResponseHandler
) {
	suspend fun fetchAllEvents() = handler {
		eventsApi.getEvents()
	}

	suspend fun fetchPendingEvents() = handler {
		val now = nowAsIso8601()
		eventsApi.getEvents(begin = now)
	}

	suspend fun fetchAllEvents(begin: String, end: String) = handler {
		eventsApi.getEvents(begin, end)
	}

	suspend fun fetchUserEvents(
		userId: String,
		begin: String? = null,
		end: String? = null
	) = handler {
		eventsApi.getUserEvents(userId, begin, end)
	}

	suspend fun fetchEvent(eventId: String) = handler {
		eventsApi.getEvent(eventId)
	}

	suspend fun fetchEventSalary(eventId: String) = handler {
		eventsApi.getEventSalary(eventId)
	}

	suspend fun applyForPlace(placeId: String, roleId: String) = handler {
		eventsApi.applyForPlace(placeId, roleId)
	}

	suspend fun fetchEventRoles() = handler {
		eventsApi.getEventRoles()
	}
}