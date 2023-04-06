package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.projects.models.Deadlines
import ru.rtuitlab.itlab.data.remote.api.projects.models.Owner

@Serializable
data class ProjectVersion(
    val archived: Boolean,
    @SerialName("certify_budget")
    val certifyBudget: CertifyBudget?,
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
)