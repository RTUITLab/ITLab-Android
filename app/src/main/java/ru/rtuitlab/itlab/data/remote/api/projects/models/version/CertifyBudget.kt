package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CertifyBudget(
    @SerialName("certify_by")
    val certifyBy: String,
    @SerialName("certify_date")
    val certifyDate: String,
    val tasks: List<Task>,
    val total: Map<String, Total>, // roleId to Total
    @SerialName("total_sum")
    val totalSum: Int
)