@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.rtuitlab.itlab.presentation.screens.events

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import ru.rtuitlab.itlab.common.extensions.endOfTimes
import ru.rtuitlab.itlab.common.extensions.minus
import ru.rtuitlab.itlab.common.extensions.nowAsIso8601
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.data.remote.api.events.models.EventInvitation
import ru.rtuitlab.itlab.domain.use_cases.events.*
import ru.rtuitlab.itlab.domain.use_cases.users.GetCurrentUserUseCase
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.SwipingStates
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import java.time.Instant
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalPagerApi
@HiltViewModel
class EventsViewModel @Inject constructor(
	private val getEvents: GetEventsUseCase,
	private val getUserEvents: GetUserEventsUseCase,
	private val updateEvents: UpdateEventsUseCase,
	private val updateInvitations: UpdateInvitationsUseCase,
	private val answerInvitation: AnswerInvitationUseCase,
	private val deleteInvitation: DeleteNotificationUseCase,
	getCurrentUser: GetCurrentUserUseCase,
	getInvitations: GetInvitationsUseCase
) : ViewModel() {

	private val userId = getCurrentUser().map {
		it?.id
	}.stateIn(viewModelScope, SharingStarted.Eagerly, null)

	private var _beginEventsDate = MutableStateFlow(Clock.System.now().minus(7, DateTimeUnit.DAY).toEpochMilliseconds())
	val beginEventsDate = _beginEventsDate.asStateFlow()
	private var _endEventsDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
	val endEventsDate = _endEventsDate.asStateFlow()

	val pagerState = PagerState()

	val swipingState = SwipeableState(SwipingStates.EXPANDED)

	private var _isDateSelectionMade = MutableStateFlow(false)
	val isDateSelectionMade = _isDateSelectionMade.asStateFlow()

	private val _isRefreshing = MutableStateFlow(false)
	val isRefreshing = _isRefreshing.asStateFlow()

	private val _areInvitationsRefreshing = MutableStateFlow(false)
	val areInvitationsRefreshing = _areInvitationsRefreshing.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UiEvent>()
	val uiEvents = _uiEvents.asSharedFlow()

	private val searchQuery = MutableStateFlow("")

	val pendingEvents = combine(searchQuery, beginEventsDate, endEventsDate, isDateSelectionMade) { query, begin, end, isDateSelectionMade ->
		Triple(
			first = query,
			second = if (isDateSelectionMade) begin.toIsoString(false) else nowAsIso8601(),
			third = if (isDateSelectionMade) end.toIsoString(true) else endOfTimes
		)
	}.flatMapLatest {
		getEvents.search(
			query = it.first,
			begin = it.second,
			end = it.third
		)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	val pastEvents = searchQuery.flatMapLatest {
		getEvents.search(
			query = it,
			begin = Instant.MIN.toString(),
			end = nowAsIso8601()
		)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	val userEvents = searchQuery.flatMapLatest {
		getUserEvents.search(it)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	val invitations = getInvitations()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	val invitationsCount = invitations.map {
		it.size
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

	private var _showPastEvents = MutableStateFlow(false)
	val showPastEvents = _showPastEvents.asStateFlow()

	fun toggleShowPastEvents(show: Boolean) = viewModelScope.launch {
		_showPastEvents.value = show
		if (show) {
			update(end = nowAsIso8601())
		}
	}

	private fun update(begin: String? = null, end: String? = null) = viewModelScope.launch {
		_isRefreshing.emit(true)
		updateEvents(begin, end).handle(
			onError = {
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
		_isRefreshing.emit(false)
	}

	fun updatePendingEvents() = viewModelScope.launch {
		_isRefreshing.emit(true)
		updateEvents.pending().handle(
			onError = {
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
		_isRefreshing.emit(false)
	}

	fun updateUserEvents() = viewModelScope.launch {
		userId.value?.let {
			_isRefreshing.emit(true)
			updateEvents.user(it).handle(
				onError = {
					_uiEvents.emit(UiEvent.Snackbar(it))
				}
			)
			_isRefreshing.emit(false)
		}
	}

	fun updateNotifications() = viewModelScope.launch {
		_areInvitationsRefreshing.emit(true)
		updateInvitations().handle(
			onError = {
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
		_areInvitationsRefreshing.emit(false)
	}

	fun setEventsDates(begin: Long, end: Long) {
		_showPastEvents.value = false
		_isDateSelectionMade.value = true
		_beginEventsDate.value = begin
		_endEventsDate.value = end
		update(
			begin = begin.toIsoString(false),
			end = end.toIsoString(true)
		)
	}

	fun clearDateSelection() {
		_isDateSelectionMade.value = false
	}

	fun rejectInvitation(
		notification: EventInvitation,
		successMessage: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		answerInvitation.accept(notification.placeId).handle(
			onSuccess = {
				onFinish()
				deleteInvitation(notification.eventId, notification.placeId)
				_uiEvents.emit(UiEvent.Snackbar(it.errorBody()?.string() ?: successMessage))
			},
			onError = {
				onFinish()
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
	}
	fun acceptInvitation(
		notification: EventInvitation,
		successMessage: String,
		onFinish: () -> Unit
	) = viewModelScope.launch {
		answerInvitation.reject(notification.placeId).handle(
			onSuccess = {
				onFinish()
				deleteInvitation(notification.eventId, notification.placeId)
				_uiEvents.emit(UiEvent.Snackbar(it.errorBody()?.string() ?: successMessage))
			},
			onError = {
				onFinish()
				_uiEvents.emit(UiEvent.Snackbar(it))
			}
		)
	}

	fun onSearch(query: String) {
		searchQuery.update { query }
	}

}