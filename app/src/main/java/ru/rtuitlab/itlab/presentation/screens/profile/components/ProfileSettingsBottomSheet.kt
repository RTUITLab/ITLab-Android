package ru.rtuitlab.itlab.presentation.screens.profile.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyType
import ru.rtuitlab.itlab.presentation.screens.auth.AuthViewModel
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileViewModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadableButtonContent
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryButton
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@Composable
fun ProfileSettingsBottomSheet(
	profileViewModel: ProfileViewModel = viewModel(),
	authViewModel: AuthViewModel = viewModel()
) {

	val credentials by profileViewModel.userCredentialsFlow.collectAsState()
	val properties by profileViewModel.properties.collectAsState()

	var selectedFieldText: String? by remember{ mutableStateOf("")}
	var selectedProperty: UserPropertyType? by remember { mutableStateOf(null) }
	var fieldEditedNow: EditableField? by remember {
		mutableStateOf(null)
	}

	credentials.handle(
		onSuccess = { userResponse ->

			var isLoading by remember { mutableStateOf(false) }
			var open by remember{ mutableStateOf(false)}
			userResponse.toUser().run {
				Column(
					modifier = Modifier
						.padding(bottom = 15.dp)
				) {
					ProfileSettingsItem(
						painter = painterResource(R.drawable.ic_last_name),
						contentDescription = stringResource(R.string.last_name),
						text = lastName
					) {
						selectedFieldText = lastName
						fieldEditedNow = EditableField.LAST_NAME
						open = true
					}

					ProfileSettingsItem(
						painter = painterResource(R.drawable.ic_first_name),
						contentDescription = stringResource(R.string.first_name),
						text = firstName
					) {
						selectedFieldText = firstName
						fieldEditedNow = EditableField.FIRST_NAME
						open = true
					}

					ProfileSettingsItem(
						painter = painterResource(R.drawable.ic_middle_name),
						contentDescription = stringResource(R.string.middle_name),
						text = middleName
					) {
						selectedFieldText = middleName
						fieldEditedNow = EditableField.MIDDLE_NAME
						open = true
					}

					ProfileSettingsItem(
						icon = Icons.Default.Mail,
						contentDescription = stringResource(R.string.email),
						text = email
					) {}

					ProfileSettingsItem(
						icon = Icons.Default.Phone,
						contentDescription = stringResource(R.string.phone_number),
						text = phoneNumber
					) {
						selectedFieldText = phoneNumber
						fieldEditedNow = EditableField.PHONE_NUMBER
						open = true
					}

					properties.handle(
						onSuccess = { list ->
							list.map { it.toUiPropertyType() }.forEach { prop ->
								val userPropValue = userResponse.properties.find { it.userPropertyType.id == prop.id }?.value ?: ""
								ProfileSettingsItem(
									icon = prop.icon ?: ImageVector.vectorResource(prop.vectorResource!!),
									contentDescription = prop.nameResource?.let { stringResource(it) } ?: prop.name!!,
									text = userPropValue
								) {
									selectedFieldText = userPropValue
									selectedProperty = prop
									open = true
								}
							}
						}
					)

					Divider()

					ProfileSettingsItem(
						icon = Icons.Default.Logout,
						contentDescription = stringResource(R.string.logout),
						text = stringResource(R.string.logout),
						tint = AppColors.red,
						onClick = authViewModel::onLogoutEvent
					)

				}
				if (open)
					SettingsDialog(
						initialValue = selectedFieldText,
						isLoading = isLoading,
						title = selectedProperty?.name ?: stringResource(selectedProperty?.nameResource ?: fieldEditedNow!!.nameResource)
					) { newValue ->
						newValue?.let {
							if (isLoading) return@let
							selectedProperty?.let {
								isLoading = true
								profileViewModel.editUserProperty(
									id = it.id,
									value = newValue,
									credentials = userResponse
								) {
									isLoading = false
									selectedProperty = null
									open = false
								}
							} ?:
							fieldEditedNow?.let {
								profileViewModel.editUserInfo(
									info =
										when(fieldEditedNow) {
											EditableField.FIRST_NAME -> userResponse.getEditRequest().copy(firstName = newValue)
											EditableField.MIDDLE_NAME -> userResponse.getEditRequest().copy(middleName = newValue)
											EditableField.LAST_NAME -> userResponse.getEditRequest().copy(lastName = newValue)
											EditableField.PHONE_NUMBER -> userResponse.getEditRequest().copy(phoneNumber = newValue)
											else -> userResponse.getEditRequest()
										},
									userResponse = userResponse
								) {
									isLoading = false
									fieldEditedNow = null
									open = false
								}
							}
						} ?: run { fieldEditedNow = null; selectedProperty = null }
						if (!isLoading) open = false
					}
			}
		},
		onError = {
			LoadingError(msg = it)
		}
	)
}

@Composable
private fun ProfileSettingsItem(
	painter: Painter,
	contentDescription: String,
	text: String?,
	onClick: () -> Unit
) {
	IconizedRow(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(4.dp))
			.clickable {
				onClick()
			}
			.height(48.dp),
		painter = painter,
		contentDescription = contentDescription,
		spacing = 10.dp,
		opacity = 1f,
		tint = AppColors.accent.collectAsState().value,
	) {
		SettingsText(
			text = text,
			hint = contentDescription
		)
	}
}

@Composable
private fun ProfileSettingsItem(
	icon: ImageVector,
	contentDescription: String,
	text: String?,
	tint: Color? = AppColors.accent.collectAsState().value,
	onClick: () -> Unit
) {
	IconizedRow(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(4.dp))
			.clickable {
				onClick()
			}
			.height(48.dp),
		imageVector = icon,
		spacing = 10.dp,
		opacity = 1f,
		contentDescription = contentDescription,
		tint = tint
	) {
		SettingsText(
			text = text,
			hint = contentDescription
		)
	}
}

@Composable
private fun SettingsText(
	text: String?,
	hint: String
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
	) {
		if (text.isNullOrEmpty())
			Text(
				text = hint,
				style = MaterialTheme.typography.caption,
				color = AppColors.greyText.collectAsState().value.copy(alpha = .4f)
			)
		else
			Text(
				text = text,
				style = MaterialTheme.typography.caption,
			)
	}
}

@Composable
private fun SettingsDialog(
	initialValue: String?,
	isLoading: Boolean,
	title: String,
	onResult: (String?) -> Unit
) {

	var textFieldValue by remember { mutableStateOf(initialValue ?: "") }

	AlertDialog(
		title = {
			Text(text = title, style = MaterialTheme.typography.h4)
			Spacer(modifier = Modifier.height(15.dp))
		},
		text = {
			Column {
				Spacer(modifier = Modifier.height(15.dp))
				BasicTextField(
					modifier = Modifier
						.height(35.dp)
						.background(shape = RoundedCornerShape(6.dp), color = Color.Transparent)
						.border(
							width = 1.dp,
							color = MaterialTheme.colors.onSurface.copy(alpha = .12f),
							shape = RoundedCornerShape(6.dp)
						)
						.fillMaxWidth(),
					value = textFieldValue,
					textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
					singleLine = true,
					keyboardActions = KeyboardActions(
						onDone = {onResult(textFieldValue)}
					),
					onValueChange = { textFieldValue = it.filterNot { it == '\n' } }
				) { innerTextField ->
					Row(verticalAlignment = Alignment.CenterVertically) {
						Spacer(modifier = Modifier.width(8.dp))
						innerTextField()
					}
				}
			}
		},
		confirmButton = {
			PrimaryButton(
				text = stringResource(R.string.save),
				onClick = {
					onResult(textFieldValue)
				}
			) { text ->
				LoadableButtonContent(
					isLoading = isLoading,
					strokeWidth = 2.dp
				) {
					text()
				}
			}
		},
		dismissButton = {
			PrimaryButton(
				text = stringResource(R.string.cancel),
				onClick = {
					onResult(null)
				}
			) { text ->
				text()
			}
		},
		onDismissRequest = {onResult(null)}
	)
}

private enum class EditableField(@StringRes val nameResource: Int) {
	FIRST_NAME(R.string.first_name),
	MIDDLE_NAME(R.string.middle_name),
	LAST_NAME(R.string.last_name),
	PHONE_NUMBER(R.string.phone_number)
}