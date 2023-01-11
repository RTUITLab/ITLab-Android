package ru.rtuitlab.itlab.data.remote.api.events.models


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.ShiftEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"]
        )
    ]
)
@Serializable
data class EventShiftSalary(
    @PrimaryKey val shiftId: String,
    val count: Int,
    val description: String
)