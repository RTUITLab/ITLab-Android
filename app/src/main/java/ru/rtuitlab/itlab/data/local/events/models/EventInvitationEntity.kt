package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventInvitationDto
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRoleModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

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
    ],
    primaryKeys = ["id", "placeId"],
    indices = [
        Index(value = ["id", "placeId"]),
        Index("typeId"),
        Index("roleId")
    ]
)
data class EventInvitationEntity(
    val id: String,
    val title: String,
    val typeId: String, // FK
    val beginTime: String,
    val placeId: String,
    val roleId: String, // FK
    val placeDescription: String,
    val placeNumber: Int,
    val shiftDescription: String,
    val shiftDurationInMinutes: Double,
    val creationTime: String
)

data class EventInvitationWithTypeAndRole(
    @Embedded val eventInvitationEntity: EventInvitationEntity,
    @Relation(
        parentColumn = "roleId",
        entityColumn = "id"
    )
    val role: EventRoleModel,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    )
    val eventType: EventTypeModel
) {
    fun toInvitationDto() = EventInvitationDto(
        id = eventInvitationEntity.id,
        title = eventInvitationEntity.title,
        placeId = eventInvitationEntity.placeId,
        placeDescription = eventInvitationEntity.placeDescription,
        eventType = eventType,
        beginTime = eventInvitationEntity.beginTime,
        shiftDurationInMinutes = eventInvitationEntity.shiftDurationInMinutes,
        placeNumber = eventInvitationEntity.placeNumber,
        shiftDescription = eventInvitationEntity.shiftDescription,
        eventRole = role,
        creationTime = eventInvitationEntity.creationTime
    )
}