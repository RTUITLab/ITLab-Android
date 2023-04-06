package ru.rtuitlab.itlab.data.remote.api.projects.models


import kotlinx.serialization.Serializable

@Serializable
data class Deadlines(
    val hard: String,
    val soft: String
)