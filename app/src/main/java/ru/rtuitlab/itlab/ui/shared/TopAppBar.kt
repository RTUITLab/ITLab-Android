package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BasicTopAppBar(
	text: String,
	onBackAction: () -> Unit = emptyBackAction,
	options: List<AppBarOption> = emptyList()
) {
	TopAppBar(
		elevation = 10.dp
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(54.dp)
				.padding(
					start = if (onBackAction == emptyBackAction) 15.dp else 0.dp,
					end = 15.dp
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Row(
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.CenterVertically
			) {
				if (onBackAction != emptyBackAction) {
					IconButton(onClick = onBackAction) {
						Icon(Icons.Default.ArrowBack, contentDescription = null)
					}
				}
				
				Text(
					text = text,
					fontSize = 20.sp,
					fontWeight = FontWeight(500),
					textAlign = TextAlign.Start,
				)
			}

			Row(
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically
			) {
				options.forEach { option ->
					IconButton(onClick = option.onClick) {
						Icon(imageVector = option.icon, contentDescription = option.contentDescription)
					}
				}
			}
		}
	}
}

@Composable
fun ExtendedTopAppBar(content: @Composable () -> Unit
) {
	TopAppBar(
		elevation = 10.dp
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(54.dp)
				.padding(
					start = 15.dp,
					end = 15.dp
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			content()
		}
	}
}

data class AppBarOption(
	val icon: ImageVector,
	val contentDescription: String? = null,
	val onClick: () -> Unit
)

private val emptyBackAction: () -> Unit = {}