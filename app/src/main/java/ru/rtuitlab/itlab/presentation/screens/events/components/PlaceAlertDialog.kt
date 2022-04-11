package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRole
import ru.rtuitlab.itlab.data.remote.api.events.models.detail.Place
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun PlaceAlertDialog(
	place: Place,
	number: Int,
	salary: Int?,
	eventViewModel: EventViewModel,
	shiftContainsUser: Boolean,
	onResult: () -> Unit,
	onDismissRequest: () -> Unit
) {

	var isLoading by remember { mutableStateOf(false) }

	val eventRoles by eventViewModel.eventRoles.collectAsState()

	val navController = LocalNavController.current

	Dialog(
		onDismissRequest = onDismissRequest,
	) {
		Card(
			shape = RoundedCornerShape(10.dp)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 20.dp,
						start = 20.dp,
						bottom = 10.dp,
						end = 20.dp
					)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
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
					imageWidth = 14.dp
				) {
					Text(
						text = if (salary != null) stringResource(
							R.string.salary,
							salary
						) else stringResource(R.string.salary_not_specified),
						style = MaterialTheme.typography.subtitle2
					)
				}
				Spacer(modifier = Modifier.height(10.dp))
				Divider()
				val entireList = place.participants + place.invited + place.wishers
				if (entireList.isNotEmpty()) {
					Spacer(modifier = Modifier.height(10.dp))

					LazyColumn(
						modifier = Modifier.fillMaxWidth(),
						verticalArrangement = Arrangement.spacedBy(5.dp)
					) {
						items(
							items = entireList,
							key = { it.user.id }
						) {
							val role = it.eventRole.toUiRole()
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								IconizedRow(
									imageVector = Icons.Default.Circle,
									imageWidth = 14.dp,
									imageHeight = 14.dp,
									opacity = 1f,
									tint = when (place.participants.contains(it)) {
										true -> Color(0xFF44B90D)
										false -> Color(0xFFE4A400)
//										else -> Color.Gray
									},
									verticalAlignment = Alignment.CenterVertically,
									spacing = 0.dp
								) {
									UserLink(user = it.user, onNavigate = onResult)
								}

								Text(
									text = if (role !is EventRole.Other) stringResource(role.nameResource) else role.name
										?: "",
									style = MaterialTheme.typography.subtitle2,
									color = AppColors.greyText.collectAsState().value,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis
								)
							}
						}
					}

					if (!shiftContainsUser) {
						Spacer(modifier = Modifier.height(10.dp))
						Divider()
					}
				}

				if (!shiftContainsUser) {
					Spacer(modifier = Modifier.height(20.dp))
					val choices = remember { eventRoles }
					var selectedSegment by remember { mutableStateOf(choices[1]) }
					SegmentedControl(
						segments = choices,
						selectedSegment = selectedSegment,
						onSegmentSelected = { selectedSegment = it }
					) { choice ->
						SegmentText(
							modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
							text = if (choice !is EventRole.Other) stringResource(choice.nameResource) else choice.name
								?: "",
							selected = selectedSegment == choice,
							selectedColor = AppColors.accent.collectAsState().value,
							unselectedColor = AppColors.greyText.collectAsState().value.copy(alpha = .8f)
						)
					}

					Spacer(modifier = Modifier.height(5.dp))
					val resources = LocalContext.current.resources
					PrimaryButton(
						modifier = Modifier
							.align(Alignment.End),
						onClick = {
							if (!isLoading) {
								isLoading = true
								eventViewModel.onPlaceApply(
									place.id,
									selectedSegment.id,
									successMessage = resources.getString(R.string.application_successful)
								) {
									isLoading = false
									onResult()
								}
							}
						},
						text = stringResource(R.string.event_apply)
					) { text ->
						LoadableButtonContent(
							isLoading = isLoading,
							strokeWidth = 2.dp
						) {
							text()
						}
					}
				}
			}
		}
	}
}