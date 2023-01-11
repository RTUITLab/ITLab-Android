package ru.rtuitlab.itlab.presentation.ui.components.modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.components.FAB_DEFAULT_HEIGHT
import ru.rtuitlab.itlab.presentation.ui.components.FabSpacing

/**
 * [PaddingModifier][androidx.compose.foundation.layout.PaddingModifier]
 * that is configured to take navigation FAB into account, adding necessary
 * padding to [bottom].
 * It is used to deal with content obstruction.
 */
@Stable
fun Modifier.fabAwarePadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) =
    this.then(
        Modifier.padding(start, top, end, bottom + FabSpacing + FAB_DEFAULT_HEIGHT)
    )

/**
 * [PaddingModifier][androidx.compose.foundation.layout.PaddingModifier]
 * that is configured to take navigation FAB into account, adding necessary
 * padding to the *bottom* component.
 * It is used to deal with content obstruction.
 */
@Stable
fun Modifier.fabAwarePadding(all: Dp) =
    this.then(
        Modifier.padding(all, all, all, all + FabSpacing + FAB_DEFAULT_HEIGHT)
    )

/**
 * [PaddingModifier][androidx.compose.foundation.layout.PaddingModifier]
 * that is configured to take navigation FAB into account, adding necessary
 * padding to the *bottom* component.
 * It is used to deal with content obstruction.
 */
@Stable
fun Modifier.fabAwarePadding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
) =
    this.then(
        Modifier.padding(horizontal, vertical, horizontal, vertical + FabSpacing + FAB_DEFAULT_HEIGHT)
    )