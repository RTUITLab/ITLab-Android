package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["archivationIssuerId"]
        )
    ],
    indices = [Index("archivationIssuerId")]
)
data class Project(
    @PrimaryKey val id: String,
    val isArchived: Boolean,
    val archivationIssuerId: String?,
    val archivationDate: LocalDateTime?,
    val creationDateTime: LocalDateTime,
    val logoUrl: String,
    val name: String,
    val shortDescription: String
)


