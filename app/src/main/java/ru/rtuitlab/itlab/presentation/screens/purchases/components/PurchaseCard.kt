@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.purchases.components

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseSolution
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchaseUiState
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.chips.InfoChip
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerThemes
import ru.rtuitlab.itlab.presentation.ui.theme.Green27
import ru.rtuitlab.itlab.presentation.ui.theme.Green92
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import kotlin.math.roundToInt

@Composable
fun PurchaseCard(
    modifier: Modifier = Modifier,
    isSolvingAccessible: Boolean,
    onReject: () -> Unit,
    onApprove: () -> Unit,
    onDelete: () -> Unit,
    state: PurchaseUiState
) {

    val navController = LocalNavController.current
    val purchase = state.purchase

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline(),
                    text = purchase.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                when (purchase.solution.status) {
                    PurchaseStatusApi.ACCEPT -> {
                        InfoChip(
                            label = {
                                Text(
                                    text = stringResource(
                                        R.string.salary_int,
                                        purchase.price.roundToInt()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            labelColor = Green27,
                            containerColor = Green92
                        )
                    }
                    else -> {
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = stringResource(R.string.salary_int, purchase.price.roundToInt()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (purchase.description.isNullOrBlank()) stringResource(R.string.event_no_description)
                else purchase.description,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(
                    alpha = if (purchase.description.isNullOrBlank()) .5f
                    else 1f
                )
            )
        }

        purchase.purchasePhotoUrl?.let {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.purchase_photo),
                contentScale = ContentScale.FillWidth,
                loading = {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        shape = RectangleShape
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        Column(
            modifier = Modifier
                .padding(
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                when (purchase.solution.status) {
                    PurchaseStatusApi.AWAIT -> {
                        Text(
                            text = purchase.purchaseDate.fromIso8601(
                                context = LocalContext.current,
                                parseWithTime = false
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(.8f)
                        )

                        IconizedRow(
                            imageVector = Icons.Default.Schedule,
                            spacing = 8.dp
                        ) {
                            Text(
                                text = stringResource(R.string.status_await),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    PurchaseStatusApi.ACCEPT -> {
                        purchase.solution.solver?.let { user ->
                            SuggestionChip(
                                onClick = {
                                    navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
                                },
                                label = {
                                    Text(
                                        text = user.abbreviatedName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                border = SuggestionChipDefaults.suggestionChipBorder(Green27)
                            )
                        }

                        Text(
                            text = stringResource(R.string.purchase_status_confirmed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Green27
                        )
                    }
                    else -> {
                        purchase.solution.solver?.let { user ->
                            SuggestionChip(
                                onClick = {
                                    navController.navigate("${AppScreen.EmployeeDetails.navLink}/${user.id}")
                                },
                                label = {
                                    Text(
                                        text = user.abbreviatedName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                border = SuggestionChipDefaults.suggestionChipBorder(MaterialTheme.colorScheme.error)
                            )
                        }

                        Text(
                            text = stringResource(R.string.purchase_status_rejected),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
            ) {
                Row {
                    SuggestionChip(
                        onClick = {
                            navController.navigate("${AppScreen.EmployeeDetails.navLink}/${purchase.purchaser.id}")
                        },
                        label = {
                            Text(
                                text = purchase.purchaser.abbreviatedName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    if (Patterns.WEB_URL.matcher(purchase.checkPhotoUrl).matches()) {
                        val handler = LocalUriHandler.current
                        IconButton(onClick = { handler.openUri(purchase.checkPhotoUrl) }) {
                            Icon(
                                imageVector = Icons.Outlined.Receipt,
                                contentDescription = stringResource(R.string.purchase_check),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (purchase.solution.status == PurchaseStatusApi.AWAIT) {
                        IconButton(
                            onClick = onDelete,
                            enabled = state.areSolutionButtonsEnabled
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.purchase_check),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (isSolvingAccessible && purchase.solution.status == PurchaseStatusApi.AWAIT) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onReject,
                            enabled = state.areSolutionButtonsEnabled,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            if (state.isRejectingInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp),
                                    color = LocalContentColor.current,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.purchase_check)
                                )
                            }
                        }

                        IconButton(
                            onClick = onApprove,
                            enabled = state.areSolutionButtonsEnabled,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Green27
                            )
                        ) {
                            if (state.isApprovingInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp),
                                    color = LocalContentColor.current,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = stringResource(R.string.purchase_check)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmeredPurchaseCard(
    modifier: Modifier = Modifier
) {

    val defaultShimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = ShimmerThemes.defaultShimmerTheme
    )

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )

                Spacer(modifier = Modifier.width(16.dp))

                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shimmer = defaultShimmer
            )
        }

        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            shape = RectangleShape
        )

        Spacer(modifier = Modifier.height(14.dp))


        Column(
            modifier = Modifier
                .padding(
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(.2f)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )

                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(.2f)
                        .height(20.dp),
                    shimmer = defaultShimmer
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
            ) {
                Row {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shimmer = defaultShimmer
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    ShimmerBox(
                        modifier = Modifier
                            .weight(3f)
                            .height(32.dp),
                        shimmer = defaultShimmer
                    )
                }
            }
        }
    }
}

@Preview(
    widthDp = 700,
    heightDp = 2000
)
@Composable
fun PurchaseCardPreview() {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController()
    ) {
        ITLabTheme {
            Surface {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var width by remember { mutableStateOf(300f.dp) }
                    Slider(
                        value = width.value,
                        valueRange = 250f..600f,
                        onValueChange = {
                            width = it.dp
                        }
                    )

                    Text("${width.value.roundToInt()} DP")

                    PurchaseCard(
                        modifier = Modifier.width(width),
                        isSolvingAccessible = true,
                        onReject = { },
                        onApprove = { },
                        onDelete = { },
                        state = PurchaseUiState(
                            Purchase(
                                id = 0,
                                purchaser = User(
                                    id = "1",
                                    firstName = "Александр",
                                    middleName = "Максимович",
                                    lastName = "Левандровский"
                                ),
                                name = "Raspberry PI 4 8GB",
                                price = 4600f,
                                description = "Описание товара. Где купил сколько стоит по чём и так далее прочая информация о товаре полное описание, раскрытое.\n" +
                                        "На полную.",
                                purchaseDate = "2022-04-19T17:48:40Z",
                                additionDate = "2022-04-19T17:48:40Z",
                                checkPhotoUrl = "https://dev.manage.rtuitlab.dev/api//mfs/download/62852d1f2b52a6de24adfa7a",
                                purchasePhotoUrl = "https://dev.manage.rtuitlab.dev/api//mfs/download/62852d1f2b52a6de24adfa7a",
                                solution = PurchaseSolution(
                                    status = PurchaseStatusApi.AWAIT,
                                    decisionComment = null,
                                    date = null,
                                    solver = null
                                )
                            )
                        )
                    )

                    PurchaseCard(
                        modifier = Modifier.width(width),
                        isSolvingAccessible = true,
                        onReject = { },
                        onApprove = { },
                        onDelete = { },
                        state = PurchaseUiState(
                            Purchase(
                                id = 0,
                                purchaser = User(
                                    id = "1",
                                    firstName = "Александр",
                                    middleName = "Максимович",
                                    lastName = "Левандровский"
                                ),
                                name = "Raspberry PI 4 8GB Giga Ultra Chad Edition. For chads, not virgins.",
                                price = 4600f,
                                description = "Описание товара. Где купил сколько стоит по чём и так далее прочая информация о товаре полное описание, раскрытое.\n" +
                                        "На полную.",
                                purchaseDate = "2022-04-19T17:48:40Z",
                                additionDate = "2022-04-19T17:48:40Z",
                                checkPhotoUrl = "https://dev.manage.rtuitlab.dev/api//mfs/download/62852d1f2b52a6de24adfa7a",
                                purchasePhotoUrl = "https://dev.manage.rtuitlab.dev/api//mfs/download/62852d1f2b52a6de24adfa7a",
                                solution = PurchaseSolution(
                                    status = PurchaseStatusApi.AWAIT,
                                    decisionComment = null,
                                    date = null,
                                    solver = null
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}