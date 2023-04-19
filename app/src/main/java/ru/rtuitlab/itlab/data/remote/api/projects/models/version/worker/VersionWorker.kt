package ru.rtuitlab.itlab.data.remote.api.projects.models.version.worker


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.local.projects.models.Worker

@Serializable
data class VersionWorker(
    @SerialName("assigned_by")
    val assignedBy: String,
    @SerialName("base_salary_per_hour")
    val baseSalaryPerHour: Double,
    val confirmed: Confirmed,
    @SerialName("created_at")
    val createdAt: String,
    val id: String,
    val modifier: Int,
    val role: String,
    @SerialName("role_id")
    val roleId: String,
    @SerialName("salary_per_hour")
    val salaryPerHour: Int,
    @SerialName("salary_per_month")
    val salaryPerMonth: Int,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("user_id")
    val userId: String,
    @SerialName("version_id")
    val versionId: String,
    @SerialName("work_hours")
    val workHours: Int
) {
    fun toWorkerEntity() = Worker(
        id = id,
        appointerId = assignedBy,
        baseHourlyRate = baseSalaryPerHour,
        isConfirmed = confirmed.confirmed,
        creationTime = createdAt.toZonedDateTime(),
        rateModifier = modifier,
        role = role,
        roleId = roleId,
        hourlyRate = salaryPerHour,
        monthlySalary = salaryPerMonth,
        updateTime = updatedAt?.toZonedDateTime(),
        userId = userId,
        versionId = versionId,
        workHours = workHours
    )
}