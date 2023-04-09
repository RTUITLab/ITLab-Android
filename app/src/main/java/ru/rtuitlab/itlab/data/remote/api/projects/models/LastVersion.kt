package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.local.projects.models.Version

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
) {
    fun toVersionEntity(projectId: String) = Version(
        id = id,
        isArchived = archived.archived,
        creationDateTime = createdAt.toLocalDateTime(),
        description = null,
        hardDeadline = deadlines.hard.toLocalDateTime(),
        softDeadline = deadlines.soft.toLocalDateTime(),
        name = name,
        ownerId = owner?.userId,
        projectId = projectId,
        updateTime = updatedAt?.toLocalDateTime()
    )
}