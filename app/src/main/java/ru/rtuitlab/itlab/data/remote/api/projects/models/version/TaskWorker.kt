package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.local.projects.models.TaskWorkerEntity

@Serializable
data class TaskWorker(
    @SerialName("base_sum")
    val baseSum: Double,
    @SerialName("created_at")
    val createdAt: String,
    val hours: Int,
    val role: String,
    @SerialName("role_id")
    val roleId: String,
    val sum: Int
) {
    fun toEntity(taskId: String) = TaskWorkerEntity(
        taskId = taskId,
        baseRate = baseSum,
        creationTime = createdAt.toLocalDateTime(),
        hours = hours,
        roleName = role,
        roleId = roleId,
        rate = sum
    )
}