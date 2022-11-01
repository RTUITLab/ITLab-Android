package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EventDetailEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val address: String,
    val eventId: String // FK
)


data class EventWithShiftsAndSalary(
    @Embedded val eventDetail: EventDetailEntity,
    @Relation(
        entity = EventEntity::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val eventInfo: EventWithType,
    @Relation(
        entity = ShiftEntity::class,
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val shifts: List<ShiftWithPlacesAndSalary>,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val salary: EventSalaryEntity? = null
) {

    @Ignore
    val beginTime = shifts.minOf { it.shift.beginTime }

    @Ignore
    val endTime = shifts.maxOf { it.shift.endTime }
}