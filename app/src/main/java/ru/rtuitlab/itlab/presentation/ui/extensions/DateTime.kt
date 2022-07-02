package ru.rtuitlab.itlab.presentation.ui.extensions

import android.content.Context
import android.util.Log
import kotlinx.datetime.*
import ru.rtuitlab.itlab.R
import java.time.DateTimeException
import java.time.format.DateTimeFormatter

private val timeZone = TimeZone.of("UTC+3")

fun Instant.plus(value: Long, unit: DateTimeUnit) = plus(value, unit, timeZone)

fun Instant.minus(value: Long, unit: DateTimeUnit) = plus(-value, unit)

fun Instant.toMoscowDateTime() = toLocalDateTime(timeZone)

fun Instant.toIso8601(): String {
	val formatter = DateTimeFormatter.ISO_INSTANT
	return formatter.format(this.toJavaInstant())
}

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
	dateTimeDelimiter: String = " ${context.resources.getString(R.string.at)}",
	parseWithTime: Boolean = true
) = try {
	// ITLab uses both normalized and non-normalized
	// ISO8601 strings. This is a workaround to always
	// parse normalized strings
	(if (this.contains("Z")) this else this + "Z")
		.fromIso8601ToInstant().run {
			val day = dayOfMonth.toString().padStart(2, '0')
			val month = monthNumber.toString().padStart(2, '0')
			if (parseWithTime) {
				val hour = hour.toString().padStart(2, '0')
				val minute = minute.toString().padStart(2, '0')
				return "$day.$month.$year$dateTimeDelimiter $hour:$minute"
			}
			"$day.$month.$year"
		}
} catch (e: DateTimeException) {
	e.printStackTrace()
	Log.e("DateTime", "Unable to parse ${if (this.contains("Z")) this else this + "Z"}")
	context.getString(R.string.time_parsing_error)
}

fun String.fromIso8601ToInstant() =
	java.time.Instant.from(
		DateTimeFormatter.ISO_DATE_TIME.parse(this)
	).toKotlinInstant().toMoscowDateTime()

fun nowAsIso8601() = java.time.Instant.now().toString()