package ru.rtuitlab.itlab.common.extensions

import android.content.Context
import android.util.Log
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import ru.rtuitlab.itlab.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val timeZone = TimeZone.of("UTC+3")

private const val ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

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

fun Long.toIsoString(
	addOneDay: Boolean
) = Date(this + if (addOneDay) 86_400_000 else 0).toIsoString()

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

/**
 * Parses ISO8601 string and returns a pair of Strings representing date and time respectively
 */
fun String.fromIso8601ToDateTime(
	context: Context,
	includeSeconds: Boolean = true
): Pair<String, String> = try {
	// ITLab uses both normalized and non-normalized
	// ISO8601 strings. This is a workaround to always
	// parse normalized strings
	(if (this.contains("Z")) this else this + "Z")
		.fromIso8601ToInstant().run {
			val day = dayOfMonth.toString().padStart(2, '0')
			val month = monthNumber.toString().padStart(2, '0')
			val hour = hour.toString().padStart(2, '0')
			val minute = minute.toString().padStart(2, '0')
			val second = second.toString().padStart(2, '0')

			"$day.$month.$year" to if (includeSeconds) "$hour:$minute:$second" else "$hour:$minute"
		}
} catch (e: DateTimeException) {
	e.printStackTrace()
	Log.e("DateTime", "Unable to parse ${if (this.contains("Z")) this else this + "Z"}")
	context.getString(R.string.time_parsing_error) to ""
}

fun String.toZonedDateTime(): ZonedDateTime =
	ZonedDateTime
		.parse(this, DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z")))

fun ZonedDateTime.toIsoString(): String =
	format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z")))

fun String.fromIso8601ToInstant() =
	java.time.Instant.from(
		DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()).parse(this)
	).toKotlinInstant().toMoscowDateTime()

fun Date.toIsoString(): String {
	val dateFormat: DateFormat = SimpleDateFormat(ISO_8601_24H_FULL_FORMAT, Locale.getDefault())
	return dateFormat.format(this)
}

fun nowAsIso8601() = Date().toIsoString()
const val endOfTimes = "9999-12-31T23:59:59Z"