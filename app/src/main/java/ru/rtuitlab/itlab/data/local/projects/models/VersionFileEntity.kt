package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import ru.rtuitlab.itlab.BuildConfig
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
) {
    @Ignore
    val fileUrl: String = "${BuildConfig.API_URI}/mfs/download/$fileId"
}

enum class ProjectFileType {
    FUNCTIONAL_TASK,
    ATTACHMENT
}
