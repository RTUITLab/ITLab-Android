package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchasesApi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseCreateRequest
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseDto
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseResolveRequest
import ru.rtuitlab.itlab.data.remote.api.purchases.models.pagination.PaginationResult

class MockPurchasesApi: PurchasesApi {
    override suspend fun getPurchase(id: Int): PurchaseDto {
        TODO("Not yet implemented")
    }

    override suspend fun updatePurchase(purchase: PurchaseCreateRequest): PurchaseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getPurchases(
        pageNumber: Int,
        pageSize: Int,
        sortDirection: String,
        sortBy: String,
        purchaseStartDate: String?,
        purchaseEndDate: String?,
        purchaseStatus: String?
    ): PaginationResult {
        TODO("Not yet implemented")
    }

    override suspend fun resolvePurchase(
        id: Int,
        resolveRequest: PurchaseResolveRequest
    ): PurchaseDto {
        TODO("Not yet implemented")
    }

    override suspend fun createPurchase(purchase: PurchaseCreateRequest): PurchaseDto {
        TODO("Not yet implemented")
    }

    override suspend fun deletePurchase(id: Int): Response<Unit> {
        TODO("Not yet implemented")
    }
}