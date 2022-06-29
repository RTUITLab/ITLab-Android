package ru.rtuitlab.itlab.data.remote.api.purchases

import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R

enum class PurchaseSortingDirection {
    ASC, DESC
}

// Enum marker
interface PurchaseStatus

enum class PurchaseStatusApi: PurchaseStatus {
    AWAIT,
    ACCEPT,
    DECLINE,
    UNDEFINED
}

enum class PurchaseStatusUi: PurchaseStatus {
    AWAIT,
    ACCEPT,
    DECLINE,
    ALL
}

enum class PurchaseSortingOrder(val key: String, @StringRes val nameResource: Int) {
    NAME("name", R.string.by_name),
    PRICE("price", R.string.by_price),
    PURCHASE_DATE("purchaseDate", R.string.by_purchase_date),
    ADDITION_DATE("additionDate", R.string.by_addition_date)
}

object PurchaseSortingBy {
    const val id: String = "id"
    const val purchaserId: String = "purchaserId"
    const val name: String = "name"
    const val price: String = "price"
    const val description: String = "description"
    const val purchaseDate: String = "purchaseDate"
    const val additionDate: String = "additionDate"
    const val checkPhotoUrl: String = "checkPhotoUrl"
    const val purchasePhotoUrl: String = "purchasePhotoUrl"
}