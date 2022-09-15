package ru.rtuitlab.itlab.data.remote.api.purchases.models.pagination


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SortingConfig(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)