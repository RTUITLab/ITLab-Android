package ru.rtuitlab.itlab.data.remote.api.events.models


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity

@Serializable
data class EventSalary(
    val eventId: String,
    val created: String,
    val shiftSalaries: List<EventShiftSalary>,
    val placeSalaries: List<EventPlaceSalary>,
    val authorId: String,
    val modificationDate: String,
    val count: Int,
    val description: String
) {
    fun toEventSalaryEntity() = EventSalaryEntity(
        eventId = eventId,
        createdAt = created,
        authorId = authorId,
        modificationDate = modificationDate,
        count = count,
        description = description
    )
}