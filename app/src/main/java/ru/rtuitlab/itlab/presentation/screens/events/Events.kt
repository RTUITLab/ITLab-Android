package ru.rtuitlab.itlab.presentation.screens.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.components.EventCard
import ru.rtuitlab.itlab.presentation.screens.events.components.UserEventCard
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CollapsibleScrollArea
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.heightDelta
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.EventTab
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Events(
	eventsViewModel: EventsViewModel = singletonViewModel()
) {
	val userEvents by eventsViewModel.userEvents.collectAsState()

	val isRefreshing by eventsViewModel.isRefreshing.collectAsState()
	var secondPageVisited by rememberSaveable { mutableStateOf(false) }

	val pagerState = eventsViewModel.pagerState
	val swipingState = eventsViewModel.swipingState
	val tabs = listOf(EventTab.All, EventTab.My)

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }.collect { page ->
			if (secondPageVisited) cancel()
			if (page == 1 && !secondPageVisited) {
				secondPageVisited = true
				eventsViewModel.updateUserEvents()
			}
		}

	}
	val scaffoldState = rememberScaffoldState(snackbarHostState = SnackbarHostState())

	eventsViewModel.uiEvents.collectUiEvents(scaffoldState)

	Scaffold(
		scaffoldState = scaffoldState
	) {
		HorizontalPager(
			modifier = Modifier
				.fillMaxSize(),
			verticalAlignment = Alignment.Top,
			count = tabs.size,
			state = pagerState,
			itemSpacing = 1.dp
		) { index ->
			SwipeRefresh(
				modifier = Modifier.fillMaxSize(),
				state = rememberSwipeRefreshState(isRefreshing),
				onRefresh = {
					eventsViewModel.updatePendingEvents()
					eventsViewModel.updateNotifications()
					eventsViewModel.updateUserEvents()
				}
			) {
				CollapsibleScrollArea(
					swipingState = swipingState,
					heightDelta = heightDelta
				) {
					when (tabs[index]) {
						EventTab.All -> {
							EventsList(
								eventsViewModel = eventsViewModel
							)
						}
						EventTab.My -> {
							if (userEvents.isEmpty()) {
								LoadingError(msg = stringResource(R.string.no_user_events))
							} else {
								UserEventsList(
									eventsViewModel = eventsViewModel
								)
							}
						}
					}
				}

			}
		}
	}


}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun EventsList(
	eventsViewModel: EventsViewModel
) {
	val events by eventsViewModel.pendingEvents.collectAsState()
	val pastEvents by eventsViewModel.pastEvents.collectAsState()
	val showPastEvents by eventsViewModel.showPastEvents.collectAsState()

	val navController = LocalNavController.current
	if (events.isEmpty() && (pastEvents.isEmpty() || !showPastEvents))
		LoadingError(msg = stringResource(R.string.no_pending_events))
	else
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(10.dp),
			contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
		) {
			items(
				items = events,
				key = { it.id }
			) {
				EventCard(
					modifier = Modifier.clickable {
						navController.navigate("${AppScreen.EventDetails.navLink}/${it.id}")
					},
					event = it
				)
			}
			if (showPastEvents) {
				if (events.isNotEmpty())
					item {
						Spacer(modifier = Modifier.height(16.dp))
					}
				items(
					items = pastEvents,
					key = { it.id }
				) {
					EventCard(
						modifier = Modifier.clickable {
							navController.navigate("${AppScreen.EventDetails.navLink}/${it.id}")
						},
						event = it
					)
				}
			}
		}
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun UserEventsList(
	eventsViewModel: EventsViewModel
) {
	val events by eventsViewModel.userEvents.collectAsState()
	val navController = LocalNavController.current
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
	) {
		items(
			items = events,
			key = { it.id }
		) {
			UserEventCard(
				modifier = Modifier.clickable {
					navController.navigate("${AppScreen.EventDetails.navLink}/${it.id}")
				},
				event = it
			)
		}
	}
}