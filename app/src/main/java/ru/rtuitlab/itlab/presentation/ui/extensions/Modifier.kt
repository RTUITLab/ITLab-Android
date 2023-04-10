package ru.rtuitlab.itlab.presentation.ui.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

fun Modifier.ignoreOutsidePaddingOf(size: Dp) =
    layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + size.roundToPx(), // Adding horizontal padding
            )
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }