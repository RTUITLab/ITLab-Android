package ru.rtuitlab.itlab.presentation.screens.purchases

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.data.repository.PurchasesRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchasesUiState
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: PurchasesRepository,
    private val usersRepository: UsersRepository
): ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val pageSize = 10

    private val _state = MutableStateFlow(PurchasesUiState())
    val state = searchQuery.flatMapLatest { query ->
        _state.asStateFlow().map {
            it.copy(
                purchases = it.purchases.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, _state.value)

    private val paginator = Paginator(
        initialKey = state.value.page,
        onLoadingUpdated = {
            _state.value = _state.value.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            _state.value = _state.value.copy(errorMessage = null)
            repository.fetchPurchases(
                pageNumber = nextPage,
                pageSize = pageSize,
                sortingDirection = _state.value.selectedSortingDirection,
                sortBy = _state.value.selectedSortingOrder,
                purchaseStartDate = _state.value.startDate,
                purchaseEndDate = _state.value.endDate,
                purchaseStatus = _state.value.selectedStatus,
            )
        },
        getNextKey = { _state.value.page + 1 },
        onError = {
            _state.value = _state.value.copy(
                errorMessage = it,
                isLoading = false,
                isRefreshing = false
            )
        },
        onSuccess = { result, newPage ->
            _state.value = _state.value.copy(
                page = newPage,
                purchases = _state.value.purchases + result.content.map { purchaseDto ->
                    purchaseDto.toPurchase(
                        purchaser = usersRepository.cachedUsersFlow.value.find { it.id == purchaseDto.purchaserId }!!,
                        solver = usersRepository.cachedUsersFlow.value.find { it.id == purchaseDto.solution.solverId }
                    )
                },
                paginationState = result,
                errorMessage = null,
                isLoading = false,
                endReached = result.content.isEmpty()
            )
        }
    )

    init {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    _state.value = _state.value.copy(isRefreshing = true)
                    usersRepository.usersResponsesFlow.collect {
                        it.handle(
                            onSuccess = {
                                fetchNextItems()
                                _state.value = _state.value.copy(isRefreshing = false)
                                cancel()
                            },
                            onError = {
                                _state.value = _state.value.copy(
                                    isRefreshing = false,
                                    errorMessage = it
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun fetchNextItems() = viewModelScope.launch {
        paginator.fetchNext()
    }

    fun onRefresh() {
        paginator.reset()
        _state.value = _state.value.copy(
            purchases = emptyList(),
            paginationState = null
        )
        fetchNextItems()
    }

    fun onStatusChange(status: PurchaseStatusUi) {
        _state.value = _state.value.copy(
            selectedStatus = status
        )
        onRefresh()
    }

    fun onSortingOrderChange(order: PurchaseSortingOrder) {
        _state.value = _state.value.copy(
            selectedSortingOrder = order
        )
        onRefresh()
    }

    fun onSortingDirectionChange(direction: PurchaseSortingDirection) {
        _state.value = _state.value.copy(
            selectedSortingDirection = direction
        )
        onRefresh()
    }
}