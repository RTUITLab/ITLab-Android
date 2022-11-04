package ru.rtuitlab.itlab.data.local.events

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

@Dao
interface EventsDao {
    @Transaction
    @Query("SELECT * FROM EventEntity ORDER BY DATETIME(beginTime) DESC")
    fun getAllEvents(): Flow<List<EventWithType>>

    // ISO8601 timestamps are designed to be comparable as strings,
    // and since ITLab uses only one timezone, there is no need to
    // use SQLite DATETIME function, so we reduce overhead
    @Transaction
    @Query("""SELECT * FROM EventEntity
    WHERE (title LIKE '%' || :query || '%') AND
    endTime >= :begin AND
    beginTime <= :end
    ORDER BY DATETIME(beginTime) DESC""")
    fun searchEvents(
        query: String,
        begin: String,
        end: String
    ): Flow<List<EventWithType>>

    @Transaction
    @Query("""SELECT * FROM UserEventEntity 
        WHERE title LIKE '%' || :query || '%' 
        ORDER BY DATETIME(beginTime) DESC""")
    fun searchUserEvents(query: String): Flow<List<UserEventWithTypeAndRole>>

    @Transaction
    @Query("SELECT * FROM EventDetailEntity WHERE id = :id")
    fun getEventDetail(id: String): Flow<EventWithShiftsAndSalary?>

    @Transaction
    @Query("SELECT * FROM EventInvitationEntity")
    fun getInvitations(): Flow<List<EventInvitationWithTypeAndRole>>

    @Transaction
    @Query("""SELECT * FROM UserEventEntity
        WHERE userId = :userId
        ORDER BY DATETIME(beginTime) DESC""")
    fun getUserEvents(userId: String): Flow<List<UserEventWithTypeAndRole>>

    @Query("SELECT * FROM EventRoleModel")
    fun getEventRoles(): Flow<List<EventRoleModel>>

    @Transaction
    @Query("SELECT * FROM UserEventRoleEntity")
    suspend fun getRolesWithUsers(): List<UserWithRole>

    @Transaction
    @Upsert
    suspend fun upsertEvent(
        event: EventEntity
    )

    @Transaction
    @Upsert
    suspend fun upsertEventTypes(types: List<EventTypeModel>)

    @Transaction
    @Upsert
    suspend fun upsertEventDetail(
        event: EventDetailEntity,
        shifts: List<ShiftEntity>,
        places: List<PlaceEntity>,
        roles: List<UserEventRoleEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertUserEvents(
        events: List<UserEventEntity>
    )

    @Transaction
    @Delete
    suspend fun deleteEvents(
        events: List<EventEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertEvents(
        events: List<EventEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertInvitations(
        invitations: List<EventInvitationEntity>
    )

    @Delete
    suspend fun deleteInvitation(invitation: EventInvitationEntity)

    @Query("DELETE FROM EventInvitationEntity WHERE id = :id AND placeId = :placeId")
    suspend fun deleteInvitation(id: String, placeId: String)

    @Query("DELETE FROM EventInvitationEntity")
    suspend fun deleteInvitations()

    @Query("DELETE FROM UserEventEntity")
    suspend fun deleteUserEvents()

    @Transaction
    @Delete
    suspend fun deleteInvitations(
        invitations: List<EventInvitationEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertEventSalaries(
        salaries: List<EventSalaryEntity>
    )

    @Transaction
    @Upsert
    suspend fun upsertShiftSalaries(
        salaries: List<EventShiftSalary>
    )

    @Transaction
    @Upsert
    suspend fun upsertPlaceSalaries(
        salaries: List<EventPlaceSalary>
    )

    @Transaction
    @Upsert
    suspend fun upsertFullEventSalary(
        eventSalary: EventSalaryEntity,
        shiftSalaries: List<EventShiftSalary>,
        placeSalaries: List<EventPlaceSalary>
    )

    @Transaction
    @Upsert
    suspend fun upsertEventRoles(
        roles: List<EventRoleModel>
    )

    @Transaction
    @Upsert
    suspend fun upsertUserEventRoles(
        roles: List<UserEventRoleEntity>
    )
}