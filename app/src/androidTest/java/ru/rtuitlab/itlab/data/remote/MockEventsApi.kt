package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

class MockEventsApi: EventsApi {
    override suspend fun getEvents(begin: String?, end: String?): List<EventModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserEvents(
        userId: String,
        begin: String?,
        end: String?
    ): List<UserEventModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvent(eventId: String): EventDetailDto {
        TODO("Not yet implemented")
    }

    override suspend fun getEventSalary(eventId: String): EventSalary {
        TODO("Not yet implemented")
    }

    override suspend fun applyForPlace(placeId: String, roleId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getInvitations(): List<EventInvitationDto> {
        TODO("Not yet implemented")
    }

    override suspend fun rejectInvitation(placeId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvitation(placeId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getEventRoles(): List<EventRoleModel> {
        TODO("Not yet implemented")
    }
}