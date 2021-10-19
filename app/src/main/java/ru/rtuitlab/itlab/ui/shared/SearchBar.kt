package ru.rtuitlab.itlab.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
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

	Box(
		modifier = modifier,
		contentAlignment = Alignment.CenterStart
	) {
		BasicTextField(
			value = text,
			onValueChange = {
				text = it
				onSearch(it)
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
				.padding(horizontal = 20.dp, vertical = 12.dp)
				.focusRequester(focusRequester)
		)
		if(isHintDisplayed) {
			Text(
				text = hint,
				color = Color.LightGray,
				fontSize = 20.sp,
				modifier = Modifier
					.padding(horizontal = 20.dp, vertical = 12.dp)
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