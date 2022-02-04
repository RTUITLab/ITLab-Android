package ru.rtuitlab.itlab.repositories

import android.util.Log
import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.events.EventsApi
import ru.rtuitlab.itlab.utils.nowAsIso8601
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
		Log.v("DateTime", now)
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
}