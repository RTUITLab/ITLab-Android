package ru.rtuitlab.itlab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.rtuitlab.itlab.data.local.events.EventsDao
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.local.reports.ReportEntity
import ru.rtuitlab.itlab.data.local.users.UsersDao
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

@Database(
    entities = [
        UserEntity::class, UserPropertyEntity::class, UserPropertyTypeModel::class, // Users
        EventEntity::class, EventDetailEntity::class, UserEventRoleEntity::class, PlaceEntity::class, ShiftEntity::class, EventRoleModel::class, EventTypeModel::class, // Events
        EventInvitationEntity::class, UserEventEntity::class, EventSalaryEntity::class, EventShiftSalary::class, EventPlaceSalary::class,
        ReportEntity::class
    ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract val usersDao: UsersDao
    abstract val eventsDao: EventsDao

    companion object {
        const val NAME = "ITLAB_DATABASE"
    }
}