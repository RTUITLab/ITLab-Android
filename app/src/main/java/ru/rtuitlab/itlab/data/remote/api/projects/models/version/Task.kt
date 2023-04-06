package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val workers: List<VersionWorker>
)