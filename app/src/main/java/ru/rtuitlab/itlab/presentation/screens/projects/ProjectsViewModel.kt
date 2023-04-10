package ru.rtuitlab.itlab.presentation.screens.projects

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.extensions.collectUntil
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.domain.use_cases.projects.GetProjectsPageUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectsOnlineUiState
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val getProjectsOnline: GetProjectsPageUseCase,
    getUsers: GetUsersUseCase
): ViewModel() {


    private var fetchingJob: Job? = null

    private val _state = MutableStateFlow(ProjectsOnlineUiState())
    val onlineState = _state.asStateFlow()

    private val users = getUsers().map {
        it.map { it.toUser() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    val pageSize = 20

    private val paginator = Paginator(
        initialKey = onlineState.value.offset,
        onLoadingUpdated = { isLoading ->
            _state.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            getProjectsOnline(
                limit = pageSize,
                onlyManagedProjects = false,
                offset = nextPage,
                onlyParticipatedProjects = false,
                matcher = if (_state.value.searchQuery.isBlank()) ""
                    else "name:${_state.value.searchQuery}",
                sortBy = "name:desc"
            )
        },
        getNextKey = { _state.value.offset + pageSize },
        onError = { errorMessage ->
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = errorMessage
                )
            }
        },
        onSuccess = { result, newOffset ->
            _state.update { state ->
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

                // Collecting search query and relaunching fetching with a new search query
                viewModelScope.launch {
                    _state
                        .map { it.searchQuery }
                        .distinctUntilChanged()
                        .collect {
                            Log.v("ProjectsViewModel", "New query: $it")
                            fetchingJob?.cancel()
                            paginator.reset()
                            _state.update {
                                it.copy(
                                    offset = 0,
                                    projects = emptyList(),
                                    paginationState = null
                                )
                            }
                            fetchNextItems()
                        }
                }
            }
        }


    }

    fun fetchNextItems() {
        fetchingJob = viewModelScope.launch(Dispatchers.IO) {
            paginator.fetchNext()
        }
    }

    fun onRefresh() {
        paginator.reset()
        _state.update {
            it.copy(
                offset = 0,
                projects = emptyList(),
                paginationState = null
            )
        }
        fetchNextItems()
    }

    fun onSearch(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }
        Log.v("ProjectsViewModel", "New query: ${_state.value.searchQuery}")
    }
}