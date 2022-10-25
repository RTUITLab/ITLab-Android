package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventPlaceSalary

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

data class PlaceWithUsersAndSalary(
    @Embedded val place: PlaceEntity,
    @Relation(
        entity = UserEventRoleEntity::class,
        parentColumn = "id",
        entityColumn = "placeId"
    )
    val usersWithRoles: List<UserWithRole>,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    val salary: EventPlaceSalary? = null
)