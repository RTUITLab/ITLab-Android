@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.ui.components.text_fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import ru.rtuitlab.itlab.presentation.utils.text_toolbar.AppTextToolbar

@Composable
fun OutlinedAppTextField(
	value: TextFieldValue,
	onValueChange: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = LocalTextStyle.current,
	label: @Composable (() -> Unit)? = null,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	supportingText: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	singleLine: Boolean = false,
	maxLines: Int = Int.MAX_VALUE,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	shape: Shape = TextFieldDefaults.outlinedShape,
	colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
		selectionColors = TextSelectionColors(
			handleColor = MaterialTheme.colorScheme.primary,
			backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
		)
	),
	toolbar: AppTextToolbar? = null
) {
	val textFieldModifier = if (onClick != null)
		Modifier.clip(shape).clickable(onClick = onClick).then(modifier)
	else modifier
	CompositionLocalProvider(
		LocalTextToolbar provides (toolbar ?: LocalTextToolbar.current)
	) {
		OutlinedTextField(value, onValueChange, textFieldModifier, enabled, readOnly, textStyle, label, placeholder, leadingIcon, trailingIcon, supportingText, isError, visualTransformation, keyboardOptions, keyboardActions, singleLine, maxLines, interactionSource, shape, colors)
	}
}

@Composable
fun OutlinedAppTextField(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = LocalTextStyle.current,
	label: @Composable (() -> Unit)? = null,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	supportingText: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	visualTransformation: VisualTransformation = VisualTransformation.None,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	singleLine: Boolean = false,
	maxLines: Int = Int.MAX_VALUE,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	shape: Shape = TextFieldDefaults.outlinedShape,
	colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
		selectionColors = TextSelectionColors(
			handleColor = MaterialTheme.colorScheme.primary,
			backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
		)
	),
	toolbar: AppTextToolbar? = null
) {
	val textFieldModifier = if (onClick != null)
		Modifier.clip(shape).clickable(onClick = onClick).then(modifier)
	else modifier
	CompositionLocalProvider(
		LocalTextToolbar provides (toolbar ?: LocalTextToolbar.current)
	) {
		OutlinedTextField(value, onValueChange, textFieldModifier, enabled, readOnly, textStyle, label, placeholder, leadingIcon, trailingIcon, supportingText, isError, visualTransformation, keyboardOptions, keyboardActions, singleLine, maxLines, interactionSource, shape, colors)
	}
}