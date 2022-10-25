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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(
        event: EventEntity
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventDetail(
        event: EventDetailEntity,
        shifts: List<ShiftEntity>,
        places: List<PlaceEntity>,
        roles: List<UserEventRoleEntity>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEvents(
        events: List<UserEventEntity>
    )

    @Transaction
    @Delete
    suspend fun deleteEvents(
        events: List<EventEntity>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(
        events: List<EventEntity>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitations(
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventSalaries(
        salaries: List<EventSalaryEntity>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShiftSalaries(
        salaries: List<EventShiftSalary>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceSalaries(
        salaries: List<EventPlaceSalary>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFullEventSalary(
        eventSalary: EventSalaryEntity,
        shiftSalaries: List<EventShiftSalary>,
        placeSalaries: List<EventPlaceSalary>
    )

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventRoles(
        roles: List<EventRoleModel>
    )
}