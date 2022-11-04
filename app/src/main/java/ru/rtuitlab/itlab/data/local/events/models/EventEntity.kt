package ru.rtuitlab.itlab.data.local.events.models

import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventTypeModel::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("typeId")
    ]
)
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val beginTime: String,
    val endTime: String? = null,
    val typeId: String, // FK
    val address: String,
    val shiftsCount: Int = 0,
    val currentParticipantsCount: Int = 0,
    val targetParticipantsCount: Int = 0,
    val participating: Boolean = false
)

data class EventWithType(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "id"
    )
    val type: EventTypeModel
) {
    fun toEventModel() = EventModel(
        id = event.id,
        title = event.title,
        beginTime = event.beginTime,
        endTime = event.endTime,
        eventType = type,
        address = event.address,
        shiftsCount = event.shiftsCount,
        currentParticipantsCount = event.currentParticipantsCount,
        targetParticipantsCount = event.targetParticipantsCount,
        participating = event.participating
    )
}