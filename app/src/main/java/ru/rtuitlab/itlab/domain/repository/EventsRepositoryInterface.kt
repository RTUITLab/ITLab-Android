package ru.rtuitlab.itlab.domain.repository

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.events.models.EventWithShiftsAndSalary
import ru.rtuitlab.itlab.data.local.events.models.EventWithType
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

interface EventsRepositoryInterface {
    suspend fun updatePendingEvents(): Resource<List<EventModel>>

    fun getEvents(): Flow<List<EventWithType>>

    fun searchEvents(query: String): Flow<List<EventWithType>>

    fun getEventDetail(eventId: String): Flow<EventWithShiftsAndSalary?>

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

    suspend fun updateEventTypes(): Resource<List<EventTypeModel>>

    suspend fun rejectInvitation(placeId: String): Resource<Response<Unit>>

    suspend fun acceptInvitation(placeId: String): Resource<Response<Unit>>
}