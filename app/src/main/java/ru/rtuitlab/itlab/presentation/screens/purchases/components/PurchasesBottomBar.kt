package ru.rtuitlab.itlab.presentation.screens.purchases.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.purchases.PurchasesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabeledRadioButton
import ru.rtuitlab.itlab.presentation.ui.components.LabeledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.TransitionFloatingActionButton
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun PurchasesBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    purchasesViewModel: PurchasesViewModel = singletonViewModel()
) {
    val state by purchasesViewModel.state.collectAsState()
    val navController = LocalNavController.current
    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        secondaryFloatingActionButton = {
            TransitionFloatingActionButton(
                key = "Purchases/New",
                screenKey = AppScreen.Purchases.route,
                icon = Icons.Default.Add
            ) {
                navController.navigate(AppScreen.NewPurchase.route)
            }
        },
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = {
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
                    LabeledCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        checked = state.selectedSortingDirection == PurchaseSortingDirection.ASC,
                        onCheckedChange = { isAsc ->
                            if (isAsc) purchasesViewModel.onSortingDirectionChange(
                                PurchaseSortingDirection.ASC)
                            else purchasesViewModel.onSortingDirectionChange(
                                PurchaseSortingDirection.DESC)
                        },
                        label = stringResource(R.string.sort_asc)
                    )
                }
            )
        )
    )
}