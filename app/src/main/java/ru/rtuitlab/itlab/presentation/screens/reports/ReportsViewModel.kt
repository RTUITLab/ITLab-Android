package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.rtuitlab.itlab.common.extensions.collectUntil
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.domain.use_cases.reports.CreateReportUseCase
import ru.rtuitlab.itlab.domain.use_cases.reports.GetReportsUseCase
import ru.rtuitlab.itlab.domain.use_cases.reports.UpdateReportsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetCurrentUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagerApi
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReports: GetReportsUseCase,
    private val updateReports: UpdateReportsUseCase,
    private val createReport: CreateReportUseCase,
    getCurrentUser: GetCurrentUserUseCase,
    getUsers: GetUsersUseCase
): ViewModel() {

    private val currentUserId = getCurrentUser().map {
        it?.id
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            currentUserId.collectUntil(
                condition = { it != null },
                action = {
                    it?.let {
                        launch {
                            updateReports.firstTime(
                                userId = it,
                                refreshState = _isRefreshing,
                                onError = {
                                    launch {
                                        _uiEvents.emit(UiEvent.Snackbar(it))
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    val pagerState = PagerState()

    private val searchQuery = MutableStateFlow("")

    val reportsAboutUser = searchQuery.combine(currentUserId) { query, userId ->
        query to userId
    }.flatMapLatest { (query, userId) ->
        userId?.let { id ->
            getReports.searchAboutUser(query, id).map {
                it.map { it.toReport() }
            }
        } ?: emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val reportsFromUser = searchQuery.combine(currentUserId) { query, userId ->
        query to userId
    }.flatMapLatest { (query, userId) ->
        userId?.let { id ->
            getReports.searchFromUser(query, id).map {
                it.map { it.toReport() }
            }
        } ?: emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val users = getUsers().map {
        it.map { it.toUser() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun update() = viewModelScope.launch {
        _isRefreshing.emit(true)
        currentUserId.value?.let {
            updateReports(it).handle(
                onError = {
                    _uiEvents.emit(UiEvent.Snackbar(it))
                }
            )
        }
        _isRefreshing.emit(false)
    }

    fun onSearch(query: String) {
        searchQuery.value = query
    }

    fun onSubmitReport(
        title: String,
        text: String,
        implementerId: String? = null,
        successMessage: String,
        onFinished: (isSuccessful: Boolean, newReport: ReportDto?) -> Unit
    ) = viewModelScope.launch {
        createReport(implementerId, title, text).handle(
            onSuccess = { newReport ->
                withContext(Dispatchers.Main) {
                    pagerState.scrollToPage(1)
                    onFinished(true, newReport)
                }
                delay(100) // NewReport screen will close, and Reports screen will collect this event
                _uiEvents.emit(UiEvent.Snackbar(successMessage))
            },
            onError = {
                withContext(Dispatchers.Main) {
                    onFinished(false, null)
                }
                _uiEvents.emit(UiEvent.Snackbar(it))
            }
        )
    }
}