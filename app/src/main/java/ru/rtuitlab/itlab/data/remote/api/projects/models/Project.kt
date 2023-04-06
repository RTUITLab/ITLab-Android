package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val archived: Boolean,
    @SerialName("archived_by")
    val archivedBy: String?,
    @SerialName("archived_date")
    val archivedDate: String?,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
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
)