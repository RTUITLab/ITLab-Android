@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.purchases.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.presentation.screens.purchases.PurchasesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.AppDropdownMenu
import ru.rtuitlab.itlab.presentation.ui.components.LabeledRadioButton
import ru.rtuitlab.itlab.presentation.ui.components.LabeledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField

// Will be developed further and used once implementation details are clarified
@Composable
fun PurchasesBottomSheet(
    purchasesViewModel: PurchasesViewModel = viewModel()
) {

    val state by purchasesViewModel.state.collectAsState()

    var isSortingDropdownShown by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isSortingDropdownShown) 180f else 0f
    )

    Text(
        text = stringResource(R.string.reports_filters),
        style = MaterialTheme.typography.titleMedium
    )

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = LocalContentColor.current,
        disabledBorderColor = LocalContentColor.current,
        disabledLabelColor = LocalContentColor.current,
        disabledLeadingIconColor = LocalContentColor.current.copy(.8f)
    )

    AppDropdownMenu(
        anchor = { show ->
            OutlinedAppTextField(
                value = stringResource(state.selectedSortingOrder.nameResource),
                onValueChange = {},
                onClick = {
                    show()
                    isSortingDropdownShown = true
                },
                trailingIcon = {
                    Icon(
                        modifier = Modifier.rotate(arrowRotation),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(R.string.reports_sorting))
                },
                enabled = false,
                colors = textFieldColors
            )
        },
        content = {
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
                        PurchaseSortingDirection.ASC
                    )
                    else purchasesViewModel.onSortingDirectionChange(
                        PurchaseSortingDirection.DESC
                    )
                },
                label = stringResource(R.string.sort_asc)
            )
        }
    )

}