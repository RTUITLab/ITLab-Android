package ru.rtuitlab.itlab.presentation.ui.components.datetime

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DateTimeRangeLabel(
    startDateTime: Pair<String, String>,
    endDateTime: Pair<String, String>
) {
    val (startDate, startTime) = startDateTime
    val (endDate, endTime) = endDateTime
    Row {
        if (startDate == endDate) {
            Text(
                text = "$startDate ",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.8f)
            )
            Text(
                text = "$startTime — $endTime",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.6f)
            )
        } else {
            Text(
                text = "$startDate, $startTime — ",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.8f)
            )
            Text(
                text = "$endDate, $endTime",
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.6f)
            )
        }
    }
}