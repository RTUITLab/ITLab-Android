package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.local.TypeConverter
import ru.rtuitlab.itlab.data.local.projects.models.VersionFileEntity

@Serializable
data class VersionFile(
    @SerialName("attached_id")
    val attachedId: String,
    @SerialName("attached_to")
    val attachedTo: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("file_id")
    val fileId: String,
    @SerialName("file_type")
    val fileType: String,
    val id: String,
    val name: String
) {
    fun toEntity(versionId: String) = VersionFileEntity(
        id = id,
        attachmentId = attachedId,
        versionId = versionId,
        authorId = authorId,
        createdAt = createdAt.toZonedDateTime(),
        fileId = fileId,
        fileType = TypeConverter.fromStringToProjectFileType(fileType),
        name = name
    )
}