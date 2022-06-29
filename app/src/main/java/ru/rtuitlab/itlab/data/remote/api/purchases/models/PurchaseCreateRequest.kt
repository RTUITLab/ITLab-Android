package ru.rtuitlab.itlab.data.remote.api.purchases.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseCreateRequest(
    val name: String,
    val price: Int,
    val description: String,
    val purchaseDate: String,
    @SerialName("checkPhotoUrl")
    val receiptPhotoUrl: String,
    @SerialName("purchasePhotoUrl")
    val itemPhotoUrl: String
)