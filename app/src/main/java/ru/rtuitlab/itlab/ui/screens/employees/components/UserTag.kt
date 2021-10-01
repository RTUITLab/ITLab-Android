package ru.rtuitlab.itlab.ui.screens.employees.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.ui.theme.ITLabTheme

@Composable
fun UserTagComponent(
	tag: String
) {
	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(15.dp))
			.background(colorResource(R.color.tag_color))
			.padding(
				start = 10.dp,
				end = 10.dp,
				top = 6.dp,
				bottom = 6.dp
			)
	) {
		Text(
			text = tag,
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.body2,
			color = Color.Black
		)
	}
}
