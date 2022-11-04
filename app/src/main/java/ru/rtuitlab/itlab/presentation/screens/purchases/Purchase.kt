package ru.rtuitlab.itlab.presentation.screens.purchases

import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.presentation.screens.reports.duration
import ru.rtuitlab.itlab.presentation.ui.components.*
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.utils.SharedElementsTransitionSpec
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.common.fromIso8601
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Purchase(
    id: Int,
    purchasesViewModel: PurchasesViewModel = singletonViewModel(),
    appBarViewModel: AppBarViewModel = singletonViewModel()
) {

    val state by purchasesViewModel.state.collectAsState()
    val purchase = state.selectedPurchaseState!!.purchase
    val animationState by remember {
        mutableStateOf(MutableTransitionState(false))
    }

    val isSolvingAccessible by purchasesViewModel.isSolvingAccessible.collectAsState()

    val context = LocalContext.current

    val scaffoldState = rememberScaffoldState(
        snackbarHostState = SnackbarHostState()
    )

    purchasesViewModel.uiEvents.collectUiEvents(scaffoldState)

    LaunchedEffect(purchase) {
        animationState.targetState = true
        appBarViewModel.onNavigate(
            screen = AppScreen.PurchaseDetails(purchase?.name ?: "")
        )
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 15.dp)
                .animateContentSize()
        ) {

            SharedElement(
                key = purchase.id,
                screenKey = AppScreen.PurchaseDetails.route,
                transitionSpec = SharedElementsTransitionSpec(
                    durationMillis = duration
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colors.surface,
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {


                        SharedElement(
                            key = "${purchase.id}/time",
                            screenKey = AppScreen.PurchaseDetails.route,
                            transitionSpec = SharedElementsTransitionSpec(
                                durationMillis = duration
                            )
                        ) {
                            IconizedRow(
                                imageVector = Icons.Default.Schedule,
                                opacity = .7f,
                                spacing = 10.dp
                            ) {
                                Text(text = "${stringResource(R.string.purchase_date)}: ")
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
                            screenKey = AppScreen.PurchaseDetails.route,
                            transitionSpec = SharedElementsTransitionSpec(
                                durationMillis = duration
                            )
                        ) {
                            IconizedRow(
                                imageVector = Icons.Default.Person,
                                opacity = .7f,
                                spacing = 10.dp
                            ) {
                                Text(text = "${stringResource(R.string.purchase_buyer)}:")
                                UserLink(user = purchase.purchaser)
                            }
                        }

                        // Approver
                        purchase.solution.solver?.let {
                            SharedElement(
                                key = "${purchase.id}/approver",
                                screenKey = AppScreen.PurchaseDetails.route,
                                transitionSpec = SharedElementsTransitionSpec(
                                    durationMillis = duration
                                )
                            ) {
                                IconizedRow(
                                    imageVector = Icons.Default.ManageAccounts,
                                    opacity = .7f,
                                    spacing = 10.dp
                                ) {
                                    Text(
                                        text = "${
                                            stringResource(
                                                if (purchase.solution.status == PurchaseStatusApi.ACCEPT)
                                                    R.string.purchase_approved_by
                                                else R.string.purchase_rejected_by
                                            )
                                        }:"
                                    )
                                    UserLink(user = it)
                                }
                            }
                        }

                        // Compensation
                        SharedElement(
                            key = "${purchase.id}/price",
                            screenKey = AppScreen.PurchaseDetails.route,
                            transitionSpec = SharedElementsTransitionSpec(
                                durationMillis = duration
                            )
                        ) {
                            IconizedRow(
                                imageVector = Icons.Default.Payment,
                                opacity = .7f,
                                spacing = 10.dp
                            ) {
                                Text(text = "${stringResource(R.string.purchase_price)}: ")
                                Text(
                                    text = stringResource(R.string.salary_float, purchase.price),
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = purchase.solution.status == PurchaseStatusApi.AWAIT && isSolvingAccessible
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Chip(
                                    onClick = {
                                        purchasesViewModel.onReject(
                                            context.getString(R.string.purchase_rejected)
                                        )
                                    },
                                    colors = ChipDefaults.outlinedChipColors(
                                        backgroundColor = Color.Transparent,
                                        contentColor = MaterialTheme.colors.error
                                    ),
                                    border = BorderStroke(
                                        width = ChipDefaults.OutlinedBorderSize,
                                        color = MaterialTheme.colors.error
                                    ),
                                    leadingIcon = {
                                        if (state.selectedPurchaseState!!.isRejectingInProgress)
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(24.dp),
                                                color = MaterialTheme.colors.error,
                                                strokeWidth = 2.dp
                                            )
                                        else
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null
                                            )
                                    },
                                    enabled = state.selectedPurchaseState!!.areSolutionButtonsEnabled
                                ) {
                                    Text(
                                        text = stringResource(R.string.purchase_reject),
                                        style = MaterialTheme.typography.body1
                                    )
                                }

                                Chip(
                                    onClick = {
                                        purchasesViewModel.onApprove(
                                            context.getString(R.string.purchase_approved)
                                        )
                                    },
                                    colors = ChipDefaults.outlinedChipColors(
                                        backgroundColor = Color.Transparent,
                                        contentColor = AppColors.green
                                    ),
                                    border = BorderStroke(
                                        width = ChipDefaults.OutlinedBorderSize,
                                        color = AppColors.green
                                    ),
                                    leadingIcon = {
                                        if (state.selectedPurchaseState!!.isApprovingInProgress)
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .size(24.dp),
                                                color = AppColors.green,
                                                strokeWidth = 2.dp
                                            )
                                        else
                                            Icon(
                                                imageVector = Icons.Default.Done,
                                                contentDescription = null
                                            )
                                    },
                                    enabled = state.selectedPurchaseState!!.areSolutionButtonsEnabled
                                ) {
                                    Text(
                                        text = stringResource(R.string.purchase_approve),
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!purchase.description.isNullOrBlank()) {
                PurchaseDescription(
                    header = {
                        Text(
                            text = stringResource(R.string.description),
                            style = MaterialTheme.typography.h6
                        )
                    },
                    description = purchase.description,
                    visibleState = animationState
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (Patterns.WEB_URL.matcher(purchase.checkPhotoUrl).matches()) {
                PurchaseFile(
                    description = stringResource(R.string.purchase_check),
                    visibleState = animationState,
                    url = purchase.checkPhotoUrl,
                    icon = Icons.Default.PictureAsPdf
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (!purchase.purchasePhotoUrl.isNullOrBlank() && Patterns.WEB_URL.matcher(purchase.purchasePhotoUrl).matches()) {
                PurchaseFile(
                    description = stringResource(R.string.purchase_photo),
                    visibleState = animationState,
                    url = purchase.purchasePhotoUrl,
                    icon = Icons.Default.Image
                )
            }

        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun PurchaseDescription(
    header: @Composable () -> Unit,
    description: String,
    visibleState: MutableTransitionState<Boolean>
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInVertically(
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            initialOffsetY = { it }
        ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            border = ButtonDefaults.outlinedBorder
        ) {
            Column {
                Box(
                    Modifier
                        .background(color = MaterialTheme.colors.onSurface.copy(alpha = .1f))
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                ) {
                    header()
                }
                Divider()
                Text(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    text = description
                )
            }
        }
    }
}

@Composable
private fun PurchaseFile(
    description: String,
    url: String,
    visibleState: MutableTransitionState<Boolean>,
    icon: ImageVector
) {

    val handler = LocalUriHandler.current

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInVertically(
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            initialOffsetY = { it }
        ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            border = ButtonDefaults.outlinedBorder
        ) {
            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TextFieldDefaults.OutlinedTextFieldShape)
                    .clickable {
                        handler.openUri(url)
                    },
                value = "",
                onValueChange = {},
                enabled = false,
                label = {
                    Row {
                        Text(
                            text = description,
                            color = MaterialTheme.colors.onSurface.copy(alpha = .6f)
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                    disabledBorderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled),
                    disabledLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
