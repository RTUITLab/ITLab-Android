package ru.rtuitlab.itlab.data.remote.api.events.models


import kotlinx.serialization.Serializable

@Serializable
data class EventSalary(
    val eventId: String,
    val created: String,
    val shiftSalaries: List<Int>,
    val placeSalaries: List<Int>,
    val authorId: String,
    val modificationDate: String,
    val count: Int,
    val description: String
)