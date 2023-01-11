package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.events.models.PlaceWithUsersAndSalary
import ru.rtuitlab.itlab.data.local.events.models.ShiftWithPlacesAndSalary
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventSalary
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel
import ru.rtuitlab.itlab.presentation.screens.profile.ProfileViewModel
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.ImagePosition
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ShiftBottomSheet(
	shiftAndSalary: ShiftWithPlacesAndSalary,
	eventSalary: EventSalaryEntity?,
	eventViewModel: EventViewModel,
	profileViewModel: ProfileViewModel = viewModel(),
	bottomSheetViewModel: BottomSheetViewModel = singletonViewModel()
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Start
	) {
		IconizedRow(
			imageVector = Icons.Default.DateRange,
			spacing = 10.dp
		) {
			Text(shiftAndSalary.shift.getTime(LocalContext.current))
		}
	}
	Spacer(Modifier.height(10.dp))
	LazyColumn(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(top = 10.dp, bottom = 15.dp)
	) {
		itemsIndexed(
			items = shiftAndSalary.places,
			key = { _, item -> item.place.id }
		) { index, item ->
			ShiftPlaceCard(
				number = index + 1,
				eventSalary = eventSalary,
				shiftSalary = shiftAndSalary.salary,
				placeWithUsersAndSalary = item,
				eventViewModel = eventViewModel,
				bottomSheetViewModel = bottomSheetViewModel,
				shiftContainsUser = shiftAndSalary.places
					.any {
						it.usersWithRoles
							.any { it.userRole.userId == profileViewModel.userId }
					}
			)
		}
	}
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun ShiftPlaceCard(
	number: Int,
	eventSalary: EventSalaryEntity?,
	shiftSalary: EventShiftSalary?,
	placeWithUsersAndSalary: PlaceWithUsersAndSalary,
	eventViewModel: EventViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	shiftContainsUser: Boolean
) {
	var dialogIsShown by remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()

	val place = placeWithUsersAndSalary.place
	val salary = placeWithUsersAndSalary.salary?.count ?: shiftSalary?.count ?: eventSalary?.count
	val users = placeWithUsersAndSalary.usersWithRoles

	if (dialogIsShown)
		PlaceAlertDialog(
			number = number,
			eventSalary = eventSalary,
			shiftSalary = shiftSalary,
			placeWithUsersAndSalary = placeWithUsersAndSalary,
			eventViewModel = eventViewModel,
			onResult = {
				dialogIsShown = false
				bottomSheetViewModel.hide(scope)
			},
			shiftContainsUser = shiftContainsUser
		) {
			dialogIsShown = false
		}

	Card(
		modifier = Modifier
			.clickable {
				dialogIsShown = true
			}
			.fillMaxWidth(),
		shape = MaterialTheme.shapes.medium,
		elevation = 8.dp
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
		) {
			VerticalLinearProgressIndicator(
				modifier = Modifier.matchParentSize(),
				progress = if (place.targetParticipantsCount != 0)
					users.size.toFloat() / place.targetParticipantsCount.toFloat()
				else 1f,
				color = AppColors.accent.collectAsState().value
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
								text = "${users.size}/${place.targetParticipantsCount}"
							)
						}
					}

					Spacer(modifier = Modifier.height(5.dp))

					if (!place.description.isNullOrBlank())
						IconizedRow(
							imageVector = Icons.Default.Info,
							imageHeight = 14.dp,
							imageWidth = 14.dp,
							verticalAlignment = Alignment.Top
						) {
							Text(
								text = place.description,
								style = MaterialTheme.typography.subtitle2
							)
						}

					IconizedRow(
						imageVector = Icons.Default.Payment,
						imageHeight = 14.dp,
						imageWidth = 14.dp,
						verticalAlignment = Alignment.Top
					) {
						Text(
							text = salary?.let {
								stringResource(
									R.string.salary_int,
									it
								)
							} ?: stringResource(R.string.salary_not_specified),
							style = MaterialTheme.typography.subtitle2
						)
					}
				}
			}
		}
	}
}