package ru.rtuitlab.itlab.data.remote.api.purchases.models


import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: Int,
    val purchaserId: String,
    val name: String,
    val price: Int,
    val description: String?,
    val purchaseDate: String,
    val additionDate: String,
    val checkPhotoUrl: String,
    val purchasePhotoUrl: String?,
    val solution: PurchaseSolutionDto
) {
    fun toPurchase() =
        Purchase(
            id,
            purchaserId,
            name,
            price,
            description,
            purchaseDate,
            additionDate,
            checkPhotoUrl,
            purchasePhotoUrl,
            solution.toPurchaseSolution()
        )
}

data class Purchase(
    val id: Int,
    val purchaserId: String,
    val name: String,
    val price: Int,
    val description: String?,
    val purchaseDate: String,
    val additionDate: String,
    val checkPhotoUrl: String,
    val purchasePhotoUrl: String?,
    val solution: PurchaseSolution
)