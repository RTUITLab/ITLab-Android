package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Place
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Shift
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.ImagePosition
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ShiftBottomSheet(
	shift: Shift,
	salaries: List<Int>,
	eventsViewModel: EventsViewModel = viewModel()
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Start
	) {
		IconizedRow(
			imageVector = Icons.Default.DateRange,
			spacing = 10.dp
		) {
			Text(shift.getTime(LocalContext.current))
		}
	}
	Spacer(Modifier.height(10.dp))
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(top = 10.dp, bottom = 15.dp)
	) {
		itemsIndexed(
			items = shift.places,
			key = { _, item -> item.id }
		) { index, item ->
			ShiftPlaceCard(
				number = index + 1,
				place = item,
				salary = salaries[index].takeUnless { it == -1 }
			)
		}
	}
}

@Composable
private fun ShiftPlaceCard(
	number: Int,
	place: Place,
	salary: Int?
) {
	Card(
		modifier = Modifier
			.fillMaxWidth(),
		shape = RoundedCornerShape(5.dp),
		elevation = 8.dp
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
		) {
			VerticalLinearProgressIndicator(
				modifier = Modifier.matchParentSize(),
				progress = if (place.targetParticipantsCount != 0)
					place.participants.size.toFloat() / place.targetParticipantsCount.toFloat()
				else 1f,
				color = AppColors.accent,

			)
			Surface(
				modifier = Modifier
					.padding(
						end = 8.dp
					)
					.fillMaxWidth()
			) {
				Column(
					modifier = Modifier.padding(15.dp)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
					) {
						Text(
							text = stringResource(R.string.place_number_n, number),
							style = MaterialTheme.typography.h6
						)
						IconizedRow(
							imageVector = Icons.Default.People,
							imagePosition = ImagePosition.RIGHT,
							imageWidth = 14.dp,
							imageHeight = 14.dp
						) {
							Text(
								text = "${place.participants.size}/${place.targetParticipantsCount}"
							)
						}
					}

					Spacer(modifier = Modifier.height(5.dp))

					if (place.description.isNotBlank())
						IconizedRow(
							imageVector = Icons.Default.Info,
							imageHeight = 14.dp,
							imageWidth = 14.dp
						) {
							Text(
								text = place.description,
								style = MaterialTheme.typography.subtitle2
							)
						}

					IconizedRow(
						imageVector = Icons.Default.Payment,
						imageHeight = 14.dp,
						imageWidth = 14.dp
					) {
						Text(
							text = if (salary != null) stringResource(R.string.salary, salary) else stringResource(R.string.salary_not_specified),
							style = MaterialTheme.typography.subtitle2
						)
					}
				}
			}
		}
	}
}