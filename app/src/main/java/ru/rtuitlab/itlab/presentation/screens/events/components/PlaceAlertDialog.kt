package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
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
import ru.rtuitlab.itlab.data.local.events.models.PlaceWithUsersAndSalary
import ru.rtuitlab.itlab.data.local.events.models.UserParticipationType
import ru.rtuitlab.itlab.data.local.events.models.salary.EventSalaryEntity
import ru.rtuitlab.itlab.data.remote.api.events.models.EventRole
import ru.rtuitlab.itlab.data.remote.api.events.models.EventShiftSalary
import ru.rtuitlab.itlab.presentation.screens.events.EventViewModel
import ru.rtuitlab.itlab.presentation.ui.components.*

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun PlaceAlertDialog(
	placeWithUsersAndSalary: PlaceWithUsersAndSalary,
	eventSalary: EventSalaryEntity?,
	shiftSalary: EventShiftSalary?,
	number: Int,
	eventViewModel: EventViewModel,
	shiftContainsUser: Boolean,
	onResult: () -> Unit,
	onDismissRequest: () -> Unit
) {

	var isLoading by remember { mutableStateOf(false) }

	val eventRoles by eventViewModel.roles.collectAsState()

	val place = placeWithUsersAndSalary.place
	val salary = placeWithUsersAndSalary.salary?.count ?: shiftSalary?.count ?: eventSalary?.count
	val users = placeWithUsersAndSalary.usersWithRoles

	Dialog(
		onDismissRequest = onDismissRequest,
	) {
		Card(
			shape = MaterialTheme.shapes.large
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
						style = MaterialTheme.typography.titleMedium
					)
					IconizedRow(
						imageVector = Icons.Default.People,
						imagePosition = ImagePosition.RIGHT,
						imageWidth = 14.dp,
						imageHeight = 14.dp
					) {
						Text(
							text = "${users.size}/${place.targetParticipantsCount}",
							style = MaterialTheme.typography.labelMedium
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
							style = MaterialTheme.typography.labelMedium
						)
					}

				IconizedRow(
					imageVector = Icons.Default.Payment,
					imageHeight = 14.dp,
					imageWidth = 14.dp
				) {
					Text(
						text = salary?.let {
							stringResource(
								R.string.salary_int,
								it
							)
						} ?: stringResource(R.string.salary_not_specified),
						style = MaterialTheme.typography.labelMedium
					)
				}
				Spacer(modifier = Modifier.height(10.dp))
				Divider()
				if (users.isNotEmpty()) {
					Spacer(modifier = Modifier.height(10.dp))

					LazyColumn(
						modifier = Modifier.fillMaxWidth(),
						verticalArrangement = Arrangement.spacedBy(5.dp)
					) {
						items(
							items = users,
							key = { it.userRole.userId }
						) {
							val role = it.role.toUiRole()
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
									tint = when (it.userRole.participationType) {
										UserParticipationType.PARTICIPANT -> Color(0xFF44B90D)
										UserParticipationType.INVITED -> Color(0xFFE4A400)
										else -> Color.Gray
									},
									verticalAlignment = Alignment.CenterVertically,
									spacing = 0.dp
								) {
									UserLink(user = it.user, onNavigate = onResult)
								}

								Text(
									text = if (role !is EventRole.Other) stringResource(role.nameResource) else role.name
										?: "",
									style = MaterialTheme.typography.labelMedium,
									color = LocalContentColor.current.copy(.8f),
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

				if (!shiftContainsUser && eventRoles.isNotEmpty()) {
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
							selected = selectedSegment == choice
						)
					}

					Spacer(modifier = Modifier.height(5.dp))
					val resources = LocalContext.current.resources
					PrimaryTextButton(
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