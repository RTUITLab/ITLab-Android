package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaceEntity(
    @PrimaryKey val id: String,
    val targetParticipantsCount: Int,
    val description: String? = null,
    val shiftId: String // FK
)

data class PlaceWithRoles(
    @Embedded val place: PlaceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    val roles: List<EventRoleEntity>
)