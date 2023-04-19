package ru.rtuitlab.itlab.data.remote.api.projects

import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R

enum class SortingDirection(val literal: String) {
    ASC("asc"),
    DESC("desc")
}

enum class SortingField(
    val apiLiteral: String,
    val localLiteral: String,
    @StringRes val nameResource: Int
) {
    NAME(
        apiLiteral = "name",
        localLiteral = "name",
        nameResource = R.string.by_name
    ),
    DATE(
        apiLiteral = "created_at",
        localLiteral = "creationDateTime",
        nameResource = R.string.by_date
    )
}