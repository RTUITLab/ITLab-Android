package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import java.time.ZonedDateTime
import ru.rtuitlab.itlab.data.local.projects.models.Project as ProjectEntity

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
        archivationDate = archivedDate?.toZonedDateTime(),
        creationDateTime = createdAt.toZonedDateTime(),
        logoUrl = logoUrl,
        name = name,
        shortDescription = shortDescription
    )

    fun toProjectCompact(users: List<User>) = ProjectCompact(
        id = id,
        archived = archived,
        archivedBy = archivedBy,
        archivedDate = archivedDate?.toZonedDateTime(),
        createdAt = createdAt.toZonedDateTime(),
        lastVersion = lastVersion,
        logoUrl = logoUrl,
        name = name,
        owners = owners.map { owner -> users.toSet().find { it.id == owner.userId }!! },
        shortDescription = shortDescription,
        updatedAt = updatedAt?.toZonedDateTime()
    )
}

data class ProjectCompact(
    val id: String,
    val archived: Boolean,
    val archivedBy: String?,
    val archivedDate: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val lastVersion: LastVersion?,
    val logoUrl: String,
    val name: String,
    val owners: List<User>,
    val shortDescription: String,
    val updatedAt: ZonedDateTime?
)