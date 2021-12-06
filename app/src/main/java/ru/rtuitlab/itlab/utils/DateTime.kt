package ru.rtuitlab.itlab.utils

import android.content.Context
import kotlinx.datetime.*
import ru.rtuitlab.itlab.R
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter

private val timeZone = TimeZone.of("UTC+3")

fun Instant.plus(value: Long, unit: DateTimeUnit) = plus(value, unit, timeZone)

fun Instant.minus(value: Long, unit: DateTimeUnit) = plus(-value, unit)

fun Instant.toMoscowDateTime() = toLocalDateTime(timeZone)

fun Long.toMoscowDateTime() = Instant.fromEpochMilliseconds(this).toMoscowDateTime()

fun Long.toClientDate() = toMoscowDateTime().run {
	val day = dayOfMonth.toString().padStart(2, '0')
	val month = monthNumber.toString().padStart(2, '0')
	"$day.$month.$year"
}

fun String.fromIso8601(context: Context) =
	java.time.Instant.from(
		DateTimeFormatter.ISO_DATE_TIME.parse(this)
	).toKotlinInstant().toMoscowDateTime().run {
		val day = dayOfMonth.toString().padStart(2, '0')
		val month = monthNumber.toString().padStart(2, '0')
		val hour = hour.toString().padStart(2, '0')
		val minute = minute.toString().padStart(2, '0')
		"$day.$month.$year ${context.getString(R.string.at)} $hour:$minute"
	}
