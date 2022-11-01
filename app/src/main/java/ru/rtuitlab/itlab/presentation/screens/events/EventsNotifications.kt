package ru.rtuitlab.itlab.presentation.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.components.EventNotificationCard
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun EventsNotifications(
	eventsViewModel: EventsViewModel = singletonViewModel()
) {
	val invitationDtos by eventsViewModel.invitations.collectAsState()

	val isRefreshing by eventsViewModel.areInvitationsRefreshing.collectAsState()

	val scaffoldState = rememberScaffoldState(snackbarHostState = SnackbarHostState())

	eventsViewModel.uiEvents.collectUiEvents(scaffoldState)

	Scaffold(
		scaffoldState = scaffoldState
	) {
		SwipeRefresh(
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = eventsViewModel::updateNotifications
		) {
			if (invitationDtos.isEmpty())
				LoadingError(
					msg = stringResource(R.string.events_notifications_empty)
				)
			else {
				val invitations = invitationDtos.map {
					it.toEventInvitation(LocalContext.current)
				}
				LazyColumn(
					verticalArrangement = Arrangement.spacedBy(10.dp),
					contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp)
				) {
					items(
						items = invitations,
						key = { it.eventId + it.eventRole.id }
					) {
						EventNotificationCard(
							notification = it,
							eventsViewModel = eventsViewModel
						)
					}
				}
			}
		}
	}
}