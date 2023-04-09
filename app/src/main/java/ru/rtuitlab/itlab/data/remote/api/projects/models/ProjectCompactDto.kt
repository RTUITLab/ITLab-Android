package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.projects.models.Project as ProjectEntity
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.remote.api.users.models.User

@Serializable
data class ProjectCompactDto(
    val id: String,
    val archived: Boolean,
    @SerialName("archived_by")
    val archivedBy: String?,
    @SerialName("archived_date")
    val archivedDate: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("last_version")
    val lastVersion: LastVersion?,
    @SerialName("logo_url")
    val logoUrl: String,
    val name: String,
    val owners: List<Owner>,
    @SerialName("short_description")
    val shortDescription: String,
    @SerialName("updated_at")
    val updatedAt: String?
) {
    fun toProjectEntity() = ProjectEntity(
        id = id,
        isArchived = archived,
        archivationIssuerId = archivedBy,
        archivationDate = archivedDate?.toLocalDateTime(),
        creationDateTime = createdAt.toLocalDateTime(),
        logoUrl = logoUrl,
        name = name,
        shortDescription = shortDescription
    )
}

data class ProjectCompact(
    val id: String,
    val archived: Boolean,
    val archivedBy: String?,
    val archivedDate: String?,
    val createdAt: String,
    val lastVersion: LastVersion?,
    val logoUrl: String,
    val name: String,
    val owners: List<User>,
    val shortDescription: String,
    val updatedAt: String?
)