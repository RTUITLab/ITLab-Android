package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.collectUntil
import ru.rtuitlab.itlab.data.remote.api.projects.SortingDirection
import ru.rtuitlab.itlab.data.remote.api.projects.SortingField
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.domain.use_cases.projects.GetProjectsPageUseCase
import ru.rtuitlab.itlab.domain.use_cases.projects.SearchProjectsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsBottomSheetState
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsOfflineUiState
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsOnlineUiState
import ru.rtuitlab.itlab.presentation.utils.NetworkMonitor
import ru.rtuitlab.itlab.presentation.utils.NetworkStatus
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val getProjectsOnline: GetProjectsPageUseCase,
    private val searchProjects: SearchProjectsUseCase,
    networkMonitor: NetworkMonitor,
    getUsers: GetUsersUseCase
): ViewModel() {

    private var fetchingJob: Job? = null

    private val _onlineState = MutableStateFlow(ProjectsOnlineUiState())
    val onlineState = _onlineState.asStateFlow()

    private val _offlineState = MutableStateFlow(ProjectsOfflineUiState())
    val offlineState = _offlineState.asStateFlow()

    private val _bottomSheetState = MutableStateFlow(ProjectsBottomSheetState())
    val bottomSheetState = _bottomSheetState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    var shouldShowNetworkAction by mutableStateOf(false)
        private set

    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    private val users = getUsers().map {
        it.map { it.toUser() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiEvents = MutableSharedFlow<UiEvent>(replay = 1)
    val uiEvents = _uiEvents.asSharedFlow()

    val pageSize = 20

    private val paginator = Paginator(
        initialKey = onlineState.value.offset,
        onLoadingUpdated = { isLoading ->
            _onlineState.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            getProjectsOnline(
                limit = pageSize,
                onlyManagedProjects = bottomSheetState.value.isManagedProjectsChecked,
                offset = nextPage,
                onlyParticipatedProjects = bottomSheetState.value.isParticipatedProjectsChecked,
                matcher = if (searchQuery.value.isBlank()) ""
                    else "name:${searchQuery.value}",
                sortBy = bottomSheetState.value.sortingQuery
            )
        },
        getNextKey = { _onlineState.value.offset + pageSize },
        onError = { errorMessage ->
            _onlineState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = errorMessage
                )
            }
        },
        onSuccess = { result, newOffset ->
            _onlineState.update { state ->
                state.copy(
                    offset = newOffset,
                    projects = state.projects + result.items.map { it.toProjectCompact(users.value) },
                    endReached = !result.hasMore,
                    paginationState = result,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    )

    init {
        viewModelScope.launch {
            users.collectUntil({it.isNotEmpty()}) {

                // Collecting search query, network status and filters/sorters state
                // and relaunching fetching with a combination of the three
                viewModelScope.launch {
                    combine(searchQuery, isOnline, bottomSheetState) { query, isOnline, bottomSheetState ->
                        Triple(query, isOnline, bottomSheetState)
                    }
                        .collect { (query, isOnline, bottomSheetState) ->
                            if (isOnline) {
                                fetchingJob?.cancel()
                                paginator.reset()
                                _onlineState.update {
                                    it.copy(
                                        offset = 0,
                                        projects = emptyList(),
                                        paginationState = null
                                    )
                                }
                                fetchNextItems()
                            } else {
                                _offlineState.update {
                                    it.copy(
                                        projects = searchProjects(
                                            query = query,
                                            sortingFieldLiteral = bottomSheetState.selectedSortingField.localLiteral,
                                            sortingDirectionLiteral = bottomSheetState.selectedSortingDirection.literal,
                                            managedProjects = bottomSheetState.isManagedProjectsChecked,
                                            participatedProjects = bottomSheetState.isParticipatedProjectsChecked
                                        )
                                    )
                                }
                            }
                        }
                }
            }
        }

        viewModelScope.launch {
            networkMonitor.networkStatus.collect {
                // If user is in Offline mode but has connection, prompt them to switch back
                if (!isOnline.value && it == NetworkStatus.Available) {
                    _uiEvents.emit(
                        UiEvent.Snackbar(
                            resId = R.string.projects_back_online_prompt,
                            actionLabel = R.string.projects_switch,
                            duration = SnackbarDuration.Short,
                            onActionPerformed = {
                                when (it) {
                                    SnackbarResult.ActionPerformed -> switchNetworkState(true)
                                    SnackbarResult.Dismissed -> {
                                        // Show switch icon in bottom bar
                                        shouldShowNetworkAction = true
                                    }
                                }
                            }
                        )
                    )
                }
            }
        }
    }

    fun fetchNextItems() {
        _onlineState.update {
            it.copy(errorMessage = null)
        }
        fetchingJob = viewModelScope.launch(Dispatchers.IO) {
            paginator.fetchNext()
        }
    }

    fun onRefresh() {
        paginator.reset()
        _onlineState.update {
            it.copy(
                offset = 0,
                projects = emptyList(),
                paginationState = null
            )
        }
        fetchNextItems()
    }

    fun onSearch(query: String) {
        _searchQuery.update { query }
    }

    fun switchNetworkState(isOnline: Boolean) {
        _isOnline.update { isOnline }
        shouldShowNetworkAction = false
    }

    fun onSortingFieldChange(sortingField: SortingField) {
        _bottomSheetState.update {
            it.copy(
                selectedSortingField = sortingField
            )
        }
    }

    fun onManagedProjectsChange(isChecked: Boolean) {
        _bottomSheetState.update {
            it.copy(
                isManagedProjectsChecked = isChecked
            )
        }
    }

    fun onParticipatedProjectsChange(isChecked: Boolean) {
        _bottomSheetState.update {
            it.copy(
                isParticipatedProjectsChecked = isChecked
            )
        }
    }

    fun onSortingOrderChange(isChecked: Boolean) {
        _bottomSheetState.update {
            it.copy(
                selectedSortingDirection = if (isChecked) SortingDirection.ASC else SortingDirection.DESC
            )
        }
    }

}