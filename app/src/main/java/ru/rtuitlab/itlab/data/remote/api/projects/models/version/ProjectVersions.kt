package ru.rtuitlab.itlab.data.remote.api.projects.models.version

import kotlinx.serialization.Serializable

@Serializable
data class ProjectVersions(
    val versions: List<ProjectVersion>
)
