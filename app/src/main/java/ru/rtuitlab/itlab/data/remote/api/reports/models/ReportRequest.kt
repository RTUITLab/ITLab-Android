package ru.rtuitlab.itlab.data.remote.api.reports.models

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
	val name: String? = null,
	val text: String
) {
}