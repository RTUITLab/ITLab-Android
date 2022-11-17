package ru.rtuitlab.itlab.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R

@Composable
fun LoadingError(
	modifier: Modifier = Modifier,
	isScrollable: Boolean = true,
	msg: String
) {

	var mod = modifier
		.fillMaxSize()
	if (isScrollable) mod = mod.verticalScroll(rememberScrollState())

	Box(
		modifier = mod,
		contentAlignment = Alignment.Center
	) {
		Text(text = msg)
	}
}

@Composable
fun LoadingErrorRetry(
	modifier: Modifier = Modifier,
	errorMessage: String,
	onRetry: () -> Unit
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(errorMessage)
		PrimaryButton(
			onClick = onRetry,
			text = stringResource(id = R.string.retry)
		)
	}
}