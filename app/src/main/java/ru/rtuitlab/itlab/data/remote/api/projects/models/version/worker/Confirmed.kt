package ru.rtuitlab.itlab.data.remote.api.projects.models.version.worker


import kotlinx.serialization.Serializable

@Serializable
data class Confirmed(
    val confirmed: Boolean
)