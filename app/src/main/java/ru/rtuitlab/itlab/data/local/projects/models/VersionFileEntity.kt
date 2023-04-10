package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Version::class,
            parentColumns = ["id"],
            childColumns = ["versionId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("versionId")
    ]
)
data class VersionFileEntity(
    @PrimaryKey val id: String,
    val attachmentId: String,
    val versionId: String,
    val authorId: String,
    val createdAt: ZonedDateTime,
    val fileId: String,
    val fileType: ProjectFileType,
    val name: String
)

enum class ProjectFileType {
    FUNCTIONAL_TASK,
    ATTACHMENT
}
