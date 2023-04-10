package ru.rtuitlab.itlab.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.rtuitlab.itlab.data.local.events.EventsDao
import ru.rtuitlab.itlab.data.local.events.models.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.local.projects.ProjectsDao
import ru.rtuitlab.itlab.data.local.projects.models.*
import ru.rtuitlab.itlab.data.local.reports.ReportsDao
import ru.rtuitlab.itlab.data.local.reports.models.ReportEntity
import ru.rtuitlab.itlab.data.local.users.UsersDao
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

@Database(
    entities = [
        // Users
        UserEntity::class,          UserPropertyEntity::class,          UserPropertyTypeModel::class,

        // Events
        EventEntity::class,         EventDetailEntity::class,           UserEventRoleEntity::class,
        PlaceEntity::class,         ShiftEntity::class,                 EventRoleModel::class,
        EventTypeModel::class,      EventInvitationEntity::class,       UserEventEntity::class,
        EventSalaryEntity::class,   EventShiftSalary::class,            EventPlaceSalary::class,

        // Reports
        ReportEntity::class,        ReportSalary::class,

        // Projects
        Project::class,             Version::class,                     TaskWorkerEntity::class,
        ProjectOwner::class,        VersionTask::class,                 MilestoneEntity::class,
        ProjectRepoEntity::class,   Worker::class,                      VersionFileEntity::class,
                                    BudgetCertificationEntity::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(1, 2)
    ],
    exportSchema = true
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val usersDao: UsersDao
    abstract val eventsDao: EventsDao
    abstract val reportsDao: ReportsDao
    abstract val projectsDao: ProjectsDao

    companion object {
        const val NAME = "ITLAB_DATABASE"
    }
}