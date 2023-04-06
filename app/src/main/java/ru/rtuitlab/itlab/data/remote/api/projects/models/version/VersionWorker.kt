package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionWorker(
    @SerialName("base_sum")
    val baseSum: Double,
    @SerialName("created_at")
    val createdAt: String,
    val hours: Int,
    val role: String,
    @SerialName("role_id")
    val roleId: String,
    val sum: Int
)