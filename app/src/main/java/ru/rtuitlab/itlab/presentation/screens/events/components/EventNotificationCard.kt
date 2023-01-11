package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.EventInvitation
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.ButtonLoadingIndicator
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
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
				style = MaterialTheme.typography.titleLarge
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
					onClick = {
						if (!isLoadingState) {
							isLoadingState = true
							eventsViewModel.acceptInvitation(
								notification = notification,
								successMessage = resources.getString(R.string.event_invitation_accepted)
							) {
								isLoadingState = false
							}
						}
					}
				) {
					if (isLoadingState) {
						ButtonLoadingIndicator(
							strokeWidth = 2.dp
						)
					} else {
						Text(
							text = stringResource(R.string.event_invitation_accept)
						)
					}
				}

				Button(
					modifier = Modifier
						.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error,
						contentColor = contentColorFor(MaterialTheme.colorScheme.error)
					),
					onClick = {
						if (!isLoadingState) {
							isLoadingState = true
							eventsViewModel.rejectInvitation(
								notification = notification,
								successMessage = resources.getString(R.string.event_invitation_rejected)
							) {
								isLoadingState = false
							}
						}
					}
				) {
					if (isLoadingState) {
						ButtonLoadingIndicator(
							strokeWidth = 2.dp
						)
					} else {
						Text(
							text = stringResource(R.string.event_invitation_reject)
						)
					}
				}
			}
		}
	}
}