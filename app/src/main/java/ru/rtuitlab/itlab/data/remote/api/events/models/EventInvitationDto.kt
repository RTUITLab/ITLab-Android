package ru.rtuitlab.itlab.data.remote.api.events.models

import android.content.Context
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601ToInstant
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@Serializable
data class EventInvitationDto(
    val id: String,
    val title: String,
    val eventType: EventTypeModel,
    val beginTime: String,
    val placeId: String,
    val placeDescription: String,
    val placeNumber: Int,
    val shiftDescription: String,
    val shiftDurationInMinutes: Double,
    val eventRole: EventRoleModel,
    val creationTime: String
) {
    fun toEventInvitation(context: Context) =
        EventInvitation(
            eventId = id,
            title = title,
            placeId = placeId,
            placeDescription = placeDescription,
            eventType = eventType,
            beginTime = getTime(context),
            duration = getDurationString(context),
            eventRole = eventRole.toUiRole()
        )

    private fun getTime(context: Context) = run {
        val shiftStartInstant = beginTime.fromIso8601ToInstant()
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
        }"
    }

    @OptIn(ExperimentalTime::class)
    private fun getDurationString(context: Context): String {
        val MINUTES_IN_HOUR = 60
        val HOURS_IN_DAY = 24
        val DAYS_IN_MONTH = 30
        val MONTHS_IN_YEAR = 12

        val minutes = shiftDurationInMinutes.roundToInt()
        val hours = (shiftDurationInMinutes / MINUTES_IN_HOUR).roundToInt()
        val days = (shiftDurationInMinutes / MINUTES_IN_HOUR / HOURS_IN_DAY).roundToInt()
        val months = (shiftDurationInMinutes / MINUTES_IN_HOUR / HOURS_IN_DAY / DAYS_IN_MONTH).roundToInt()
        val years = (shiftDurationInMinutes / MINUTES_IN_HOUR / HOURS_IN_DAY / DAYS_IN_MONTH / MONTHS_IN_YEAR).roundToInt()

        val res = context.resources
        return when {
            years > 1 -> res.getQuantityString(R.plurals.n_years, years, years)
            months > 1 -> res.getQuantityString(R.plurals.n_months, months, months)
            days > 1 -> res.getQuantityString(R.plurals.n_days, days, days)
            hours > 1 -> res.getQuantityString(R.plurals.n_hours, hours, hours)
            else -> res.getQuantityString(R.plurals.n_minutes, minutes, minutes)
        }
    }
}

data class EventInvitation(
    val eventId: String,
    val title: String,
    val placeId: String,
    val placeDescription: String,
    val eventType: EventTypeModel,
    val beginTime: String,
    val duration: String,
    val eventRole: EventRole,
)