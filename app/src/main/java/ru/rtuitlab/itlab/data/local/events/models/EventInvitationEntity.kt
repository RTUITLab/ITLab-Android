package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventTypeModel::class,
            parentColumns = ["id"],
            childColumns = ["typeId"]
        ),
        ForeignKey(
            entity = UserEventRoleEntity::class,
            parentColumns = ["userId"],
            childColumns = ["roleId"]
        )
    ]
)
data class EventInvitationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val typeId: String, // FK
    val beginTime: String,
    val placeId: String,
    val placeDescription: String,
    val placeNumber: Int,
    val shiftDescription: String,
    val shiftDurationInMinutes: Double,
    val roleId: String, // FK
    val creationTime: String
)
