package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Version(
    @PrimaryKey val id: String,
    val isArchived: Boolean,
    val creationDateTime: LocalDateTime,
    val description: String?,
    val hardDeadline: LocalDateTime,
    val softDeadline: LocalDateTime,
    val name: String,
    val ownerId: String?,
    val projectId: String,
    val updateTime: LocalDateTime?
)
