package ru.rtuitlab.itlab.data.remote.api.purchases

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

enum class PurchaseStatusUi(
    @StringRes val nameResource: Int,
    val containerColorFactory: @Composable () -> Color,
    val labelColorFactory: @Composable () -> Color
): PurchaseStatus {
    ALL(
        R.string.status_all,
        { MaterialTheme.colorScheme.primary },
        { MaterialTheme.colorScheme.onPrimary }
    ),
    AWAIT(
        R.string.status_await,
        { ru.rtuitlab.itlab.presentation.ui.theme.Orange45 },
        { ru.rtuitlab.itlab.presentation.ui.theme.Orange92 }
    ),
    ACCEPT(
        R.string.status_confirmed,
        { ru.rtuitlab.itlab.presentation.ui.theme.Green27 },
        { ru.rtuitlab.itlab.presentation.ui.theme.Green92 }
    ),
    DECLINE(
        R.string.status_rejected,
        { MaterialTheme.colorScheme.error },
        { MaterialTheme.colorScheme.onError }
    )
}

enum class PurchaseSortingOrder(val key: String, @StringRes val nameResource: Int) {
    NAME("name", R.string.by_name),
    PRICE("price", R.string.by_price),
    PURCHASE_DATE("purchaseDate", R.string.by_purchase_date),
    ADDITION_DATE("additionDate", R.string.by_addition_date)
}