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
data class ShiftEntity(
    @PrimaryKey val id: String,
    val beginTime: String,
    val endTime: String,
    val description: String? = null,
    val eventId: String // FK
)

data class ShiftWithPlaces(
    @Embedded val shift: ShiftEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "shiftId"
    )
    val places: List<PlaceWithRoles>
)