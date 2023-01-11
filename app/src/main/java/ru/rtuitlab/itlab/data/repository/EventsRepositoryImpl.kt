package ru.rtuitlab.itlab.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.local.AppDatabase
import ru.rtuitlab.itlab.data.local.events.models.EventWithShiftsAndSalary
import ru.rtuitlab.itlab.data.local.events.models.EventWithType
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.data.repository.util.tryUpdate
import ru.rtuitlab.itlab.domain.repository.EventsRepository
import ru.rtuitlab.itlab.common.extensions.nowAsIso8601
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.local.events.models.EventEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepositoryImpl @Inject constructor(
    private val eventsApi: EventsApi,
    private val handler: ResponseHandler,
    private val authStateStorage: IAuthStateStorage,
    private val coroutineScope: CoroutineScope,
    db: AppDatabase
): EventsRepository {

    init {
        coroutineScope.launch {
            updateEvents(
                begin = nowAsIso8601()
            )
            updateInvitations()
        }
    }

    private val dao = db.eventsDao

    override var eventsUpdatedAtLeastOnce: Boolean = false
        private set

    override suspend fun updatePendingEvents() = updateEvents(
        begin = nowAsIso8601(),
        end = null
    )

    override fun getEvents(): Flow<List<EventWithType>> = dao.getAllEvents()

    override fun getUserEvents(
        userId: String,
        beginTime: String,
        endTime: String
    ) = dao.getUserEvents(userId, beginTime, endTime)

    override fun getEventRoles() = dao.getEventRoles()

    override fun searchEvents(
        query: String,
        begin: String,
        end: String
    ): Flow<List<EventWithType>> = dao.searchEvents(query, begin,  end)

    override fun searchUserEvents(query: String, userId: String) = dao.searchUserEvents(query, userId)

    override fun getEventDetail(eventId: String): Flow<EventWithShiftsAndSalary?> = dao.getEventDetail(eventId)

    override fun getInvitations() = dao.getInvitations()

    override suspend fun updateEvents(
        begin: String?,
        end: String?
    ) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getEvents(begin, end) },
        into = {
            eventsUpdatedAtLeastOnce = true
            updateEventTypes()
            updateEventRoles()
            dao.upsertEvents(
                it.map { it.toEventEntity() }
            )
        }
    )

    override suspend fun updateUserEvents(
        userId: String,
        begin: String?,
        end: String?
    ): Resource<List<UserEventModel>> = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getUserEvents(userId, begin, end) },
        into = {
            dao.upsertEventRoles(
                it.map { it.role }
            )
            dao.upsertEventTypes(
                it.map { it.eventType }
            )
            dao.upsertUserEvents(
                it.map { it.toEntity(userId) }
            )
        }
    )

    override suspend fun updateEventDetails(eventId: String) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getEvent(eventId) },
        into = {
            updateEventRoles()

            val currentUserId = authStateStorage.userIdFlow.first()
            // Since EventDetailEntity links to an EventEntity with a foreign key,
            // corresponding EventEntity must be present in the database.
            // There is no way to fetch this model, so we construct it ourselves.
            dao.upsertEvent(
                EventEntity(
                    id = it.id,
                    title = it.title,
                    beginTime = it.shifts.minOf { it.beginTime },
                    endTime = it.shifts.maxOf { it.endTime },
                    typeId = it.eventType.id,
                    address = it.address,
                    shiftsCount = it.shifts.size,
                    currentParticipantsCount = it.shifts.sumOf { it.places.sumOf { it.participants.size } },
                    targetParticipantsCount = it.shifts.sumOf { it.places.sumOf { it.targetParticipantsCount } },
                    participating = it.shifts.any {
                        it.places.any {
                            it.participants.any {
                                it.user.id == currentUserId
                            } ||
                            it.wishers.any {
                                it.user.id == currentUserId
                            } ||
                            it.invited.any {
                                it.user.id == currentUserId
                            } ||
                            it.unknowns.any {
                                it.user.id == currentUserId
                            }
                        }
                    }
                )
            )
            dao.upsertEventDetail(
                event = it.toEventDetailEntity(),
                shifts = it.extractShiftEntities(),
                places = it.extractPlaceEntities(),
                roles = it.extractRoles()
            )
            updateEventSalary(eventId)
        }
    )

    override suspend fun updateEventSalary(eventId: String) = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getEventSalary(eventId) },
        into = {
            dao.upsertFullEventSalary(
                eventSalary = it.toEventSalaryEntity(),
                shiftSalaries = it.shiftSalaries,
                placeSalaries = it.placeSalaries
            )
        }
    )

    override suspend fun applyForPlace(placeId: String, roleId: String) = handler {
        eventsApi.applyForPlace(placeId, roleId)
    }

    override suspend fun updateEventRoles() = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getEventRoles() },
        into = {
            dao.upsertEventRoles(it)
        }
    )

    override suspend fun updateInvitations() = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getInvitations() },
        into = {
            updateEventRoles()
            updateEventTypes()
            dao.upsertInvitations(
                it.map {
                    it.toInvitationEntity()
                }
            )
        }
    )

    override suspend fun updateEventTypes() = tryUpdate(
        inScope = coroutineScope,
        withHandler = handler,
        from = { eventsApi.getEventTypes() },
        into = {
            dao.upsertEventTypes(it)
        }
    )

    override suspend fun rejectInvitation(placeId: String) = handler {
        eventsApi.rejectInvitation(placeId)
    }

    override suspend fun acceptInvitation(placeId: String) = handler {
        eventsApi.acceptInvitation(placeId)
    }

    override suspend fun deleteInvitation(id: String, placeId: String) =
        dao.deleteInvitation(id, placeId)

    override suspend fun clearUserEvents() {
        dao.deleteInvitations()
        dao.deleteUserEvents()
        eventsUpdatedAtLeastOnce = false
    }

}