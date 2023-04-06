package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.Serializable

@Serializable
data class Files(
    val attach: List<ProjectFile>?,
    val functask: List<ProjectFile>?
)