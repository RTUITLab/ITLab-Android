@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.util.Pair
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.toClientDate
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.components.UserEventCard
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadingErrorRetry
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun UserEvents(
	userViewModel: UserViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val scope = rememberCoroutineScope()
	IconizedRow(
		modifier = Modifier
			.clickable {
				userViewModel.ensureEventsUpdated()
				bottomSheetViewModel.show(
					sheet = AppBottomSheet.ProfileEvents(userViewModel),
					scope = scope
				)
			}
			.padding(horizontal = 20.dp)
			.height(36.dp),
		imageVector = Icons.Default.EventNote,
		tint = MaterialTheme.colorScheme.primary,
		opacity = 1f
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.user_events_participation),
				style = MaterialTheme.typography.bodyLarge
			)
			Icon(
				imageVector = Icons.Default.NavigateNext,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		}
	}
}

@Composable
fun UserEvents(
	userViewModel: UserViewModel
) {

	var isExpanded by rememberSaveable { mutableStateOf(false) }
	val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

	val beginEventsDate by userViewModel.beginEventsDate.collectAsState()
	val endEventsDate by userViewModel.endEventsDate.collectAsState()

	val events by userViewModel.events.collectAsState()
	val areEventsRefreshing by userViewModel.areEventsRefreshing.collectAsState()
	val errorMessage by userViewModel.eventUpdateErrorMessage.collectAsState()

	val listener = MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
		userViewModel.setEventsDates(it.first, it.second)
	}
	val context = LocalContext.current
	val navController = LocalNavController.current

	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.clickable {
					userViewModel.ensureEventsUpdated()
					isExpanded = !isExpanded
				}
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.user_events_participation),
				style = MaterialTheme.typography.titleMedium
			)
			Icon(
				modifier = Modifier.rotate(rotation),
				imageVector = Icons.Default.KeyboardArrowDown,
				contentDescription = null
			)
		}

		AnimatedVisibility(visible = isExpanded) {
			Column {
				Box(
					modifier = Modifier.padding(horizontal = 16.dp)
				) {
					OutlinedAppTextField(
						modifier = Modifier
							.fillMaxWidth()
							.clip(MaterialTheme.shapes.extraSmall)
							.clickable {
								MaterialDatePicker
									.Builder
									.dateRangePicker()
									.setSelection(
										Pair(beginEventsDate, endEventsDate)
									)
									.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
									.build()
									.apply {
										show(
											(context as AppCompatActivity).supportFragmentManager,
											null
										)
										addOnPositiveButtonClickListener(listener)
									}
							},
						value = "${beginEventsDate.toClientDate()} — ${endEventsDate.toClientDate()}",
						onValueChange = {},
						enabled = false,
						label = {
							Text(
								text = stringResource(R.string.events_select_date_range)
							)
						},
						colors = TextFieldDefaults.outlinedTextFieldColors(
							disabledTextColor = LocalContentColor.current,
							disabledBorderColor = MaterialTheme.colorScheme.outline,
							disabledLabelColor = MaterialTheme.colorScheme.onSurface,
							disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(.6f)
						),
						leadingIcon = {
							Icon(
								imageVector = Icons.Default.DateRange,
								contentDescription = null
							)
						}
					)
				}

				Spacer(modifier = Modifier.height(16.dp))

				if (events.isEmpty() && errorMessage == null && !areEventsRefreshing) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp),
						contentAlignment = Alignment.Center
					) {
						Text(stringResource(R.string.events_nothing_in_period))
					}
				} else {
					Column(
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						events.forEach {
							key(it.id) {
								UserEventCard(
									modifier = Modifier.clickable {
										navController.navigate("${AppScreen.EventDetails.navLink}/${it.id}")
									},
									event = it
								)
							}
						}
					}
				}

				errorMessage?.let {
					if (!areEventsRefreshing) {
						LoadingErrorRetry(
							modifier = Modifier.align(Alignment.CenterHorizontally),
							errorMessage = it
						) {
							userViewModel.updateEvents()
						}
					}
				}

				if (areEventsRefreshing) {
					CircularProgressIndicator(
						modifier = Modifier
							.align(Alignment.CenterHorizontally)
							.padding(16.dp)
					)
				}

				Spacer(modifier = Modifier.height(16.dp))
			}
		}
	}
}