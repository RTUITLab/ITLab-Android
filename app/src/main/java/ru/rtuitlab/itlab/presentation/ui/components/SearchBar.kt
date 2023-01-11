@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

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

@Composable
fun SearchBar(
	modifier: Modifier = Modifier,
	hint: String = stringResource(R.string.search),
	onSearch: (String) -> Unit,
	onDismissRequest: () -> Unit
) {
	var text by rememberSaveable {
		mutableStateOf("")
	}
	val focusRequester = remember { FocusRequester() }

	LaunchedEffect(text) {
		onSearch(text)
	}

	CompositionLocalProvider(
		LocalTextSelectionColors provides
				TextSelectionColors(
					handleColor = MaterialTheme.colorScheme.primary,
					backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
				)
	) {
		OutlinedTextField(
			modifier = Modifier
				.fillMaxWidth()
				.focusRequester(focusRequester)
				.then(modifier),
			value = text,
			onValueChange = { text = it },
			placeholder = {
				Text(text = hint)
			},
			leadingIcon = {
				IconButton(onClick = onDismissRequest) {
					Icon(Icons.Default.ArrowBack, null)
				}
			},
			trailingIcon = {
				IconButton(onClick = { text = "" }) {
					Icon(Icons.Default.Close, null)
				}
			},
			shape = MaterialTheme.shapes.large,
			colors = TextFieldDefaults.outlinedTextFieldColors(
				containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(128.dp),
				unfocusedBorderColor = Color.Transparent,
				disabledBorderColor = Color.Transparent,
				errorBorderColor = Color.Transparent,
				focusedBorderColor = Color.Transparent
			)
		)
	}

	DisposableEffect(Unit) {
		focusRequester.requestFocus()
		onDispose {
			onSearch("")
		}
	}
}

@Preview
@Composable
fun SearchBarPreview() {
	ITLabTheme {
		Surface(
			tonalElevation = 3.dp
		) {
			Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
				SearchBar(
					onSearch = {},
					onDismissRequest = {}
				)
			}
		}
	}
}