package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.local.projects.models.Version
import ru.rtuitlab.itlab.data.remote.api.projects.models.Deadlines
import ru.rtuitlab.itlab.data.remote.api.projects.models.Owner

@Serializable
data class ProjectVersion(
    val archived: Boolean,
    @SerialName("archived_by")
    val archivedBy: String?,
    @SerialName("archived_date")
    val archivedDate: String?,
    @SerialName("certify_budget")
    val budgetCertification: BudgetCertification?,
    @SerialName("created_at")
    val createdAt: String,
    val deadlines: Deadlines,
    val description: String,
    val files: Files,
    val id: String,
    val milestones: List<Milestone>?,
    val name: String,
    val owner: Owner?,
    @SerialName("project_id")
    val projectId: String,
    @SerialName("updated_at")
    val updatedAt: String?
) {
    fun toVersionEntity() = Version(
        id = id,
        isArchived = archived,
        creationDateTime = createdAt.toZonedDateTime(),
        softDeadline = deadlines.soft.toZonedDateTime(),
        hardDeadline = deadlines.hard.toZonedDateTime(),
        description = description,
        name = name,
        ownerId = owner?.userId,
        projectId = projectId,
        updateTime = updatedAt?.toZonedDateTime(),
        archivationIssuerId = archivedBy,
        archivationDate = archivedDate?.toZonedDateTime(),
        completedTaskCount = budgetCertification?.let {
            it.tasks.count { it.complete }
        } ?: 0,
        taskCount = budgetCertification?.tasks?.size ?: 0
    )
}