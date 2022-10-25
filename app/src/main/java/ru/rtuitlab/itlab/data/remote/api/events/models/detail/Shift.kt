package ru.rtuitlab.itlab.data.remote.api.events.models.detail


import android.content.Context
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.local.events.models.ShiftEntity
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601ToInstant
import java.time.format.TextStyle
import java.util.*

@Serializable
data class Shift(
    val id: String,
    val beginTime: String,
    val endTime: String,
    val description: String? = null,
    val places: List<Place>
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

    val duration = endTime.fromIso8601ToInstant().hour - beginTime.fromIso8601ToInstant().hour

    fun toShiftEntity(eventId: String) = ShiftEntity(
        id = id,
        beginTime = beginTime,
        endTime = endTime,
        description = description,
        eventId = eventId
    )

    fun extractPlaceEntities() = places.map {
        it.toPlaceEntity(id)
    }

    fun extractUserRoles() = places.flatMap {
        it.extractUserRoles()
    }
}