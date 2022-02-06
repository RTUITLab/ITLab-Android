package ru.rtuitlab.itlab.data.remote.api.events

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rtuitlab.itlab.data.remote.api.events.models.EventDetailDto
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventSalary

interface EventsApi {

	@GET("Event")
	suspend fun getEvents(
		@Query("begin") begin: String? = null,
		@Query("end") end: String? = null
	) : List<EventModel>

	@GET("Event/user/{userId}")
	suspend fun getUserEvents(
		@Path("userId") userId: String,
		@Query("begin") begin: String? = null,
		@Query("end") end: String? = null
	) : List<EventModel>

	@GET("Event/{eventId}")
	suspend fun getEvent(
		@Path("eventId") eventId: String
	) : EventDetailDto

	@GET("salary/v1/event/{eventId}")
	suspend fun getEventSalary(
		@Path("eventId") eventId: String
	) : EventSalary
}