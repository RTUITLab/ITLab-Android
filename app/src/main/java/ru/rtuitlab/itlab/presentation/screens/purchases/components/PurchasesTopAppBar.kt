package ru.rtuitlab.itlab.presentation.screens.purchases.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.presentation.screens.purchases.PurchasesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabelledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.LabeledRadioButton
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun PurchasesTopAppBar(
    purchasesViewModel: PurchasesViewModel = singletonViewModel()
) {
    val state by purchasesViewModel.state.collectAsState()
    BasicTopAppBar(
        text = stringResource(R.string.purchases),
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = { collapse ->
                    PurchaseSortingOrder.values().forEach { order ->
                        LabeledRadioButton(
                            state = order == state.selectedSortingOrder,
                            onCheckedChange = {
                                if (!it) return@LabeledRadioButton
                                purchasesViewModel.onSortingOrderChange(order)
                            },
                            label = stringResource(order.nameResource),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                    Divider()
                    LabelledCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        checked = state.selectedSortingDirection == PurchaseSortingDirection.ASC,
                        onCheckedChange = { isAsc ->
                            if (isAsc) purchasesViewModel.onSortingDirectionChange(PurchaseSortingDirection.ASC)
                            else purchasesViewModel.onSortingDirectionChange(PurchaseSortingDirection.DESC)
                        },
                        label = stringResource(R.string.sort_asc)
                    )
                }
            )
        )
    )
}