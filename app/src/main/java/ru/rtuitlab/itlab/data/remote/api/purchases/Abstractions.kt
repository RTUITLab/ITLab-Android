package ru.rtuitlab.itlab.data.remote.api.purchases

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

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

enum class PurchaseStatusUi(@StringRes val nameResource: Int, val color: Color): PurchaseStatus {
    ALL(R.string.status_all, AppColors.accent.value),
    AWAIT(R.string.status_await, AppColors.orange),
    ACCEPT(R.string.status_confirmed, AppColors.green),
    DECLINE(R.string.status_rejected, AppColors.red)
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