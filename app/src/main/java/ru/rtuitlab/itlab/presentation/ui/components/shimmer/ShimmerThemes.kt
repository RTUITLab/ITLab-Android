package ru.rtuitlab.itlab.presentation.ui.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerTheme
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

object ShimmerThemes {
    val defaultShimmerTheme = ShimmerTheme(
        animationSpec = infiniteRepeatable(
            animation = tween(
                800,
                easing = LinearEasing,
                delayMillis = 1_500,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        blendMode = BlendMode.DstIn,
        rotation = 15.0f,
        shaderColors = listOf(
            Color.LightGray.copy(alpha = .6f),
            Color.LightGray.copy(alpha = .2f),
            Color.LightGray.copy(alpha = .6f),
        ),
        shaderColorStops = listOf(
            0.0f,
            0.5f,
            1.0f,
        ),
        shimmerWidth = 400.dp,
    )

    val accentShimmerTheme = ShimmerTheme(
        animationSpec = infiniteRepeatable(
            animation = tween(
                800,
                easing = LinearEasing,
                delayMillis = 1_500,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        blendMode = BlendMode.DstIn,
        rotation = 15.0f,
        shaderColors = listOf(
            AppColors.accent.value.copy(alpha = .6f),
            AppColors.accent.value.copy(alpha = .2f),
            AppColors.accent.value.copy(alpha = .6f),
        ),
        shaderColorStops = listOf(
            0.0f,
            0.5f,
            1.0f,
        ),
        shimmerWidth = 400.dp,
    )
}