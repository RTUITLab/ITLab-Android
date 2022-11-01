package ru.rtuitlab.itlab.data.remote.api.events

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

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
	): List<UserEventModel>

	@GET("Event/{eventId}")
	suspend fun getEvent(
		@Path("eventId") eventId: String
	) : EventDetailDto

	@GET("salary/v1/event/{eventId}")
	suspend fun getEventSalary(
		@Path("eventId") eventId: String
	) : EventSalary

	@POST("Event/wish/{placeId}/{roleId}")
	suspend fun applyForPlace(
		@Path("placeId") placeId: String,
		@Path("roleId") roleId: String
	) : Response<Unit>

	@GET("event/applications/invitations")
	suspend fun getInvitations() : List<EventInvitationDto>

	@POST("/api/Event/invitation/{placeId}/reject")
	suspend fun rejectInvitation(
		@Path("placeId") placeId: String
	) : Response<Unit>

	@POST("/api/Event/invitation/{placeId}/accept")
	suspend fun acceptInvitation(
		@Path("placeId") placeId: String
	) : Response<Unit>

	@GET("eventRole")
	suspend fun getEventRoles() : List<EventRoleModel>

	@GET("EventType?all=true")
	suspend fun getEventTypes(): List<EventTypeModel>
}