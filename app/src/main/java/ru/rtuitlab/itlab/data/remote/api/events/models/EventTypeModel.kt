package ru.rtuitlab.itlab.data.remote.api.events.models

import kotlinx.serialization.Serializable

@Serializable
data class EventTypeModel(
    val id: String,
    val title: String,
    val description: String? = null
)