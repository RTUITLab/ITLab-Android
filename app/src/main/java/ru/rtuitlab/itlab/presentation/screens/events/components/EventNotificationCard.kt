package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.EventInvitation
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.ButtonLoadingIndicator
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun EventNotificationCard(
	notification: EventInvitation,
	eventsViewModel: EventsViewModel
) {
	var isLoadingState by rememberSaveable { mutableStateOf(false) }
	val navController = LocalNavController.current
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clip(MaterialTheme.shapes.medium)
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 15.dp, vertical = 10.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = stringResource(R.string.event_invitation),
				style = MaterialTheme.typography.h6
			)
			InteractiveField(value = notification.title) {
				navController.navigate("${AppScreen.EventDetails.navLink}/${notification.eventId}")
			}

			Row {
				Text(
					text = stringResource(R.string.event_beginning),
					modifier = Modifier.weight(weight = 5f, fill = true)
				)
				Text(
					text = notification.beginTime,
					modifier = Modifier.weight(weight = 7f, fill = true)
				)
			}
			Row {
				Text(
					text = stringResource(R.string.event_duration),
					modifier = Modifier.weight(weight = 5f, fill = true)
				)
				Text(
					text = notification.duration,
					modifier = Modifier.weight(weight = 7f, fill = true)
				)
			}
			Row {
				Text(
					text = stringResource(R.string.event_role),
					modifier = Modifier.weight(weight = 5f, fill = true)
				)
				Text(
					text = notification.eventRole.name ?: stringResource(notification.eventRole.nameResource),
					modifier = Modifier.weight(weight = 7f, fill = true)
				)
			}
			Row {
				Text(
					text = stringResource(R.string.event_place),
					modifier = Modifier.weight(weight = 5f, fill = true)
				)
				Text(
					text = notification.placeDescription,
					modifier = Modifier.weight(weight = 7f, fill = true)
				)
			}

			val resources = LocalContext.current.resources

			Column {
				Button(
					modifier = Modifier
						.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						backgroundColor = AppColors.accent.collectAsState().value
					),
					onClick = {
						if (!isLoadingState) {
							isLoadingState = true
							eventsViewModel.acceptInvitation(
								placeId = notification.placeId,
								successMessage = resources.getString(R.string.event_invitation_accepted)
							) {
								isLoadingState = false
							}
						}
					}
				) {
					if (isLoadingState) {
						ButtonLoadingIndicator(
							color = Color.White,
							strokeWidth = 2.dp
						)
					} else {
						Text(
							text = stringResource(R.string.event_invitation_accept),
							color = Color.White,
							fontSize = 14.sp,
							fontWeight = FontWeight(500)
						)
					}
				}

				Button(
					modifier = Modifier
						.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						backgroundColor = AppColors.red
					),
					onClick = {
						if (!isLoadingState) {
							isLoadingState = true
							eventsViewModel.rejectInvitation(
								placeId = notification.placeId,
								successMessage = resources.getString(R.string.event_invitation_rejected)
							) {
								isLoadingState = false
							}
						}
					}
				) {
					if (isLoadingState) {
						ButtonLoadingIndicator(
							color = Color.White,
							strokeWidth = 2.dp
						)
					} else {
						Text(
							text = stringResource(R.string.event_invitation_reject),
							color = Color.White,
							fontSize = 14.sp,
							fontWeight = FontWeight(500)
						)
					}
				}
			}
		}
	}
}