package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectFile(
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
)