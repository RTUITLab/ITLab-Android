package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
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
        creationDateTime = createdAt.toZonedDateTime(),
        description = null,
        hardDeadline = deadlines.hard.toZonedDateTime(),
        softDeadline = deadlines.soft.toZonedDateTime(),
        name = name,
        ownerId = owner?.userId?.ifEmpty { null },
        projectId = projectId,
        updateTime = updatedAt?.toZonedDateTime(),
        completedTaskCount = completeTaskCount,
        taskCount = taskCount,
        archivationDate = archived.archivedDate?.toZonedDateTime(),
        archivationIssuerId = archived.archivedBy
    )
}