package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.events.models.ShiftWithPlacesAndSalary
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow

@Composable
fun ShiftCard(
	modifier: Modifier = Modifier,
	eventSalary: EventSalaryEntity?,
	shiftWithPlacesAndSalary: ShiftWithPlacesAndSalary
) {

	val shift = shiftWithPlacesAndSalary.shift
	val salary = shiftWithPlacesAndSalary.salary?.count ?: eventSalary?.count

	Card(
		modifier = Modifier
			.clip(MaterialTheme.shapes.medium)
			.then(modifier)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {

			Box(
				modifier = Modifier.padding(
					horizontal = 16.dp,
					vertical = 10.dp
				)
			) {
				IconizedRow(
					imageVector = Icons.Default.Event,
					spacing = 8.dp
				) {
					Text(
						text = shift.getTime(LocalContext.current),
						style = MaterialTheme.typography.titleMedium,
					)
				}
			}

			Divider(color = MaterialTheme.colorScheme.onSurface.copy(.4f))

			Column(
				modifier = Modifier
					.padding(
						horizontal = 16.dp,
						vertical = 8.dp
					)
					.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				IconizedRow(
					imageVector = Icons.Default.Info,
					imageHeight = 20.dp,
					imageWidth = 20.dp,
					verticalAlignment = Alignment.Top
				) {
					Text(
						text = if (shift.description.isNullOrBlank()) stringResource(R.string.event_no_description)
						else shift.description,
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSurface.copy(.8f)
					)
				}
				Spacer(Modifier.height(5.dp))

				IconizedRow(
					imageVector = Icons.Default.Schedule,
					imageHeight = 20.dp,
					imageWidth = 20.dp
				) {
					Text(
						text = LocalContext.current.resources.getQuantityString(
							R.plurals.n_hours,
							shift.duration,
							shift.duration
						),
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSurface.copy(.8f)
					)
				}
				Spacer(Modifier.height(5.dp))

				IconizedRow(
					imageVector = Icons.Default.Payment,
					imageHeight = 20.dp,
					imageWidth = 20.dp
				) {
					Text(
						text = salary?.let {
							stringResource(
								R.string.salary_int,
								it
							)
						} ?: stringResource(R.string.salary_not_specified),
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSurface.copy(.8f)
					)
				}
			}
			Spacer(Modifier.height(5.dp))
		}
	}
}