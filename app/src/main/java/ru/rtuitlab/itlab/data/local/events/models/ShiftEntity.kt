package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary

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

data class ShiftWithPlacesAndSalary(
    @Embedded val shift: ShiftEntity,
    @Relation(
        entity = PlaceEntity::class,
        parentColumn = "id",
        entityColumn = "shiftId"
    )
    val places: List<PlaceWithUsersAndSalary>,
    @Relation(
        parentColumn = "id",
        entityColumn = "shiftId"
    )
    val salary: EventShiftSalary? = null
)