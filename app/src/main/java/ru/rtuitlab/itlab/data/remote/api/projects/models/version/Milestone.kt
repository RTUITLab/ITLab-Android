package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.Serializable

@Serializable
data class Milestone(
    val name: String,
    val url: String
)