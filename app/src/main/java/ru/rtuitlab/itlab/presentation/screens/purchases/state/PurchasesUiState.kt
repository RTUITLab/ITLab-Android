package ru.rtuitlab.itlab.presentation.screens.purchases.state

import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatus
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase

data class PurchasesUiState(
    val page: Int = 0,
    val purchases: List<Purchase> = emptyList(),
    val errorMessage: String? = null,
    val endReached: Boolean = false,
    val searchQuery: String = "",
    val startDate: String? = null,
    val endDate: String? = null,
    val selectedStatus: PurchaseStatus = PurchaseStatusUi.ALL,
    val selectedSortingOrder: PurchaseSortingOrder = PurchaseSortingOrder.ADDITION_DATE,
    val selectedSortingDirection: PurchaseSortingDirection = PurchaseSortingDirection.DESC,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
