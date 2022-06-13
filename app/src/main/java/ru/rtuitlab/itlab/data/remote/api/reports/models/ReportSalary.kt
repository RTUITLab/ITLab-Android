package ru.rtuitlab.itlab.data.remote.api.reports.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportSalary(
    val reportId: String,
    @SerialName("approved")
    val approvingDate: String,
    val approverId: String,
    val count: Int,
    val description: String?
)