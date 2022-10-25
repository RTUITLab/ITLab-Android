package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventTypeModel::class,
            parentColumns = ["id"],
            childColumns = ["typeId"]
        ),
        ForeignKey(
            entity = EventRoleModel::class,
            parentColumns = ["id"],
            childColumns = ["roleId"]
        )
    ]
)
data class UserEventEntity(
    @PrimaryKey val id: String,
    val address: String,
    val title: String,
    val typeId: String,
    val beginTime: String,
    val roleId: String
)

data class UserEventWithTypeAndRole(
    @Embedded val userEvent: UserEventEntity,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    )
    val eventType: EventTypeModel,
    @Relation(
        parentColumn = "roleId",
        entityColumn = "id"
    )
    val role: EventRoleModel
) {
    fun toUserEventModel() = UserEventModel(
        id = userEvent.id,
        address = userEvent.address,
        title = userEvent.title,
        beginTime = userEvent.beginTime,
        eventType = eventType,
        role = role
    )
}
