package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import java.util.*

@Composable
fun PrimaryButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	text: String,
	textWrapper: @Composable RowScope.(text :@Composable () -> Unit) -> Unit
) {
	Button(
		modifier = modifier
			.clipToBounds(),
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(
			backgroundColor = Color.Transparent
		),
		elevation = ButtonDefaults.elevation(
			defaultElevation = 0.dp,
			pressedElevation = 0.dp
		)
	) {
		textWrapper {
			Text(
				text = text?.uppercase(Locale.getDefault()) ?: "",
				color = AppColors.accent.collectAsState().value,
				fontSize = 14.sp,
				fontWeight = FontWeight(500),
				lineHeight = 22.sp
			)
		}
	}
}