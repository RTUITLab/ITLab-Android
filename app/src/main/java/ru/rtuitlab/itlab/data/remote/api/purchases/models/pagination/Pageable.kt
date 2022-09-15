package ru.rtuitlab.itlab.data.remote.api.purchases.models.pagination


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val offset: Int,
    val sort: SortingConfig,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean
)