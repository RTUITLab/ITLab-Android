package ru.rtuitlab.itlab.presentation.screens.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
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
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.presentation.screens.events.components.EventCard
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CollapsibleScrollArea
import ru.rtuitlab.itlab.presentation.utils.EventTab

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Events(
	eventsViewModel: EventsViewModel,
	onNavigate: (event: EventModel) -> Unit
) {
	val eventsResource by eventsViewModel.eventsListResponsesFlow.collectAsState()
	val userEventsResource by eventsViewModel.userEventsListResponsesFlow.collectAsState()

	var isRefreshing by remember { mutableStateOf(false) }
	var secondPageVisited by remember { mutableStateOf(false) }

	val pagerState = eventsViewModel.pagerState
	val swipingState = eventsViewModel.swipingState
	val tabs = listOf(EventTab.All, EventTab.My)

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }.collect { page ->
			if (page == 1 && !secondPageVisited) {
				secondPageVisited = true
				eventsViewModel.fetchUserEvents()
				cancel()
			}
		}

	}

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
			onRefresh = eventsViewModel::fetchPendingEvents
		) {
			CollapsibleScrollArea(swipingState) {
				when (tabs[index]) {
					EventTab.All -> {
						eventsResource.handle(
							onLoading = {
								isRefreshing = true
							},
							onError = { msg ->
								isRefreshing = false
								LoadingError(msg = msg)
							},
							onSuccess = {
								isRefreshing = false
								if (it.isEmpty())
									LoadingError(msg = stringResource(R.string.no_pending_events))
								else
									EventsList(
										events = it,
										listState = eventsViewModel.listState,
										onNavigate = onNavigate
									)
							}
						)
					}
					EventTab.My -> {
						userEventsResource.handle(
							onLoading = {
								isRefreshing = true
							},
							onError = { msg ->
								isRefreshing = false
								LoadingError(msg = msg)
							},
							onSuccess = {
								isRefreshing = false
								if (it.isEmpty())
									LoadingError(msg = stringResource(R.string.no_user_events))
								else
									EventsList(
										events = it,
										listState = eventsViewModel.listState,
										onNavigate = onNavigate
									)
							}
						)
					}
				}
			}

		}
	}


}

@Composable
fun EventsList(
	events: List<EventModel>,
	listState: LazyListState,
	onNavigate: (event: EventModel) -> Unit
) {
	LazyColumn(
		state = listState,
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
	) {
		items(
			items = events,
			key = { it.id }
		) {
			EventCard(
				modifier = Modifier.clickable {
					onNavigate(it)
				},
				event = it
			)
		}
	}
}