package ru.rtuitlab.itlab.viewmodels

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.api.Resource
import ru.rtuitlab.itlab.api.feedback.models.FeedbackModel
import ru.rtuitlab.itlab.repositories.FeedbackRepository
import ru.rtuitlab.itlab.utils.emitInIO
import javax.inject.Inject

@ExperimentalPagerApi
@HiltViewModel
class FeedbackViewModel @Inject constructor(
	private val repository: FeedbackRepository
) : ViewModel() {

	val pagerState = PagerState()
	val snackbarHostState = SnackbarHostState()

	private var initiated = false
	private var incomingInitiated = false
	private var readInitiated = false

	fun init() {
		if (initiated) return
		fetchFeedback(false)
		fetchFeedback(true)
		initiated = true
	}

	private val _readFeedbackResponsesFlow =
		MutableStateFlow<Resource<List<FeedbackModel>>>(Resource.Loading)
	val readFeedbackResponsesFlow = _readFeedbackResponsesFlow.asStateFlow()

	private val _incomingFeedbackResponsesFlow =
		MutableStateFlow<Resource<List<FeedbackModel>>>(Resource.Loading)
	val incomingFeedbackResponsesFlow = _incomingFeedbackResponsesFlow.asStateFlow()

	private var cachedIncomingFeedback = listOf<FeedbackModel>()
	private var cachedReadFeedback = listOf<FeedbackModel>()

	private val _incomingFeedbackFlow = MutableStateFlow(cachedIncomingFeedback)
	val incomingFeedbackFlow = _incomingFeedbackFlow.asStateFlow()

	private val _readFeedbackFlow = MutableStateFlow(cachedIncomingFeedback)
	val readFeedbackFlow = _readFeedbackFlow.asStateFlow()

	fun onResourceSuccess(feedback: List<FeedbackModel>, isAnswered: Boolean) {
		if (isAnswered && !readInitiated) {
			cachedReadFeedback = feedback
			_readFeedbackFlow.value = feedback
			readInitiated = true
		} else if (!incomingInitiated) {
			cachedIncomingFeedback = feedback
			_incomingFeedbackFlow.value = feedback
			incomingInitiated = true
		}
	}

	private var searchQuery = ""

	fun onSearch(query: String) {
		searchQuery = query
		_incomingFeedbackFlow.value = cachedIncomingFeedback.filter { filterSearchResult(it, query) }
		_readFeedbackFlow.value = cachedReadFeedback.filter { filterSearchResult(it, query) }
	}

	fun onUpdateFeedback(
		feedback: FeedbackModel,
		newAnsweredValue: Boolean,
		onFinish: (Boolean) -> Unit
	) = viewModelScope.launch {
		repository.onToggleAnswered(
			feedback.updated(newAnsweredValue)
		).handle(
			onError = { msg ->
				snackbarHostState.showSnackbar(
					message = msg
				)
				onFinish(false)
			},
			onSuccess = {
				val newData = feedback.copy(answered = newAnsweredValue)
				if (newAnsweredValue) {
					cachedReadFeedback = listOf(newData) + cachedReadFeedback
				} else {
					cachedIncomingFeedback = listOf(newData) + cachedIncomingFeedback
				}
				_incomingFeedbackFlow.value = cachedIncomingFeedback
				_readFeedbackFlow.value = cachedReadFeedback
				onSearch(searchQuery)
				onFinish(true)
			}
		)
	}

	fun onDeleteFeedback(
		feedback: FeedbackModel,
		onFinish: (Boolean) -> Unit
	) = viewModelScope.launch {
		repository.onDelete(feedback.id).handle(
			onError = { msg ->
				onFinish(false)
				snackbarHostState.showSnackbar(
					message = msg
				)
			},
			onSuccess = {
				delete(feedback)
				onFinish(true)
			}
		)
	}

	fun delete(feedback: FeedbackModel) {
		if (feedback.answered) {
			cachedReadFeedback = cachedReadFeedback.filter { it != feedback }
		} else {
			cachedIncomingFeedback = cachedIncomingFeedback.filter { it != feedback }
		}
		// Restore search state
		onSearch(searchQuery)
	}

	fun onRefresh() {
		fetchFeedback(true)
		fetchFeedback(false)
	}

	private fun fetchFeedback(isAnswered: Boolean) =
		(if (isAnswered) _readFeedbackResponsesFlow
		else _incomingFeedbackResponsesFlow)
			.emitInIO(viewModelScope) {
				repository.fetchFeedback(isAnswered)
			}

	private fun filterSearchResult(feedback: FeedbackModel, query: String) =
		feedback.name.contains(query.trim(), ignoreCase = true)
			||
		feedback.email.contains(query.trim(), ignoreCase = true)
			||
		feedback.message.contains(query.trim(), ignoreCase = true)
}