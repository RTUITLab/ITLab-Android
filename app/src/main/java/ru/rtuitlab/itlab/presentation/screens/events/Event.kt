@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.events

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.events.models.EventWithShiftsAndSalary
import ru.rtuitlab.itlab.presentation.screens.events.components.ShiftCard
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CollapsibleScrollArea
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.SwipingStates
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Event(
	eventViewModel: EventViewModel,
	bottomSheetViewModel: BottomSheetViewModel = singletonViewModel(),
	appBarViewModel: AppBarViewModel = singletonViewModel()
) {
	val event by eventViewModel.event.collectAsState()

	val isRefreshing by eventViewModel.isRefreshing.collectAsState()

	val snackbarHostState = remember { SnackbarHostState() }
	eventViewModel.uiEvents.collectUiEvents(snackbarHostState)

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) {
		SwipeRefresh(
			modifier = Modifier
				.fillMaxSize()
				.padding(it),
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = {
				eventViewModel.updateDetails()
			}
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {
				event?.let {
					LaunchedEffect(null) {
						if (appBarViewModel.currentScreen.value is AppScreen.EventDetails)
							appBarViewModel.onNavigate(
								AppScreen.EventDetails(it.eventDetail.title)
							)
					}
					EventInfoWithList(
						event = it,
						eventViewModel = eventViewModel,
						bottomSheetViewModel = bottomSheetViewModel
					)
					event
				} ?: LoadingError(msg = stringResource(R.string.local_copy_inaccessible))
			}
		}
	}
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
@Composable
private fun EventInfoWithList(
	event: EventWithShiftsAndSalary,
	eventViewModel: EventViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val swipingState = rememberSwipeableState(SwipingStates.EXPANDED)

	MotionLayout(
		start = startConstraintSet(),
		end = endConstraintSet(),
		progress = if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction
	) {
		var height by remember { mutableStateOf(200) }

		val density = LocalDensity.current
		EventInfo(
			event = event,
			modifier = Modifier
				.layoutId("info")
				.onGloballyPositioned {
					height = with(density) { it.size.height.toDp().value.toInt() }
				},
			bottomSheetViewModel = bottomSheetViewModel
		)
		CollapsibleScrollArea(
			swipingState = swipingState,
			heightDelta = height,
			modifier = Modifier
				.layoutId("list")
		) {
			EventShifts(
				event = event,
				eventViewModel = eventViewModel,
				bottomSheetViewModel = bottomSheetViewModel
			)
		}
	}
}

@Composable
private fun startConstraintSet() = ConstraintSet {
	val info = createRefFor("info")
	val list = createRefFor("list")
	constrain(info) {
		start.linkTo(parent.start)
		top.linkTo(parent.top)
		end.linkTo(parent.end)
		pivotY = 1f
	}

	constrain(list) {
		top.linkTo(info.bottom)
	}
}

@SuppressLint("Range")
@Composable
private fun endConstraintSet() = ConstraintSet {
	val info = createRefFor("info")
	val list = createRefFor("list")
	constrain(info) {
		start.linkTo(parent.start)
		top.linkTo(parent.top)
		end.linkTo(parent.end)
		pivotY = -.4f
		scaleX = .7f
		scaleY = .7f
		alpha = 0f
	}

	constrain(list) {
		top.linkTo(parent.top)
		bottom.linkTo(parent.bottom)
	}
}

@ExperimentalMaterialApi
@Composable
private fun EventInfo(
	event: EventWithShiftsAndSalary,
	modifier: Modifier = Modifier,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	Surface(
		modifier = modifier
			.fillMaxWidth()
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconizedRow(
				imageVector = Icons.Default.Schedule,
				opacity = .7f,
				spacing = 10.dp
			) {
				Text(
					text = "${
						event.beginTime.fromIso8601(
							context,
							","
						)
					} — ${event.endTime.fromIso8601(context, ",")}"
				)
			}

			IconizedRow(
				imageVector = Icons.Default.Payment,
				opacity = .7f,
				spacing = 10.dp
			) {
				Text(
					text = if (event.salary != null) stringResource(
						R.string.salary_int,
						event.salary.count
					) else stringResource(R.string.salary_not_specified)
				)
			}

			IconizedRow(
				imageVector = Icons.Default.LocationOn,
				opacity = .7f,
				spacing = 10.dp
			) {
				Text(
					text = event.eventDetail.address
				)
			}

			IconizedRow(
				imageVector = Icons.Default.People,
				opacity = .7f,
				spacing = 10.dp
			) {
				Text(
					text = "${event.eventInfo.event.currentParticipantsCount}/${event.eventInfo.event.targetParticipantsCount}"
				)
			}

			IconizedRow(
				imageVector = Icons.Default.Info,
				opacity = .7f,
				spacing = 10.dp
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = event.eventInfo.type.title
					)

					InteractiveField(
						value = stringResource(R.string.more),
						hasArrow = true
					) {
						bottomSheetViewModel.show(
							sheet = AppBottomSheet.EventDescription(event.eventDetail.description),
							scope = coroutineScope
						)
					}
				}
			}


		}
	}
}

@ExperimentalMaterialApi
@Composable
private fun EventShifts(
	event: EventWithShiftsAndSalary,
	eventViewModel: EventViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val coroutineScope = rememberCoroutineScope()

	val shifts = event.shifts

	Column(
		modifier = Modifier
			.fillMaxSize()
	) {
		LazyColumn(
			modifier = Modifier
				.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(10.dp),
			contentPadding = PaddingValues(bottom = 15.dp, start = 20.dp, end = 20.dp)
		) {
			item {
				Spacer(modifier = Modifier.height(15.dp))
				Text(
					text = stringResource(R.string.shifts),
					style = MaterialTheme.typography.titleLarge
				)
			}
			items(
				items = shifts.sortedBy { it.shift.beginTime },
				key = { it.shift.id }
			) { shift ->
				ShiftCard(
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							bottomSheetViewModel.show(
								AppBottomSheet.EventShift(
									shiftAndSalary = shift,
									eventSalary = event.salary,
									eventViewModel = eventViewModel
								),
								coroutineScope
							)
						},
					eventSalary = event.salary,
					shiftWithPlacesAndSalary = shift
				)
			}
		}
	}
}
