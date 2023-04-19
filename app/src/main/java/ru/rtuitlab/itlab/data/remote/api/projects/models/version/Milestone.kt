package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.projects.models.MilestoneEntity

@Serializable
data class Milestone(
    val name: String,
    val url: String
) {
    fun toEntity(versionId: String) = MilestoneEntity(
        name = name,
        url = url,
        versionId = versionId
    )
}