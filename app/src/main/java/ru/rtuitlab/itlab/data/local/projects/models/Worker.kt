package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import java.time.LocalDateTime

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
    val creationTime: LocalDateTime,
    val rateModifier: Int,
    val role: String,
    val roleId: String,
    val hourlyRate: Int,
    val monthlySalary: Int,
    val updateTime: LocalDateTime?,
    val userId: String,
    val versionId: String,
    val workHours: Int
)
