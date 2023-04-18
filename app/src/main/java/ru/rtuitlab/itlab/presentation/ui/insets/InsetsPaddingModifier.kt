package ru.rtuitlab.itlab.presentation.ui.insets

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.horizontalNavigationBarsPadding() =
    composed {
        windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
    }