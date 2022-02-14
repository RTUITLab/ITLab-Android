package ru.rtuitlab.itlab.presentation.ui.extensions

import android.content.Context
import kotlinx.datetime.*
import ru.rtuitlab.itlab.R
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter

private val timeZone = TimeZone.of("UTC+3")

fun Instant.plus(value: Long, unit: DateTimeUnit) = plus(value, unit, timeZone)

fun Instant.minus(value: Long, unit: DateTimeUnit) = plus(-value, unit)

fun Instant.toMoscowDateTime() = toLocalDateTime(timeZone)

fun LocalDate.toUiString() = run {
	val day = dayOfMonth.toString().padStart(2, '0')
	val month = monthNumber.toString().padStart(2, '0')
	"$day.$month.$year"
}

fun Long.toMoscowDateTime() = Instant.fromEpochMilliseconds(this).toMoscowDateTime()

fun Long.toClientDate() = toMoscowDateTime().run {
	val day = dayOfMonth.toString().padStart(2, '0')
	val month = monthNumber.toString().padStart(2, '0')
	"$day.$month.$year"
}

fun String.fromIso8601(
	context: Context,
	dateTimeDelimiter: String = " ${context.resources.getString(R.string.at)}"
) =
	fromIso8601ToInstant().run {
		val day = dayOfMonth.toString().padStart(2, '0')
		val month = monthNumber.toString().padStart(2, '0')
		val hour = hour.toString().padStart(2, '0')
		val minute = minute.toString().padStart(2, '0')
		"$day.$month.$year$dateTimeDelimiter $hour:$minute"
	}

fun String.fromIso8601ToInstant() =
	java.time.Instant.from(
		DateTimeFormatter.ISO_DATE_TIME.parse(this)
	).toKotlinInstant().toMoscowDateTime()

fun nowAsIso8601() = java.time.Instant.now().toString()