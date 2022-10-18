package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        ),
        ForeignKey(
            entity = EventRoleModel::class,
            parentColumns = ["id"],
            childColumns = ["roleId"]
        ),
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"]
        )
    ]
)
data class EventRoleEntity(
    @PrimaryKey val userId: String,
    val roleId: String, // FK
    val placeId: String, // FK
    val creationTime: String? = null,
    val doneTime: String? = null
)

data class RoleWithUser(
    @Embedded val role: EventRoleEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val user: UserEntity
)