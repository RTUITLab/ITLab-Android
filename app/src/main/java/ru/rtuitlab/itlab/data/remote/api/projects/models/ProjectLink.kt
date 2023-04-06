package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.Serializable

@Serializable
data class ProjectLink(
    val href: String,
    val rel: String
)