package ru.rtuitlab.itlab.presentation.ui.components.datetime

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DateTimeLabel(
    date: String,
    time: String
) {
    Row {
        Text(
            text = "$date ",
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(.8f)
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(.6f)
        )
    }
}