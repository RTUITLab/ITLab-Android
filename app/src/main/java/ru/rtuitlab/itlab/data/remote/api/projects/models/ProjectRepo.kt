package ru.rtuitlab.itlab.data.remote.api.projects.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectRepo(
    val name: String,
    val url: String
)
