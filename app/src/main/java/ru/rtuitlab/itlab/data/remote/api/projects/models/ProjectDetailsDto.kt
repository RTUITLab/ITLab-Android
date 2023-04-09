package ru.rtuitlab.itlab.data.remote.api.projects.models

import kotlinx.serialization.*
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.local.projects.models.Project

@Serializable
data class ProjectDetailsDto(
    val archived: Boolean,
    @SerialName("archived_by")
    val archivedBy: String?,
    @SerialName("archived_date")
    val archivedDate: String?,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
    @SerialName("github_repos")
    val githubRepos: List<ProjectRepo>,
    @SerialName("logo_url")
    val logoUrl: String,
    val name: String,
    val owners: List<Owner>,
    @SerialName("short_description")
    val shortDescription: String,
    @SerialName("updated_at")
    val updatedAt: String?
) {
    fun toProjectEntity() = Project(
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