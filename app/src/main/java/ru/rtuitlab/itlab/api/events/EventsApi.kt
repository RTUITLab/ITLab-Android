package ru.rtuitlab.itlab.api.events

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rtuitlab.itlab.api.events.models.EventModel

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
}