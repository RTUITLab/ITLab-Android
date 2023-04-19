package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    primaryKeys = ["versionId", "roleId"],
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"],
            onDelete = CASCADE
        )
    ]
)
data class VersionRoleTotalEntity(
    val versionId: String,
    val roleId: String,
    val totalCost: Int,
    val totalHours: Int
)
