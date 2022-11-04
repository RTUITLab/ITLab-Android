package ru.rtuitlab.itlab.data.local.events.models

import android.content.Context
import androidx.room.*
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToInstant
import java.time.format.TextStyle
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShiftEntity(
    @PrimaryKey val id: String,
    val beginTime: String,
    val endTime: String,
    val description: String? = null,
    val eventId: String // FK
) {
    fun getTime(context: Context) = run {
        val shiftStartInstant = beginTime.fromIso8601ToInstant()
        val shiftEndInstant = endTime.fromIso8601ToInstant()
        "${
            shiftStartInstant.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        }, ${
            beginTime.fromIso8601(
                context,
                ""
            )
        } — ${shiftEndInstant.hour.toString().padStart(2, '0')}:${
            shiftEndInstant.minute.toString().padStart(2, '0')
        }"
    }

    @Ignore
    val duration = endTime.fromIso8601ToInstant().hour - beginTime.fromIso8601ToInstant().hour
}

data class ShiftWithPlacesAndSalary(
    @Embedded val shift: ShiftEntity,
    @Relation(
        entity = PlaceEntity::class,
        parentColumn = "id",
        entityColumn = "shiftId"
    )
    val places: List<PlaceWithUsersAndSalary>,
    @Relation(
        parentColumn = "id",
        entityColumn = "shiftId"
    )
    val salary: EventShiftSalary? = null
)