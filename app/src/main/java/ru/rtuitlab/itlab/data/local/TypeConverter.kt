package ru.rtuitlab.itlab.data.local

import androidx.room.TypeConverter
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.common.extensions.toLocalDateTime
import java.time.LocalDateTime

class TypeConverter {
    @TypeConverter
    fun fromIso8601(value: String): LocalDateTime =
        value.toLocalDateTime()

    @TypeConverter
    fun toIso8601(value: LocalDateTime?) =
        value?.toIsoString()
}