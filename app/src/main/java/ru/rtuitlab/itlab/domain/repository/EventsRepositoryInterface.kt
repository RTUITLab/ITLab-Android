package ru.rtuitlab.itlab.domain.repository

import retrofit2.Response
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

interface EventsRepositoryInterface {
    suspend fun updatePendingEvents(): Resource<List<EventModel>>

    suspend fun updateEvents(
        begin: String? = null,
        end: String? = null
    ): Resource<List<EventModel>>

    suspend fun updateUserEvents(
        userId: String,
        begin: String? = null,
        end: String? = null
    ): Resource<List<UserEventModel>>

    suspend fun fetchEvent(eventId: String): Resource<EventDetailDto>

    suspend fun updateEventSalary(eventId: String): Resource<EventSalary>

    suspend fun applyForPlace(placeId: String, roleId: String): Resource<Response<Unit>>

    suspend fun updateEventRoles(): Resource<List<EventRoleModel>>

    suspend fun updateInvitations(): Resource<List<EventInvitationDto>>

    suspend fun rejectInvitation(placeId: String): Resource<Response<Unit>>

    suspend fun acceptInvitation(placeId: String): Resource<Response<Unit>>
}