package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionThreadItemDto(
    @SerialName("attached_to")
    val attachedTo: String,
    val author: String,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
    val text: String,
    @SerialName("updated_at")
    val updatedAt: String
)