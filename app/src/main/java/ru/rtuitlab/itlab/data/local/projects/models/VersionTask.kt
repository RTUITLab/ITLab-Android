package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"]
        )
    ],
    indices = [Index("versionId")]
)
data class VersionTask(
    @PrimaryKey val id: String,
    val baseCost: Double,
    val isCompleted: Boolean,
    val creationTime: LocalDateTime,
    val name: String,
    val cost: Int,
    val updateTime: LocalDateTime,
    val versionId: String
)
