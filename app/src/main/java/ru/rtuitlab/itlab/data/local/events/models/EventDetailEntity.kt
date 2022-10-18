package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*

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


data class EventWithShifts(
    @Embedded val event: EventDetailEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val shifts: List<ShiftWithPlaces>
)