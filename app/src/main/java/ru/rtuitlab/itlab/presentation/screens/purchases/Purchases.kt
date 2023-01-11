@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package ru.rtuitlab.itlab.presentation.screens.purchases

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchaseCard
import ru.rtuitlab.itlab.presentation.screens.purchases.components.ShimmeredPurchaseCard
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchaseUiState
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@Composable
fun Purchases(
    viewModel: PurchasesViewModel = singletonViewModel()
) {
    val state by viewModel.state.collectAsState()

    val isSolvingAccessible by viewModel.isSolvingAccessible.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    viewModel.uiEvents.collectUiEvents(snackbarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize(),
                state = rememberSwipeRefreshState(state.isRefreshing),
                onRefresh = viewModel::onRefresh
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 15.dp, top = 10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Spacer(modifier = Modifier.width(15.dp))
                            PurchaseStatusUi.values().forEach {

                                val containerColor = it.containerColorFactory()
                                val labelColor = it.labelColorFactory()

                                SuggestionChip(
                                    onClick = {
                                        if (state.selectedStatus != it)
                                            viewModel.onStatusChange(it)
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (state.selectedStatus == it) containerColor else Color.Transparent,
                                        labelColor = (if (state.selectedStatus == it) labelColor else containerColor)
                                            .copy(alpha = ChipDefaults.ContentOpacity)
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        borderColor = MaterialTheme.colorScheme.onSurface.copy(
                                            ChipDefaults.OutlinedBorderOpacity
                                        ),
                                        borderWidth = 1.dp
                                    ),
                                    shape = MaterialTheme.shapes.extraLarge,
                                    label = {
                                        Text(
                                            text = stringResource(it.nameResource),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(15.dp))

                        }
                    }

                    if ((state.paginationState?.totalElements
                            ?: 0) == 0 && !state.isLoading && !state.isRefreshing && state.errorMessage == null
                    ) {
                        item {
                            LoadingError(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                msg = stringResource(R.string.purchases_empty),
                                isScrollable = false
                            )
                            return@item
                        }
                    }
                    itemsIndexed(
                        items = state.purchases,
                        key = { _, it -> it.purchase.id }
                    ) { index, purchaseUiState ->

                        // Normally this is an unhandled side-effect, but in this case we have precise
                        // control over its execution through concrete conditions, so no random calls
                        // to fetchNextItems() will be performed.
                        if (index >= state.purchases.size - 1 && !state.endReached && !state.isLoading && state.errorMessage == null) {
                            viewModel.fetchNextItems()
                        }
                        SharedElement(
                            key = purchaseUiState.purchase.id,
                            screenKey = AppScreen.Reports.route,
                            transitionSpec = SharedElementsTransitionSpec(
                                durationMillis = duration
                            )
                        ) {
                            PurchaseCard(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItemPlacement()
                                    .animateContentSize(),
                                isSolvingAccessible = isSolvingAccessible,
                                onDelete = { viewModel.showDeletingDialog(purchaseUiState.purchase) },
                                onReject = {
                                    viewModel.onReject(
                                        purchase = purchaseUiState.purchase,
                                        successMessage = context.getString(R.string.purchase_rejected)
                                    )
                                },
                                onApprove = {
                                    viewModel.onApprove(
                                        purchase = purchaseUiState.purchase,
                                        successMessage = context.getString(R.string.purchase_approved)
                                    )
                                },
                                state = purchaseUiState
                            )
                        }
                        DeleteConfirmationDialog(
                            isShown = purchaseUiState.isDeletionDialogShown,
                            purchaseState = purchaseUiState,
                            purchasesViewModel = viewModel
                        )
                    }
                    if (state.paginationState?.totalElements == null || state.paginationState!!.totalElements > state.purchases.size) {
                        state.errorMessage?.let {
                            item {
                                LoadingErrorRetry(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    errorMessage = it,
                                    onRetry = viewModel::fetchNextItems
                                )
                            }
                        } ?: items(
                            count = ((state.paginationState?.totalElements
                                ?: 0) - state.purchases.size).coerceAtLeast(viewModel.pageSize)
                        ) {
                            ShimmeredPurchaseCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    isShown: Boolean,
    purchaseState: PurchaseUiState,
    purchasesViewModel: PurchasesViewModel
) {
    val context = LocalContext.current
    if (isShown)
        AlertDialog(
            title = {
                Text(
                    text = stringResource(R.string.purchase_delete)
                )
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(
                            R.string.purchase_delete_confirmation,
                            purchaseState.purchase.name
                        )
                    )
                }
            },
            confirmButton = {
                PrimaryTextButton(
                    onClick = {
                        purchasesViewModel.onDeletePurchase(
                            purchase = purchaseState.purchase,
                            successMessage = context.getString(R.string.purchase_delete_success)
                        ) {
                            purchasesViewModel.hideDeletingDialog(purchaseState.purchase)
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
                PrimaryTextButton(
                    onClick = {
                        purchasesViewModel.hideDeletingDialog(purchaseState.purchase)
                    },
                    text = stringResource(R.string.cancel)
                )
            },
            onDismissRequest = {
                purchasesViewModel.hideDeletingDialog(purchaseState.purchase)
            }
        )
}