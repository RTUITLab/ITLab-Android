package ru.rtuitlab.itlab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.reports.ReportEntity
import ru.rtuitlab.itlab.data.local.users.UsersDao
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

@Database(
    entities = [
        UserEntity::class, UserPropertyEntity::class, UserPropertyTypeModel::class, // Users
        EventEntity::class, EventDetailEntity::class, EventRoleEntity::class, PlaceEntity::class, ShiftEntity::class, EventRoleModel::class, EventTypeModel::class, // Events
        ReportEntity::class
    ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract val dao: UsersDao

    companion object {
        const val NAME = "ITLAB_DATABASE"
    }
}