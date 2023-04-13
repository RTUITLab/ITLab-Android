package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.domain.use_cases.projects.GetVersionThreadUseCase
import ru.rtuitlab.itlab.presentation.screens.projects.state.NewsUiState
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val getVersionThread: GetVersionThreadUseCase
) : ViewModel() {
    val versionId: String = savedState["versionId"]!!
    val projectId: String = savedState["projectId"]!!

    private val _state = MutableStateFlow(NewsUiState())
    val state = _state.asStateFlow()

    val pageSize = 20

    private val paginator = Paginator(
        initialKey = state.value.offset,
        onLoadingUpdated = { isLoading ->
            _state.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextOffset ->
            getVersionThread(
                limit = pageSize,
                offset = nextOffset,
                matcher = "",
                projectId = projectId,
                versionId = versionId
            )
        },
        getNextKey = { state.value.offset + pageSize },
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
                    news = state.news + result.items,
                    endReached = !result.hasMore,
                    paginationState = result,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    )

    init {
        fetchNextItems()
    }

    fun fetchNextItems() = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(errorMessage = null)
        }

        paginator.fetchNext()
    }

    fun onRefresh() {
        paginator.reset()
        _state.update {
            it.copy(
                offset = 0,
                news = emptyList(),
                paginationState = null,
                isRefreshing = true
            )
        }
        fetchNextItems()
    }
}