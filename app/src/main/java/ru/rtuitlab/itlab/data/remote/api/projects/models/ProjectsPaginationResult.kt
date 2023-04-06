package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectsPaginationResult(
    val count: Int,
    @SerialName("has_more")
    val hasMore: Boolean,
    val items: List<Project>,
    val limit: Int,
    val links: List<ProjectLink>,
    val offset: Int,
    @SerialName("total_result")
    val totalResult: Int
)