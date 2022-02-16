package ru.rtuitlab.itlab.presentation.screens.events

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SwipeableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.events.models.EventInvitationDto
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.data.repository.EventsRepository
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.SwipingStates
import ru.rtuitlab.itlab.presentation.ui.extensions.minus
import ru.rtuitlab.itlab.presentation.ui.extensions.nowAsIso8601
import ru.rtuitlab.itlab.presentation.ui.extensions.toMoscowDateTime
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalPagerApi
@HiltViewModel
class EventsViewModel @Inject constructor(
	private val repository: EventsRepository,
	private val authStateStorage: AuthStateStorage
) : ViewModel() {

	private val userId = runBlocking { authStateStorage.userIdFlow.first() }



	private var _beginEventsDate = MutableStateFlow(Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds())
	val beginEventsDate = _beginEventsDate.asStateFlow()
	private var _endEventsDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
	val endEventsDate = _endEventsDate.asStateFlow()


	val pagerState = PagerState()

	val swipingState = SwipeableState(SwipingStates.EXPANDED)

	val allEventsListState = LazyListState()
	val userEventsListState = LazyListState()

	private var _isDateSelectionMade = MutableStateFlow(false)
	val isDateSelectionMade = _isDateSelectionMade.asStateFlow()


	private val _eventsListResponseFlow =
		MutableStateFlow<Resource<List<EventModel>>>(Resource.Loading)
	val eventsListResponsesFlow = _eventsListResponseFlow.asStateFlow().also {
		fetchPendingEvents()
	}

	private val _userEventsListResponseFlow =
		MutableStateFlow<Resource<List<UserEventModel>>>(Resource.Empty)
	val userEventsListResponsesFlow = _userEventsListResponseFlow.asStateFlow()

	private val _pastEventsListResponseFlow =
		MutableStateFlow<Resource<List<EventModel>>>(Resource.Empty)
	val pastEventsListResponseFlow = _pastEventsListResponseFlow.asStateFlow()

	private var _invitations = MutableStateFlow<Resource<List<EventInvitationDto>>>(Resource.Loading)
	val invitations = _invitations.asStateFlow().also { fetchInvitations() }

	private var cachedEventList = emptyList<EventModel>()
	private var cachedUserEventList = emptyList<UserEventModel>()
	private var cachedPastEventList = emptyList<EventModel>()

	private var _eventsFlow = MutableStateFlow(cachedEventList)
	val eventsFlow = _eventsFlow.asStateFlow()

	private var _userEventsFlow = MutableStateFlow(cachedUserEventList)
	val userEventsFlow = _userEventsFlow.asStateFlow()

	private var _pastEventsFlow = MutableStateFlow(cachedPastEventList)
	val pastEventsFlow = _pastEventsFlow.asStateFlow()

	private var _showPastEvents = MutableStateFlow(false)
	val showPastEvents = _showPastEvents.asStateFlow()

	fun toggleShowPastEvents(show: Boolean) = viewModelScope.launch {
		_showPastEvents.value = show
	}.also {
		if (!show) return@also
		_pastEventsListResponseFlow.emitInIO(viewModelScope) {
			repository.fetchAllEvents(
				end = nowAsIso8601()
			)
		}
	}

	fun fetchAllEvents(begin: String? = null, end: String? = null) = _eventsListResponseFlow.emitInIO(viewModelScope) {
		repository.fetchAllEvents(begin, end)
	}

	fun fetchPendingEvents() = _eventsListResponseFlow.emitInIO(viewModelScope) {
		_isDateSelectionMade.value = false
		repository.fetchPendingEvents()
	}

	fun fetchUserEvents(begin: String? = null, end: String? = null) = _userEventsListResponseFlow.emitInIO(viewModelScope) {
		repository.fetchUserEvents(userId, begin, end)
	}


	val snackbarHostState = SnackbarHostState()

	fun setEventsDates(begin: Long, end: Long) {
		_showPastEvents.value = false
		_isDateSelectionMade.value = true
		_beginEventsDate.value = begin
		_endEventsDate.value = end
		fetchAllEvents(
			begin = begin.toMoscowDateTime().date.toString(),
			end = end.toMoscowDateTime().date.toString()
		)
	}

	fun fetchInvitations() = _invitations.emitInIO(viewModelScope) {
		repository.fetchInvitations()
	}

	fun rejectInvitation(
		placeId: String,
		successMessage: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		repository.rejectInvitation(placeId).handle(
			onSuccess = {
				onFinish()
				fetchInvitations()
				snackbarHostState.showSnackbar(it.errorBody()?.string() ?: successMessage)
			},
			onError = {
				onFinish()
				snackbarHostState.showSnackbar(it)
			}
		)
	}
	fun acceptInvitation(
		placeId: String,
		successMessage: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		repository.acceptInvitation(placeId).handle(
			onSuccess = {
				onFinish()
				fetchInvitations()
				snackbarHostState.showSnackbar(it.errorBody()?.string() ?: successMessage)
			},
			onError = {
				onFinish()
				snackbarHostState.showSnackbar(it)
			}
		)
	}


	private var searchQuery = ""

	fun onSearch(query: String) {
		searchQuery = query
		_eventsFlow.value = cachedEventList.filter { filterSearchResult(it, query) }
		_userEventsFlow.value = cachedUserEventList.filter { filterSearchResult(it, query) }
		if (showPastEvents.value)
			_pastEventsFlow.value = cachedPastEventList.filter { filterSearchResult(it, query) }
	}

	fun onResourceSuccess(events: List<EventModel>) {
		cachedEventList = events
		_eventsFlow.value = events
	}

	fun onUserResourceSuccess(events: List<UserEventModel>) {
		cachedUserEventList = events
		_userEventsFlow.value = events
	}

	fun onPastResourceSuccess(events: List<EventModel>) {
		cachedPastEventList = events.filterNot { cachedEventList.contains(it) }
		_pastEventsFlow.value = cachedPastEventList
	}

	private fun filterSearchResult(event: EventModel, query: String) = event.run {
		title.contains(query.trim(), ignoreCase = true) ||
		eventType.title.contains(query.trim(), ignoreCase = true)
	}
	private fun filterSearchResult(event: UserEventModel, query: String) = event.run {
		title.contains(query.trim(), ignoreCase = true) ||
		eventType.title.contains(query.trim(), ignoreCase = true)
	}

}