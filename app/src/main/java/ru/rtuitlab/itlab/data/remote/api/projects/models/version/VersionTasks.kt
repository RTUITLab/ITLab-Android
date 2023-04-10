package ru.rtuitlab.itlab.data.remote.api.projects.models.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionTasks(
    val tasks: List<Task>,
    val total: Map<String, Total>,
    @SerialName("total_base_sum")
    val totalBaseCost: Float,
    @SerialName("total_sum")
    val totalCost: Int
)
