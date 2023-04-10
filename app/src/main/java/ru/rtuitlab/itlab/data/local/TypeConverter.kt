package ru.rtuitlab.itlab.data.local

import androidx.room.TypeConverter
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.common.extensions.toZonedDateTime
import ru.rtuitlab.itlab.data.local.projects.models.ProjectFileType
import java.time.ZonedDateTime

object TypeConverter {
    @TypeConverter
    fun fromIso8601(value: String?): ZonedDateTime? =
        value?.toZonedDateTime()

    @TypeConverter
    fun toIso8601(value: ZonedDateTime?) =
        value?.toIsoString()

    @TypeConverter
    fun fromProjectFileTypeToString(value: ProjectFileType) = when(value) {
        ProjectFileType.FUNCTIONAL_TASK -> "functask"
        ProjectFileType.ATTACHMENT      -> "attach"
    }

    @TypeConverter
    fun fromStringToProjectFileType(value: String) = when (value) {
        "functask" -> ProjectFileType.FUNCTIONAL_TASK
        "attach"   -> ProjectFileType.ATTACHMENT
        else -> error("No such file type: $value")
    }
}