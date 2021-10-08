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
				.height(56.dp)
				.padding(
					start = if (onBackAction == emptyBackAction) 16.dp else 0.dp,
					end = 16.dp
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
					Spacer(modifier = Modifier.width(24.dp))
				}
				
				Text(
					text = text,
					fontSize = 20.sp,
					fontWeight = FontWeight(500),
					textAlign = TextAlign.Start,
				)
			}

			OptionsRow(options)
		}
	}
}

@Composable
fun ExtendedTopAppBar(
	options: List<AppBarOption> = emptyList(),
	onBackAction: () -> Unit = emptyBackAction,
	hideBackButton: Boolean = false,
	hideOptions: Boolean = false,
	content: @Composable () -> Unit
) {
	TopAppBar(
		elevation = 10.dp
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.padding(
					start = if (hideBackButton) 16.dp else 0.dp,
					end = 16.dp
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			if (onBackAction != emptyBackAction && !hideBackButton) {
				IconButton(onClick = onBackAction) {
					Icon(Icons.Default.ArrowBack, contentDescription = null)
				}
				Spacer(modifier = Modifier.width(24.dp))
			}
			content()
			if (!hideOptions) OptionsRow(options)
		}
	}
}

@Composable
fun OptionsRow(
	options: List<AppBarOption>
) {
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

data class AppBarOption(
	val icon: ImageVector,
	val contentDescription: String? = null,
	val onClick: () -> Unit
)

private val emptyBackAction: () -> Unit = {}