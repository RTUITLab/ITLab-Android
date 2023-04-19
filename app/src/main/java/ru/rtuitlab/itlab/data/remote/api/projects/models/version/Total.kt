package ru.rtuitlab.itlab.data.remote.api.projects.models.version


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Total(
    val hours: Int,
    val sum: Int,
    @SerialName("base_sum")
    val baseSum: Float
)