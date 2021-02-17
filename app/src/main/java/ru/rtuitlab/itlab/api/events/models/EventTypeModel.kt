package ru.rtuitlab.itlab.api.events.models

import kotlinx.serialization.Serializable

@Serializable
data class EventTypeModel(
    val id: String,
    val title: String? = null,
    val description: String? = null
)