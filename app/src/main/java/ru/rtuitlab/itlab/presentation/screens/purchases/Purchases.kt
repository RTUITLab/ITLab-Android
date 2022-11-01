package ru.rtuitlab.itlab.presentation.screens.purchases

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerThemes
import ru.rtuitlab.itlab.common.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@Composable
fun Purchases(
    viewModel: PurchasesViewModel = singletonViewModel()
) {
    val state by viewModel.state.collectAsState()

    val navController = LocalNavController.current

    val scaffoldState = rememberScaffoldState(
        snackbarHostState = SnackbarHostState()
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PurchasesViewModel.PurchaseEvent.Snackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val (transitionProgress, transitionProgressSetter) = remember { mutableStateOf(0f) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            TransitionFloatingActionButton(
                key = "Purchases/New",
                screenKey = AppScreen.Purchases.route,
                icon = Icons.Default.Add,
                onClick = {
                    navController.navigate(AppScreen.NewPurchase.route)
                },
                transitionProgressSetter = transitionProgressSetter
            )
        }
    ) {
        Box {
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

                    if ((state.paginationState?.totalElements ?: 0) == 0 && !state.isLoading && !state.isRefreshing && state.errorMessage == null) {
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
                        key = { _, it -> it.id }
                    ) { index, purchase ->

                        // Normally this is an unhandled side-effect, but in this case we have precise
                        // control over its execution through concrete conditions, so no random calls
                        // to fetchNextItems() will be performed.
                        if (index >= state.purchases.size - 1 && !state.endReached && !state.isLoading && state.errorMessage == null) {
                            viewModel.fetchNextItems()
                        }
                        SharedElement(
                            key = purchase.id,
                            screenKey = AppScreen.Reports.route,
                            transitionSpec = SharedElementsTransitionSpec(
                                durationMillis = duration
                            )
                        ) {
                            PurchaseCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                                    .clickable {
                                        viewModel.onPurchaseOpened(purchase)
                                        navController.navigate("${AppScreen.PurchaseDetails.navLink}/${purchase.id}")
                                    },
                                purchase = purchase
                            )
                        }
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
                            count = (state.paginationState?.totalElements
                                ?: 0 - state.purchases.size).coerceAtLeast(viewModel.pageSize)
                        ) {
                            ShimmeredPurchaseCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp),
                                isResolved = state.selectedStatus == PurchaseStatusUi.ACCEPT || state.selectedStatus == PurchaseStatusUi.DECLINE
                            )
                        }
                    }
                }
            }
            // Providing scrimming during fab transition
            Canvas(
                modifier = Modifier.fillMaxSize(),
                onDraw = {
                    drawRect(color = Color.Black.copy(alpha = 0.32f * (transitionProgress)))
                }
            )
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
            SharedElement(
                key = "${purchase.id}/name",
                screenKey = AppScreen.Purchases.route
            ) {
                Text(
                    text = purchase.name,
                    style = MaterialTheme.typography.h6
                )
            }

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
                            text = purchase.purchaseDate.fromIso8601(
                                context = LocalContext.current,
                                parseWithTime = false
                            ),
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

@Composable
private fun ShimmeredPurchaseCard(
    modifier: Modifier = Modifier,
    isResolved: Boolean
) {
    val defaultShimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = ShimmerThemes.defaultShimmerTheme
    )
    val accentColor by AppColors.accent.collectAsState()
    SideColoredCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.LightGray
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
            ShimmerBox(
                modifier = Modifier
                    .fillMaxSize(.5f)
                    .height(20.dp),
                shimmer = defaultShimmer
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxSize(.5f)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )

                if (isResolved)
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxSize(.5f)
                            .height(20.dp),
                        shimmer = defaultShimmer,
                        color = accentColor.copy(alpha = .4f)
                    )

                ShimmerBox(
                    modifier = Modifier
                        .fillMaxSize(.5f)
                        .height(20.dp),
                    shimmer = defaultShimmer,
                    color = accentColor.copy(alpha = .4f)
                )

                ShimmerBox(
                    modifier = Modifier
                        .fillMaxSize(.25f)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )
            }
        }
    }
}