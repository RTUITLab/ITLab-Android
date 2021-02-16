package ru.rtuitlab.itlab.utils

import kotlinx.datetime.*

private val timeZone = TimeZone.of("UTC+3")

fun Instant.plus(value: Long, unit: DateTimeUnit) = plus(value, unit, timeZone)

fun Instant.minus(value: Long, unit: DateTimeUnit) = plus(-value, unit)

fun Instant.toMoscowDateTime() = toLocalDateTime(timeZone)