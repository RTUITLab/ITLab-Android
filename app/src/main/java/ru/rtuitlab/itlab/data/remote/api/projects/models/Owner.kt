package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.projects.models.ProjectOwner

@Serializable
data class Owner(
    @SerialName("user_id")
    val userId: String
) {
    fun toProjectOwnerEntity(projectId: String) =
        ProjectOwner(userId, projectId)
}