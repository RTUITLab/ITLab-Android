package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArchivationInfo(
    val archived: Boolean,
    @SerialName("archived_by")
    val archivedBy: String?,
    @SerialName("archived_date")
    val archivedDate: String?
)