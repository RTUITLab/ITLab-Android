package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R

@Composable
fun SearchBar(
	modifier: Modifier = Modifier,
	hint: String = stringResource(R.string.search),
	onSearch: (String) -> Unit = {}
) {
	var text by rememberSaveable {
		mutableStateOf("")
	}
	var isHintDisplayed by rememberSaveable {
		mutableStateOf(true)
	}
	val focusRequester = remember { FocusRequester() }

	SideEffect {
		onSearch(text)
	}

	Box(
		modifier = modifier,
		contentAlignment = Alignment.CenterStart
	) {
		CompositionLocalProvider(
			LocalTextSelectionColors provides
					TextSelectionColors(
						handleColor = MaterialTheme.colors.secondary,
						backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.4f)
					)
		) {
			BasicTextField(
				value = text,
				onValueChange = {
					text = it
					isHintDisplayed = it.isEmpty()
				},
				textStyle = TextStyle(
					color = MaterialTheme.colors.onSurface,
					fontSize = 20.sp
				),
				maxLines = 1,
				singleLine = true,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(focusRequester),
				cursorBrush = SolidColor(MaterialTheme.colors.secondary)
			)
		}
		if(isHintDisplayed) {
			Text(
				text = hint,
				color = Color.LightGray,
				fontSize = 20.sp
			)
		}
	}

	DisposableEffect(Unit) {
		focusRequester.requestFocus()
		onDispose {
			onSearch("")
		}
	}

}