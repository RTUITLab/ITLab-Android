package ru.rtuitlab.itlab.data.remote.api.purchases.models

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseResolveRequest(
    val status: String,
    val decisionComment: String? = null
)
