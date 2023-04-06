package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastVersion(
    val archived: ArchivationInfo,
    @SerialName("complete_task_count")
    val completeTaskCount: Int,
    @SerialName("created_at")
    val createdAt: String,
    val deadlines: Deadlines,
    val id: String,
    val name: String,
    val owner: Owner?,
    @SerialName("task_count")
    val taskCount: Int,
    @SerialName("updated_at")
    val updatedAt: String?,
    val workers: List<Worker>
)