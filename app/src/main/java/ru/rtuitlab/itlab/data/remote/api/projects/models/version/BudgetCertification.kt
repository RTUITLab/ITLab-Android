package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import ru.rtuitlab.itlab.data.local.projects.models.BudgetCertificationEntity

@Serializable
data class BudgetCertification(
    @SerialName("certify_by")
    val certifyBy: String,
    @SerialName("certify_date")
    val certifyDate: String,
    val tasks: List<Task>,
    val total: Map<String, Total>, // roleName to Total
    @SerialName("total_sum")
    val totalSum: Int
) {
    fun toEntity(versionId: String) = BudgetCertificationEntity(
        versionId = versionId,
        certificationIssuerId = certifyBy,
        certificationDateTime = certifyDate.toLocalDateTime(),
        totalCost = totalSum
    )
}