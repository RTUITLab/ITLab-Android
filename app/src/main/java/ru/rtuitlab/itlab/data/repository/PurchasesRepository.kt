package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.purchases.*
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseCreateRequest
import javax.inject.Inject

class PurchasesRepository @Inject constructor(
    private val purchasesApi: PurchasesApi,
    private val handler: ResponseHandler
) {
    suspend fun fetchPurchase(id: Int) = handler {
        purchasesApi.getPurchase(id)
    }

    suspend fun updatePurchase(purchase: PurchaseCreateRequest) = handler {
        purchasesApi.updatePurchase(purchase)
    }

    suspend fun deletePurchase(id: Int) = handler {
        purchasesApi.deletePurchase(id)
    }

    suspend fun fetchPurchases(
        pageNumber: Int,
        pageSize: Int = 10,
        sortingDirection: PurchaseSortingDirection = PurchaseSortingDirection.ASC,
        sortBy: PurchaseSortingOrder = PurchaseSortingOrder.ADDITION_DATE,
        purchaseStartDate: String? = null,
        purchaseEndDate: String? = null,
        purchaseStatus: PurchaseStatus = PurchaseStatusApi.AWAIT
    ) = handler {
        purchasesApi.getPurchases(
            pageNumber,
            pageSize,
            sortingDirection.toString(),
            sortBy.key,
            purchaseStartDate,
            purchaseEndDate,
            if (purchaseStatus == PurchaseStatusUi.ALL) null else purchaseStatus.toString()
        )
    }
}