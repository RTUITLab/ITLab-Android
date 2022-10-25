package ru.rtuitlab.itlab.data.remote.api.events.models


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.PlaceEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"]
        )
    ]
)
@Serializable
data class EventPlaceSalary(
    @PrimaryKey val placeId: String,
    val count: Int,
    val description: String
)