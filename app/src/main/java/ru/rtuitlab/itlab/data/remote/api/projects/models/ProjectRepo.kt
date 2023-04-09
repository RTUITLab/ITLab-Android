package ru.rtuitlab.itlab.data.remote.api.projects.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.projects.models.ProjectRepoEntity

@Serializable
data class ProjectRepo(
    val name: String,
    val url: String
) {
    fun toProjectRepoEntity(projectId: String) = ProjectRepoEntity(
        name = name,
        url = url,
        projectId = projectId
    )
}
