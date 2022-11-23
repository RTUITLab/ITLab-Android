package ru.rtuitlab.itlab.domain.repository

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

interface EventsRepository {

    val eventsUpdatedAtLeastOnce: Boolean

    suspend fun updatePendingEvents(): Resource<List<EventModel>>

    fun getEvents(): Flow<List<EventWithType>>

    fun getUserEvents(userId: String): Flow<List<UserEventWithTypeAndRole>>

    fun getEventRoles(): Flow<List<EventRoleModel>>

    fun searchEvents(
        query: String,
        begin: String,
        end: String
    ): Flow<List<EventWithType>>

    fun searchUserEvents(query: String, userId: String): Flow<List<UserEventWithTypeAndRole>>

    fun getEventDetail(eventId: String): Flow<EventWithShiftsAndSalary?>

    fun getInvitations(): Flow<List<EventInvitationWithTypeAndRole>>

    suspend fun updateEvents(
        begin: String? = null,
        end: String? = null
    ): Resource<List<EventModel>>

    suspend fun updateUserEvents(
        userId: String,
        begin: String? = null,
        end: String? = null
    ): Resource<List<UserEventModel>>

    suspend fun updateEventDetails(eventId: String): Resource<EventDetailDto>

    suspend fun updateEventSalary(eventId: String): Resource<EventSalary>

    suspend fun applyForPlace(placeId: String, roleId: String): Resource<Response<Unit>>

    suspend fun updateEventRoles(): Resource<List<EventRoleModel>>

    suspend fun updateInvitations(): Resource<List<EventInvitationDto>>

    suspend fun updateEventTypes(): Resource<List<EventTypeModel>>

    suspend fun rejectInvitation(placeId: String): Resource<Response<Unit>>

    suspend fun acceptInvitation(placeId: String): Resource<Response<Unit>>

    suspend fun deleteInvitation(id: String, placeId: String)

    suspend fun clearUserEvents()
}