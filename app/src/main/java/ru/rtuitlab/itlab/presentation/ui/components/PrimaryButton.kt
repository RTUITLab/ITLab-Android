package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

@Composable
fun PrimaryButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	text: String,
	enabled: Boolean = true,
	textWrapper: (@Composable RowScope.(text :@Composable () -> Unit) -> Unit)? = null
) {
	Button(
		modifier = modifier,
		onClick = onClick,
		enabled = enabled
	) {
		if (textWrapper != null)
			textWrapper {
				Text(text)
			}
		else
			Text(text)
	}
}

@Composable
fun PrimaryTextButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	text: String,
	enabled: Boolean = true,
	textWrapper: (@Composable RowScope.(text :@Composable () -> Unit) -> Unit)? = null
) {
	TextButton(
		modifier = modifier,
		enabled = enabled,
		onClick = onClick
	) {
		if (textWrapper != null)
			textWrapper {
				Text(text)
			}
		else
			Text(text)
	}
}


@Preview
@Composable
fun ButtonsPreview() {
	ITLabTheme {
		Surface {
			Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
				Text("BUTTONS", style = MaterialTheme.typography.labelMedium)

				PrimaryButton(onClick = {}, text = "Button")

				PrimaryTextButton(onClick = {}, text = "Text button")

				PrimaryButton(
					text = "Loading Button",
					onClick = {}
				) { text ->
					LoadableButtonContent(
						isLoading = true,
						strokeWidth = 2.dp
					) {
						text()
					}
				}

				PrimaryTextButton(
					text = "Loading Button",
					onClick = {}
				) { text ->
					LoadableButtonContent(
						isLoading = true,
						strokeWidth = 2.dp
					) {
						text()
					}
				}
			}
		}
	}
}