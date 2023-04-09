package ru.rtuitlab.itlab.data.local.projects.models

import androidx.room.Entity
import java.time.LocalDateTime

@Entity(
    primaryKeys = ["taskId", "roleId"]
)
data class TaskWorkerEntity(
    val taskId: String,
    val baseRate: Double,
    val creationTime: LocalDateTime,
    val hours: Int,
    val roleName: String,
    val roleId: String,
    val rate: Int
)
