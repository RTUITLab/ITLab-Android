package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@Composable
fun ShiftCard(
	modifier: Modifier = Modifier,
	shift: Shift,
	salary: Int?
) {
	Card(
		modifier = modifier,
		elevation = 2.dp,
		shape = RoundedCornerShape(5.dp)
	) {
		Column(
			modifier = Modifier
				.padding(15.dp)
		) {
			Text(
				text = shift.getTime(LocalContext.current),
				style = MaterialTheme.typography.h6,
			)
			Spacer(Modifier.height(10.dp))

			IconizedRow(
				imageVector = Icons.Default.Info,
				imageHeight = 14.dp,
				imageWidth = 14.dp,
				verticalAlignment = Alignment.Top
			) {
				Text(
					text =  if (shift.description.isBlank()) stringResource(R.string.event_no_description)
							else shift.description,
					style = MaterialTheme.typography.subtitle1,
					color = AppColors.greyText.collectAsState().value
				)
			}
			Spacer(Modifier.height(5.dp))

			IconizedRow(
				imageVector = Icons.Default.Schedule,
				imageHeight = 14.dp,
				imageWidth = 14.dp
			) {
				Text(
					text = LocalContext.current.resources.getQuantityString(
						R.plurals.n_hours,
						shift.duration,
						shift.duration
					),
					style = MaterialTheme.typography.subtitle1,
					color = AppColors.greyText.collectAsState().value
				)
			}
			Spacer(Modifier.height(5.dp))

			IconizedRow(
				imageVector = Icons.Default.Payment,
				imageHeight = 14.dp,
				imageWidth = 14.dp
			) {
				Text(
					text = if (salary != null) stringResource(R.string.salary, salary) else stringResource(R.string.salary_not_specified),
					style = MaterialTheme.typography.subtitle1,
					color = AppColors.greyText.collectAsState().value
				)
			}
			Spacer(Modifier.height(5.dp))


		}
	}
}