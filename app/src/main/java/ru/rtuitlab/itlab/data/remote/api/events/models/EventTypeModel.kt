package ru.rtuitlab.itlab.data.remote.api.events.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class EventTypeModel(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null
)