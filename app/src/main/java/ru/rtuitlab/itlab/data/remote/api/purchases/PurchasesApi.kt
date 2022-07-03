package ru.rtuitlab.itlab.data.remote.api.purchases

import retrofit2.Response
import retrofit2.http.*
import ru.rtuitlab.itlab.data.remote.api.purchases.models.*
import ru.rtuitlab.itlab.data.remote.api.purchases.models.pagination.PaginationResult

private const val base = "purchases/v2"

interface PurchasesApi {
    @GET("$base/{id}")
    suspend fun getPurchase(
        @Path("id") id: Int
    ): PurchaseDto

    @PUT("$base/{id}")
    suspend fun updatePurchase(
        @Body purchase: PurchaseCreateRequest
    ): PurchaseDto

    @GET(base)
    suspend fun getPurchases(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 10,
        @Query("sortDirection") sortDirection: String = PurchaseSortingDirection.ASC.toString(),
        @Query("sortBy") sortBy: String = PurchaseSortingBy.purchaseDate,
        @Query("purchaseStartDate") purchaseStartDate: String? = null,
        @Query("purchaseEndDate") purchaseEndDate: String? = null,
        @Query("purchaseStatus") purchaseStatus: String? = PurchaseStatusApi.AWAIT.toString()
    ): PaginationResult

    @PUT("$base/{id}/solve")
    suspend fun resolvePurchase(
        @Path("id") id: Int,
        @Body resolveRequest: PurchaseResolveRequest
    ): PurchaseDto

    @POST(base)
    suspend fun createPurchase(
        @Body purchase: PurchaseCreateRequest
    ): PurchaseDto

    @DELETE("$base/{id}")
    suspend fun deletePurchase(
        @Path("id") id: Int
    ): Response<Unit>

}