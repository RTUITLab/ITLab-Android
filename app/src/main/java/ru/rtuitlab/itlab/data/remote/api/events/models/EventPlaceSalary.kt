package ru.rtuitlab.itlab.data.remote.api.events.models


import kotlinx.serialization.Serializable

@Serializable
data class EventPlaceSalary(
    val placeId: String,
    val count: Int,
    val description: String
)