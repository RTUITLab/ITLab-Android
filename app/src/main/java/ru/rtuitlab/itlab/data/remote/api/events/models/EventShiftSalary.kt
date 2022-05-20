package ru.rtuitlab.itlab.data.remote.api.events.models


import kotlinx.serialization.Serializable

@Serializable
data class EventShiftSalary(
    val shiftId: String,
    val count: Int,
    val description: String
)