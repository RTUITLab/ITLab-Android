package ru.rtuitlab.itlab.data.remote.api.projects

import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R

enum class SortingDirection(val literal: String) {
    ASC("asc"),
    DESC("desc")
}

enum class SortingField(
    val literal: String,
    @StringRes val nameResource: Int
) {
    NAME(
        literal = "name",
        nameResource = R.string.by_name
    ),
    DATE(
        literal = "creationDateTime",
        nameResource = R.string.by_date
    )
}