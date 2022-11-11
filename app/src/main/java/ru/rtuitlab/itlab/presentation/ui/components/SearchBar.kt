package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
						handleColor = MaterialTheme.colorScheme.primary,
						backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
					)
		) {
			BasicTextField(
				value = text,
				onValueChange = {
					text = it
					isHintDisplayed = it.isEmpty()
					onSearch(it)
				},
				textStyle = TextStyle(
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = MaterialTheme.typography.headlineMedium.fontSize
				),
				maxLines = 1,
				singleLine = true,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(focusRequester),
				cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
			)
		}
		if(isHintDisplayed) {
			Text(
				text = hint,
				color = LocalContentColor.current.copy(.5f),
				fontSize = MaterialTheme.typography.headlineMedium.fontSize
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