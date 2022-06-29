package ru.rtuitlab.itlab.presentation.screens.purchases

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.SideColoredCard
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.extensions.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@Composable
fun Purchases(
    viewModel: PurchasesViewModel = singletonViewModel()
) {
    val state by viewModel.state.collectAsState()
    rememberLazyListState()
    SwipeRefresh(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberSwipeRefreshState(state.isRefreshing),
        onRefresh = viewModel::onRefresh
    ) {
        /*Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 15.dp)
        ) {
        }*/
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
                        Chip(
                            onClick = {
                                if (state.selectedStatus != it)
                                    viewModel.onStatusChange(it)
                            },
                            colors = ChipDefaults.outlinedChipColors(
                                backgroundColor = if (state.selectedStatus == it) it.color else Color.Transparent,
                                contentColor = (if (state.selectedStatus == it) Color.White else it.color)
                                    .copy(alpha = ChipDefaults.ContentOpacity)
                            ),
                            border = ChipDefaults.outlinedBorder
                        ) {
                            Text(
                                text = stringResource(it.nameResource),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))

                }
            }
            itemsIndexed(
                items = state.purchases,
                key = {_, it -> it.id}
            ) { index, purchase ->

                // Normally this is an unhandled side-effect, but in this case we have precise
                // control over its execution through concrete conditions, so no random calls
                // to fetchNextItems() will be performed.
                if (index >= state.purchases.size - 1 && !state.endReached && !state.isLoading) {
                    viewModel.fetchNextItems()
                }

                PurchaseCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    purchase = purchase
                )
            }
            if (state.isLoading)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.accent.collectAsState().value,
                            strokeWidth = ProgressIndicatorDefaults.StrokeWidth
                        )
                    }
                }
        }
    }
}

@Composable
fun PurchaseCard(
    modifier: Modifier = Modifier,
    purchase: Purchase
) {
    SideColoredCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = when (purchase.solution.status) {
            PurchaseStatusApi.AWAIT -> AppColors.orange
            PurchaseStatusApi.ACCEPT -> AppColors.green
            PurchaseStatusApi.DECLINE -> AppColors.red
            else -> MaterialTheme.colors.surface
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 10.dp,
                    bottom = 8.dp,
                    start = 15.dp,
                    end = 15.dp
                )
                .fillMaxWidth()
        ) {
            Text(
                text = purchase.name,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SharedElement(
                    key = "${purchase.id}/time",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    IconizedRow(
                        imageVector = Icons.Default.Schedule,
                        imageWidth = 18.dp,
                        imageHeight = 18.dp,
                        spacing = 8.dp
                    ) {
                        Text(
                            text = purchase.purchaseDate.fromIso8601(LocalContext.current),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }

                // Purchaser
                SharedElement(
                    key = "${purchase.id}/purchaser",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    IconizedRow(
                        imageVector = Icons.Default.Person,
                        spacing = 0.dp
                    ) {
                        UserLink(user = purchase.purchaser)
                    }
                }

                // Approver
                purchase.solution.solver?.let {
                    SharedElement(
                        key = "${purchase.id}/approver",
                        screenKey = AppScreen.Reports.route,
                        transitionSpec = SharedElementsTransitionSpec(
                            durationMillis = duration
                        )
                    ) {
                        IconizedRow(
                            imageVector = Icons.Default.ManageAccounts,
                            spacing = 0.dp
                        ) {
                            UserLink(user = it)
                        }
                    }
                }

                // Compensation
                SharedElement(
                    key = "${purchase.id}/price",
                    screenKey = AppScreen.Reports.route,
                    transitionSpec = SharedElementsTransitionSpec(
                        durationMillis = duration
                    )
                ) {
                    IconizedRow(
                        imageVector = Icons.Default.Payment
                    ) {
                        Text(
                            text = stringResource(R.string.salary_float, purchase.price),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }
        }
    }
}