package ru.rtuitlab.itlab.presentation.screens.purchases.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.purchases.PurchasesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LoadableButtonContent
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryButton
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@Composable
fun PurchaseTopAppBar(
    purchasesViewModel: PurchasesViewModel = singletonViewModel(),
    appBarViewModel: AppBarViewModel = singletonViewModel()
) {
    val state by purchasesViewModel.state.collectAsState()
    val purchaseState = state.selectedPurchaseState!!
    val currentScreen by appBarViewModel.currentScreen.collectAsState()
    val context = LocalContext.current
    val navController = LocalNavController.current

    BasicTopAppBar(
        text = stringResource(
            currentScreen.screenNameResource,
            (currentScreen as AppScreen.PurchaseDetails).title
        ),
        options = if (purchaseState.purchase.solution.status == PurchaseStatusApi.AWAIT) listOf(
            AppBarOption.Clickable(
                icon = ImageVector.vectorResource(R.drawable.ic_delete),
                onClick = {
                    purchasesViewModel.showDeletingDialog()
                }
            )
        ) else listOf(),
        titleSharedElementKey = "${state.selectedPurchaseState?.purchase?.id}/name",
        onBackAction = {
            navController.popBackStack()
        }
    )

    if (purchaseState.isDeletionDialogShown)
        AlertDialog(
            title = {
                Text(
                    text = stringResource(R.string.purchase_delete),
                    style = MaterialTheme.typography.h4
                )
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = stringResource(R.string.purchase_delete_confirmation, purchaseState.purchase.name))
                }
            },
            confirmButton = {
                PrimaryButton(
                    onClick = {
                        purchasesViewModel.onDeletePurchase(
                            purchase = purchaseState.purchase,
                            successMessage = context.getString(R.string.purchase_delete_success)
                        ) { isSuccessful ->

                            purchasesViewModel.hideDeletingDialog()
                            if (isSuccessful) {
                                navController.popBackStack()
                            }
                        }
                    },
                    text = stringResource(R.string.confirm_yes)
                ) { text ->
                    LoadableButtonContent(
                        isLoading = purchaseState.isDeletionInProgress,
                        strokeWidth = 2.dp
                    ) {
                        text()
                    }
                }
            },
            dismissButton = {
                PrimaryButton(
                    onClick = purchasesViewModel::hideDeletingDialog,
                    text = stringResource(R.string.cancel)
                )
            },
            onDismissRequest = purchasesViewModel::hideDeletingDialog
        )
}