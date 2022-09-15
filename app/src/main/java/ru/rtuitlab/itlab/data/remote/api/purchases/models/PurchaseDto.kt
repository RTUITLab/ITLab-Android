package ru.rtuitlab.itlab.data.remote.api.purchases.models


import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.users.models.User

@Serializable
data class PurchaseDto(
    val id: Int,
    val purchaserId: String,
    val name: String,
    val price: Float,
    val description: String?,
    val purchaseDate: String,
    val additionDate: String,
    val checkPhotoUrl: String,
    val purchasePhotoUrl: String?,
    val solution: PurchaseSolutionDto
) {
    fun toPurchase(
        purchaser: User,
        solver: User? = null
    ) =
        Purchase(
            id,
            purchaser,
            name,
            price,
            description,
            purchaseDate,
            additionDate,
            checkPhotoUrl,
            purchasePhotoUrl,
            solution.toPurchaseSolution(solver)
        )
}

data class Purchase(
    val id: Int,
    val purchaser: User,
    val name: String,
    val price: Float,
    val description: String?,
    val purchaseDate: String,
    val additionDate: String,
    val checkPhotoUrl: String,
    val purchasePhotoUrl: String?,
    val solution: PurchaseSolution
)