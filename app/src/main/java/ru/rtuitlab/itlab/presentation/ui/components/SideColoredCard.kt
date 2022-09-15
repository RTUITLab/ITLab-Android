package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun SideColoredCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = AppColors.accent.collectAsState().value,
    colorSide: ColorSide = ColorSide.START,
    stripSize: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        elevation = elevation,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        border = border
    ) {
        Box {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawRect(
                    color = color
                )
            }
            Surface(
                modifier = Modifier
                    .padding(
                        start = if (colorSide == ColorSide.START) stripSize else 0.dp,
                        end = if (colorSide == ColorSide.END) stripSize else 0.dp
                    )
                    .fillMaxWidth()
            ) {
                content()
            }
        }
    }
}

enum class ColorSide {
    START, END
}