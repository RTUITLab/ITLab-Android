package ru.rtuitlab.itlab.data.local.events

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary

@Dao
interface EventsDao {
    @Transaction
    @Query("SELECT * FROM EventEntity")
    fun getAllEvents(): Flow<List<EventWithType>>

    @Query("SELECT * FROM EventEntity WHERE title LIKE '%' | :query | '%'")
    fun searchEvents(query: String): Flow<List<EventWithType>>

    @Transaction
    @Query("SELECT * FROM EventDetailEntity WHERE id = :id")
    fun getEventDetail(id: String): Flow<EventWithShiftsAndSalary?>

    @Query("SELECT * FROM EventInvitationEntity")
    fun getInvitations(): Flow<List<EventInvitationEntity>>

    @Query("SELECT * FROM UserEventEntity")
    fun getUserEvents(): Flow<List<UserEventWithTypeAndRole>>

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
}