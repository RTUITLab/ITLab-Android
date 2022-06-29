package ru.rtuitlab.itlab.data.remote.api.purchases.models.pagination


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseDto

@Serializable
data class PaginationResult(
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: List<PurchaseDto>,
    val number: Int,
    val sort: SortingConfig,
    val pageable: Pageable,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)