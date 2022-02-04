package ru.rtuitlab.itlab.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
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
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.events.models.EventModel
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.repositories.EventsRepository
import ru.rtuitlab.itlab.ui.shared.top_app_bars.SwipingStates
import ru.rtuitlab.itlab.utils.emitInIO
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalPagerApi
@HiltViewModel
class EventsViewModel @Inject constructor(
	private val repository: EventsRepository,
	private val authStateStorage: AuthStateStorage
) : ViewModel() {

	private lateinit var userId: String

	init {
		viewModelScope.launch {
			userId = authStateStorage.userIdFlow.first()
		}
	}

	val pagerState = PagerState()

	val swipingState = SwipeableState(SwipingStates.EXPANDED)

	val listState = LazyListState()

	private val _eventsListResponseFlow =
		MutableStateFlow<Resource<List<EventModel>>>(Resource.Loading)
	val eventsListResponsesFlow = _eventsListResponseFlow.asStateFlow().also {
		fetchPendingEvents()
	}

	private val _userEventsListResponseFlow =
		MutableStateFlow<Resource<List<EventModel>>>(Resource.Loading)
	val userEventsListResponsesFlow = _userEventsListResponseFlow.asStateFlow()

	fun fetchAllEvents() = _eventsListResponseFlow.emitInIO(viewModelScope) {
		repository.fetchAllEvents()
	}

	fun fetchPendingEvents() = _eventsListResponseFlow.emitInIO(viewModelScope) {
		repository.fetchPendingEvents()
	}

	fun fetchUserEvents(begin: String? = null, end: String? = null) = _userEventsListResponseFlow.emitInIO(viewModelScope) {
		repository.fetchUserEvents(userId, begin, end)
	}

	private var searchQuery = ""

	fun onSearch(query: String) {
		searchQuery = query
	}
}