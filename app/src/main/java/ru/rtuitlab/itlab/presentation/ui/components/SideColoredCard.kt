package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SideColoredCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.primary,
    colorSide: ColorSide = ColorSide.START,
    stripSize: Dp = 4.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(modifier),
        shape = shape,
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
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                content()
            }
        }
    }
}

enum class ColorSide {
    START, END
}