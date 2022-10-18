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
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EventEntity(
    @PrimaryKey val id: String,
    val address: String,
    val title: String,
    val beginTime: String,
    val endTime: String? = null,
    val typeId: Int, // FK
    val shiftsCount: Int = 0,
    val currentParticipantsCount: Int = 0,
    val targetParticipantsCount: Int = 0,
)
