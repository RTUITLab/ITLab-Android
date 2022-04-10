package ru.rtuitlab.itlab.data.remote.api.reports.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Assignees(
    @SerialName("reporter")
    val reporterId: String, // The person that applied the report
    @SerialName("implementer")
    val implementerId: String // The person that this report is about
)