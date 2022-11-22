package ru.rtuitlab.itlab.presentation.ui.components.datetime

import androidx.compose.foundation.layout.Row
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle

@Composable
fun DateTimeRangeLabel(
    startDateTime: Pair<String, String>,
    endDateTime: Pair<String, String>,
    textStyle: TextStyle = LocalTextStyle.current,
    dateAlpha: Float = .8f,
    timeAlpha: Float = .6f,
) {
    val (startDate, startTime) = startDateTime
    val (endDate, endTime) = endDateTime

    val defaultSpanStyle = textStyle.toSpanStyle()
    val color = LocalContentColor.current

    val mainAnnotatedString: (text: String) -> AnnotatedString = { text ->
        AnnotatedString(
            text = text,
            spanStyle = defaultSpanStyle.copy(
                color = color.copy(dateAlpha)
            )
        )
    }
    val secondaryAnnotatedString: (text: String) -> AnnotatedString = { text ->
        AnnotatedString(
            text = text,
            spanStyle = defaultSpanStyle.copy(
                color = color.copy(timeAlpha)
            )
        )
    }


    Row {
        if (startDate == endDate) {
            Text(
                text = AnnotatedString.Builder().apply {
                    append(
                        mainAnnotatedString("$startDate ")
                    )
                    append(
                        secondaryAnnotatedString("$startTime — $endTime")
                    )
                }.toAnnotatedString()
            )
        } else {
            Text(
                text = AnnotatedString.Builder().apply {
                    append(
                        mainAnnotatedString("$startDate, ")
                    )
                    append(
                        secondaryAnnotatedString(startTime)
                    )
                    append(" — \n")
                    append(
                        mainAnnotatedString("$endDate, ")
                    )
                    append(
                        secondaryAnnotatedString(endTime)
                    )
                }.toAnnotatedString()
            )
        }
    }
}