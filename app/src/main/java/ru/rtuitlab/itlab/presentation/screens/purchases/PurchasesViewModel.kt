package ru.rtuitlab.itlab.presentation.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.data.repository.PurchasesRepository
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchasesUiState
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: PurchasesRepository
): ViewModel() {
    private val _state = MutableStateFlow(PurchasesUiState())
    val state = _state.asStateFlow()

    val paginator = Paginator(
        initialKey = state.value.page,
        onLoadingUpdated = {
            _state.value = _state.value.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            repository.fetchPurchases(
                pageNumber = nextPage,
                sortingDirection = _state.value.selectedSortingDirection,
                sortBy = _state.value.selectedSortingOrder,
                purchaseStartDate = _state.value.startDate,
                purchaseEndDate = _state.value.endDate,
                purchaseStatus = _state.value.selectedStatus,
            )
        },
        getNextKey = { _state.value.page + 1 },
        onError = {
            _state.value = _state.value.copy(errorMessage = it)
        },
        onSuccess = { purchases, newPage ->
            _state.value = _state.value.copy(
                page = newPage,
                purchases = _state.value.purchases + purchases.map { it.toPurchase() },
                errorMessage = null,
                isLoading = false,
                endReached = purchases.isEmpty()
            )
        }
    )

    init {
        fetchNextItems()
    }

    fun fetchNextItems() = viewModelScope.launch {
        paginator.fetchNext()
    }
}