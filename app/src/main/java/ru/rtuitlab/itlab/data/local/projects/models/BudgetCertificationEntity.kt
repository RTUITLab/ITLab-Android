package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"]
        )
    ]
)
data class BudgetCertificationEntity(
    @PrimaryKey val versionId: String,
    val certificationIssuerId: String,
    val certificationDateTime: LocalDateTime,
    val totalCost: Int
)