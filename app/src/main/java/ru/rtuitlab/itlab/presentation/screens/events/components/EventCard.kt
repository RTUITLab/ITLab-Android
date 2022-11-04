package ru.rtuitlab.itlab.presentation.screens.events.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.ImagePosition
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToInstant
import ru.rtuitlab.itlab.common.extensions.toUiString
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@Composable
fun EventCard(
	event: EventModel,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	Card(
		modifier = modifier,
		elevation = 2.dp,
		shape = MaterialTheme.shapes.medium
	) {
		event.run {
			Column(
				modifier = Modifier
					.padding(
						vertical = 10.dp,
						horizontal = 16.dp
					)
					.fillMaxWidth()
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.h6
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = if (eventType.title.isBlank()) stringResource(R.string.event_no_description)
					       else eventType.title,
					style = MaterialTheme.typography.subtitle1,
					color = AppColors.greyText.collectAsState().value
				)
				Spacer(modifier = Modifier.height(16.dp))
				RoundedLinearProgressIndicator(
					modifier = Modifier
						.fillMaxWidth(),
					progress = if (targetParticipantsCount != 0)
									currentParticipantsCount.toFloat() / targetParticipantsCount.toFloat()
					           else 1f,
					color = AppColors.accent.collectAsState().value
				)
				Spacer(modifier = Modifier.height(16.dp))
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalArrangement = SpaceBetween
				) {
					IconizedRow(
						imageVector = Icons.Default.Schedule,
						imageWidth = 18.dp,
						imageHeight = 18.dp,
						opacity = .6f
					) {
						Text(
							text = beginTime.fromIso8601(context),
							style = MaterialTheme.typography.subtitle1,
							color = AppColors.greyText.collectAsState().value
						)
					}

					IconizedRow(
						imageVector = Icons.Default.People,
						imagePosition = ImagePosition.RIGHT,
						imageWidth = 20.dp,
						imageHeight = 20.dp,
						opacity = .6f
					) {
						Text(
							text = "$currentParticipantsCount/$targetParticipantsCount",
							style = MaterialTheme.typography.subtitle1
						)
					}
				}
			}
		}
	}
}

@Composable
fun UserEventCard(
	event: UserEventModel,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier,
		elevation = 2.dp,
		shape = MaterialTheme.shapes.medium
	) {
		Column(
			modifier = Modifier
				.padding(
					vertical = 10.dp,
					horizontal = 16.dp
				)
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			UserEventCardContent(event)
		}
	}
}

@Composable
fun UserEventCardContent(
	event: UserEventModel
) {
	event.run {
		val role = role.toUiRole()

		Log.v("Roles", "name ${role.name} res ${stringResource(role.nameResource)}")

		Text(
			text = title,
			style = MaterialTheme.typography.h6
		)
		IconizedRow(
			imageVector = Icons.Default.Person,
			imageHeight = 14.dp,
			imageWidth = 14.dp
		) {
			Text(
				text = role.name ?: stringResource(role.nameResource),
				style = MaterialTheme.typography.subtitle1
			)
		}
		IconizedRow(
			imageVector = Icons.Default.Schedule,
			imageHeight = 14.dp,
			imageWidth = 14.dp
		) {
			Text(
				text = beginTime.fromIso8601ToInstant().date.toUiString(),
				style = MaterialTheme.typography.subtitle1
			)
		}
	}

}