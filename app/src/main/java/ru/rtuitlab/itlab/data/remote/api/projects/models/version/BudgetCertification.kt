package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.local.projects.models.BudgetCertificationEntity
import ru.rtuitlab.itlab.data.local.projects.models.VersionRoleTotalEntity

@Serializable
data class BudgetCertification(
    @SerialName("certify_by")
    val certifyBy: String,
    @SerialName("certify_date")
    val certifyDate: String,
    val tasks: List<Task>,
    val total: Map<String, Total>, // roleId to Total
    @SerialName("total_sum")
    val totalSum: Int
) {
    fun toEntity(versionId: String) = BudgetCertificationEntity(
        versionId = versionId,
        certificationIssuerId = certifyBy,
        certificationDateTime = certifyDate.toZonedDateTime(),
        totalCost = totalSum
    )

    fun toTaskTotals(versionId: String) = total.map { (roleId, total) ->
        VersionRoleTotalEntity(
            versionId = versionId,
            roleId = roleId,
            totalCost = total.sum,
            totalHours = total.hours
        )
    }
}