package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            CASCADE
        ),
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"],
            CASCADE
        )
    ]
)
data class Worker(
    @PrimaryKey val id: String,
    val appointerId: String,
    val baseHourlyRate: Double,
    val isConfirmed: Boolean,
    val creationTime: ZonedDateTime,
    val rateModifier: Int,
    val role: String,
    val roleId: String,
    val hourlyRate: Int,
    val monthlySalary: Int,
    val updateTime: ZonedDateTime?,
    val userId: String,
    val versionId: String,
    val workHours: Int
)

data class UserWorker(
    @Embedded val worker: Worker,
    @Relation(
        entity = UserEntity::class,
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserWithProperties
)