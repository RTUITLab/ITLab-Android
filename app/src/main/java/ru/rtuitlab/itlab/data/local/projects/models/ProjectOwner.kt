package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import ru.rtuitlab.itlab.data.local.users.models.UserEntity

@Entity(
    primaryKeys = ["userId", "projectId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["projectId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = CASCADE
        )
    ]
)
data class ProjectOwner(
    val userId: String,
    val projectId: String
)