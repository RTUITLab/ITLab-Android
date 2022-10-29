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
    ],
    primaryKeys = ["userId", "roleId", "placeId"],
    indices = [Index(value = ["userId", "roleId", "placeId"], unique = true)]
)
data class UserEventRoleEntity(
    val userId: String,
    val roleId: String,
    val placeId: String,
    val participationType: UserParticipationType
)

data class UserWithRole(
    @Embedded val userRole: UserEventRoleEntity,
    @Relation(
        parentColumn = "roleId",
        entityColumn = "id"
    )
    val role: EventRoleModel
)