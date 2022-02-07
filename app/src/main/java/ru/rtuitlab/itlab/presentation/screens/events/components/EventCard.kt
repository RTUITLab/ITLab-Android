package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.ImagePosition
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
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
		shape = RoundedCornerShape(5.dp)
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
					fontWeight = FontWeight(500),
					fontSize = 17.sp,
					lineHeight = 22.sp
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