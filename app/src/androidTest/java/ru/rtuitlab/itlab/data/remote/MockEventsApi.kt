package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.Generator
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.data.remote.api.events.models.*
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Participant
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Place
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

class MockEventsApi : EventsApi {

    private val usersApi = MockUsersApi()

    private val eventTypes = Generator.eventTypes

    private val events = Array(6) {
        EventModel(
            id = it.toString(),
            title = "Event title $it",
            beginTime = "2022-0${it + 1}-25T11:07:44Z",
            endTime = "2022-0${it + 1}-25T18:07:44Z",
            eventType = eventTypes[it % 3],
            address = "Vernadskogo $it",
            shiftsCount = 3,
            currentParticipantsCount = 4,
            targetParticipantsCount = 5,
            participating = false
        )
    }

    private val eventRoles = Array(3) {
        EventRoleModel(
            id = it.toString(),
            title = "Role $it",
            description = null
        )
    }

    private val eventSalaries = events.map {
        EventSalaryEntity(
            eventId = it.id,
            createdAt = "2022-07-25T11:07:44Z",
            authorId = "1",
            modificationDate = "2022-07-25T11:07:44Z",
            count = 2000,
            description = "Держи свои копкейки"
        )
    }

    private val eventDetails = Array(6) { id ->
        EventDetailDto(
            id = events[id].id,
            title = events[id].title,
            description = "Detailed description $id",
            address = events[id].address,
            eventType = events[id].eventType,
            shifts = Array(2) { shiftId ->
                Shift(
                    id = "Event $id Shift $shiftId",
                    beginTime = events[shiftId].beginTime,
                    endTime = events[id].endTime!!,
                    description = "Shift description $shiftId",
                    places = Array(4) { placeId ->
                        Place(
                            id = "Shift $shiftId place $placeId",
                            targetParticipantsCount = 5,
                            description = null,
                            equipment = null,
                            participants = Array(1) {
                                Participant(
                                    user = usersApi.users[it],
                                    eventRole = eventRoles[(placeId + it) % 3]
                                )
                            }.toList(),
                            invited = emptyList(),
                            wishers = emptyList(),
                            unknowns = emptyList()
                        )
                    }.toList()
                )
            }.toList()
        )
    }

    private val shiftSalaries = eventDetails.flatMap {
        it.shifts.map {
            EventShiftSalary(
                shiftId = it.id,
                count = 1000,
                description = "whatever"
            )
        }
    }

    private val placeSalaries = eventDetails.flatMap {
        it.shifts.flatMap {
            it.places.map {
                EventPlaceSalary(
                    placeId = it.id,
                    count = 500,
                    description = ""
                )
            }
        }
    }

    private val userEvents = Array(3) {
        UserEventModel(
            id = it.toString(),
            address = events[it].address,
            beginTime = events[it].beginTime,
            title = events[it].title,
            eventType = events[it].eventType,
            role = EventRoleModel(
                id = it.toString(),
                title = "Role $it",
                description = null
            )
        )
    }

    private val eventInvitations = Array(5) {
        EventInvitationDto(
            id = it.toString(),
            title = "Invitation $it",
            eventType = eventTypes[it % eventTypes.size],
            beginTime = "2022-07-25T11:07:44Z",
            placeId = eventDetails.random().shifts.random().places.random().id,
            placeDescription = "Whatever",
            placeNumber = 13,
            shiftDescription = "Whatever",
            shiftDurationInMinutes = 10000.0,
            eventRole = eventRoles.random(),
            creationTime = "2022-07-25T11:07:44Z"
        )
    }

    override suspend fun getEvents(begin: String?, end: String?): List<EventModel> {
        return events.toList()
    }

    override suspend fun getUserEvents(
        userId: String,
        begin: String?,
        end: String?
    ): List<UserEventModel> {
        return userEvents.toList()
    }

    override suspend fun getEvent(eventId: String): EventDetailDto {
        return eventDetails.find { it.id == eventId }!!
    }

    override suspend fun getEventSalary(eventId: String): EventSalary {

        val shiftSalaries = shiftSalaries.filter {
            it.shiftId in eventDetails.find { it.id == eventId }!!
                .shifts.map {
                    it.id
                }
        }

        val placeSalaries = placeSalaries.filter {
            it.placeId in eventDetails.find { it.id == eventId }!!
                .shifts.flatMap {
                    it.places.map {
                        it.id
                    }
                }
        }


        return with(eventSalaries.find { it.eventId == eventId }!!) {
            EventSalary(
                eventId = eventId,
                created = createdAt,
                shiftSalaries = shiftSalaries,
                placeSalaries = placeSalaries,
                authorId = authorId,
                modificationDate = modificationDate,
                count = count,
                description = description
            )
        }
    }

    override suspend fun applyForPlace(placeId: String, roleId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getInvitations(): List<EventInvitationDto> {
        return eventInvitations.toList()
    }

    override suspend fun rejectInvitation(placeId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvitation(placeId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getEventRoles(): List<EventRoleModel> {
        return eventRoles.toList()
    }

    override suspend fun getEventTypes(): List<EventTypeModel> {
        return eventTypes.toList()
    }
}