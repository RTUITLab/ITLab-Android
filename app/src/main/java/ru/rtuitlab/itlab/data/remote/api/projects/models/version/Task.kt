package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.local.projects.models.VersionTask

@Serializable
data class Task(
    @SerialName("base_total")
    val baseTotal: Double,
    val complete: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
    val name: String,
    val total: Int,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("version_id")
    val versionId: String,
    val workers: List<TaskWorker>
) {
    fun toEntity() = VersionTask(
        id = id,
        baseCost = baseTotal,
        isCompleted = complete,
        creationTime = createdAt.toLocalDateTime(),
        name = name,
        cost = total,
        updateTime = updatedAt.toLocalDateTime(),
        versionId = versionId
    )
}