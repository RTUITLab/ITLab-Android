package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.projects.models.BudgetCertificationEntity
import ru.rtuitlab.itlab.data.local.projects.models.UserWorker
import ru.rtuitlab.itlab.data.local.projects.models.VersionRoleTotalEntity
import ru.rtuitlab.itlab.data.local.projects.models.VersionTaskWithWorkers
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

private const val HEADER_HEIGHT_DP = 80
private const val DUMMIES_COUNT = 4

private val BORDER_COLOR
    @Composable get() = LocalContentColor.current.copy(.5f)

private val CELL_TEXT_COLOR
    @Composable get() = BORDER_COLOR.copy(.8f)

// This code is a direct result of total lack of decent table libraries,
// and I'm not being paid enough to develop my own.
// Therefore this thing breaks both Compose guidelines and common sense...
// But works
@Composable
fun TasksTable(
    modifier: Modifier = Modifier,
    tasks: List<VersionTaskWithWorkers>?,
    certification: BudgetCertificationEntity?,
    roleTotals: List<VersionRoleTotalEntity>?,
    workers: List<UserWorker>?
) {

    var headerHeight by remember { mutableStateOf(HEADER_HEIGHT_DP.dp) }
    val density = LocalDensity.current

    val numericRowsHeights = remember(tasks) {
        tasks?.let {
            mutableStateListOf(
                *tasks.map { 27.dp }.toTypedArray()
            )
        } ?: mutableStateListOf(*Array(DUMMIES_COUNT) { 27.dp })
    }

    var lastRowHeight by remember { mutableStateOf(30.dp) }

    Row(
        modifier = modifier
    ) {

        // First column - Header, Tasks and Total
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .border(.5.dp, BORDER_COLOR)
        ) {
            Cell(
                modifier = Modifier
                    .height(headerHeight),
                border = BorderStroke(0.dp, Color.Transparent)
            ) {
                Text(
                    modifier = with(this) {
                        Modifier.align(Alignment.CenterStart)
                    },
                    text = stringResource(R.string.project_version_task),
                    fontSize = 16.sp
                )
            }

            TasksColumn(
                tasks = tasks,
                onGloballyPositioned = { index, height -> numericRowsHeights[index] = height }
            )

            Cell(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        lastRowHeight = with(density) {
                            it.size.height.toDp()
                        }
                    }
            ) {
                Text(
                    text = stringResource(R.string.project_version_total),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }


        // Workers: Headers, Hours, Costs and Total
        WorkersColumns(
            workers = workers,
            tasks = tasks,
            numericRowsHeights = numericRowsHeights,
            lastRowHeight = lastRowHeight,
            roleTotals = roleTotals,
            onGloballyPositioned = { headerHeight = it }
        )

        // Last column - task totals
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Cell(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(R.string.project_version_total)
                )
            }

            tasks?.forEachIndexed { index, task ->
                Cell(
                    modifier = Modifier
                        .height(numericRowsHeights[index])
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = task.task.cost.toString()
                    )
                }
            }

            // Full version total cost
            Cell(
                modifier = Modifier
                    .height(lastRowHeight)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = certification?.totalCost?.toString() ?: tasks?.sumOf { it.task.cost }?.toString() ?: "0",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun Cell(
    modifier: Modifier = Modifier,
    cellPadding: PaddingValues = PaddingValues(4.dp),
    border: BorderStroke = BorderStroke(0.dp, LocalContentColor.current.copy(.8f)),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .border(border)
            .padding(cellPadding),
        content = content
    )
}

@Composable
private fun TasksColumn(
    tasks: List<VersionTaskWithWorkers>?,
    onGloballyPositioned: (index: Int, height: Dp) -> Unit
) {
    val density = LocalDensity.current
    if (tasks != null) {
        tasks.forEachIndexed { index, it ->
            Cell(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        onGloballyPositioned(index, with(density) { it.size.height.toDp() })
                    },
            ) {
                Text(
                    text = it.task.name,
                    fontSize = 14.sp,
                    color = if (it.task.isCompleted) AppColors.green else CELL_TEXT_COLOR
                )
            }
        }
    } else {
        repeat(DUMMIES_COUNT) { index ->
            Cell(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        onGloballyPositioned(index, with(density) { it.size.height.toDp() })
                    },
            ) {
                ShimmeredCellContent(
                    modifier = Modifier
                        .height(20.dp)
                        .width(100.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkersColumns(
    workers: List<UserWorker>?,
    tasks: List<VersionTaskWithWorkers>?,
    numericRowsHeights: SnapshotStateList<Dp>,
    lastRowHeight: Dp,
    roleTotals: List<VersionRoleTotalEntity>?,
    onGloballyPositioned: (height: Dp) -> Unit
) {
    val density = LocalDensity.current

    val WorkerHeader: @Composable (
        roleAndNameContent: @Composable ColumnScope.() -> Unit
    ) -> Unit = {
        Column(
            modifier = Modifier
                .onGloballyPositioned {
                    onGloballyPositioned(
                        with(density) {
                            it.size.height.toDp()
                        }
                    )
                }
        ) {
            Cell(
                cellPadding = PaddingValues(4.dp)
            ) {
                Column {
                    it()
                }
            }

            Row {
                Cell(
                    cellPadding = PaddingValues(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.hours),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Cell(
                    cellPadding = PaddingValues(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.cost),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (workers != null) {
        workers.forEach { worker ->
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            ) {

                WorkerHeader {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = worker.worker.role,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = worker.user.toUser().abbreviatedName,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (tasks != null) {
                    tasks.forEachIndexed { index, task ->
                        Cell(
                            cellPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.height(numericRowsHeights[index]),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (worker.worker.roleId in task.workers.map { it.roleId }) {
                                    val thisWorker =
                                        task.workers.find { it.roleId == worker.worker.roleId }!!
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp),
                                        text = thisWorker.hours.toString(),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = CELL_TEXT_COLOR
                                    )
                                    Divider(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp),
                                        color = BORDER_COLOR
                                    )
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp),
                                        text = thisWorker.cost.toString(),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = CELL_TEXT_COLOR
                                    )
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp),
                                        text = "-",
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = CELL_TEXT_COLOR.copy(.5f)
                                    )
                                }

                            }
                        }
                    }

                    // Role total
                    Cell(
                        cellPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.height(lastRowHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val thisTotal = roleTotals?.find { it.roleId == worker.worker.roleId }
                                ?: run {
                                    val thisWorkerTasks = tasks.flatMap { it.workers.filter { it.roleId == worker.worker.roleId } }
                                    VersionRoleTotalEntity(
                                        versionId = worker.worker.versionId,
                                        roleId = worker.worker.roleId,
                                        totalCost = thisWorkerTasks
                                            .sumOf { it.cost },
                                        totalHours = thisWorkerTasks.sumOf { it.hours }
                                    )
                                }
                            if (thisTotal.totalCost != 0 && thisTotal.totalHours != 0) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    text = thisTotal.totalHours.toString(),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = BORDER_COLOR
                                )
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    text = thisTotal.totalCost.toString(),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    text = "-",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary.copy(.5f)
                                )
                            }

                        }
                    }
                } else {
                    repeat(DUMMIES_COUNT) { index ->
                        Cell(
                            cellPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.height(numericRowsHeights[index]),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShimmeredCellContent(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(20.dp)
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = BORDER_COLOR
                                )
                                ShimmeredCellContent(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(20.dp)
                                )
                            }
                        }
                    }
                }


            }
        }
    } else {
        repeat(DUMMIES_COUNT) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            ) {

                WorkerHeader {
                    ShimmeredCellContent(
                        modifier = Modifier
                            .height(20.dp)
                            .width(100.dp)
                    )
                    ShimmeredCellContent(
                        modifier = Modifier
                            .height(15.dp)
                            .width(80.dp)
                    )
                }

                if (tasks == null) {
                    repeat(DUMMIES_COUNT) { index ->
                        Cell(
                            cellPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.height(numericRowsHeights[index]),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShimmeredCellContent(
                                    modifier = Modifier
                                        .height(15.dp)
                                        .weight(1f)
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = BORDER_COLOR
                                )
                                ShimmeredCellContent(
                                    modifier = Modifier
                                        .height(15.dp)
                                        .weight(1f)
                                )
                            }
                        }
                    }

                    // Role total
                    Cell(
                        cellPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.height(lastRowHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmeredCellContent(
                                modifier = Modifier
                                    .height(15.dp)
                                    .weight(1f)
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp),
                                color = BORDER_COLOR
                            )
                            ShimmeredCellContent(
                                modifier = Modifier
                                    .height(15.dp)
                                    .weight(1f)
                            )
                        }
                    }
                }


            }
        }
    }
}

@Composable
private fun ShimmeredCellContent(
    modifier: Modifier = Modifier
) {
    ShimmerBox(
        modifier = Modifier
            .padding(4.dp)
            .then(modifier)
    )
}

@Preview(
    widthDp = 400
)
@Composable
fun TasksTablePreview() {
    ITLabTheme {
        Surface {
            Column {
                TasksTable(
                    tasks = null,
                    certification = null,
                    workers = null,
                    roleTotals = null
                )

                /*TasksTable(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    tasks = listOf(
                        VersionTask(
                            id = "0",
                            baseCost = .0,
                            isCompleted = false,
                            creationTime = ZonedDateTime.now(),
                            name = "Кэширование",
                            cost = 123,
                            updateTime = ZonedDateTime.now(),
                            versionId = "ASD"
                        ),
                        VersionTask(
                            id = "0",
                            baseCost = .0,
                            isCompleted = false,
                            creationTime = ZonedDateTime.now(),
                            name = "Тестирование",
                            cost = 123,
                            updateTime = ZonedDateTime.now(),
                            versionId = "ASD"
                        ),
                        VersionTask(
                            id = "0",
                            baseCost = .0,
                            isCompleted = false,
                            creationTime = ZonedDateTime.now(),
                            name = "Рефакторинг кода",
                            cost = 123,
                            updateTime = ZonedDateTime.now(),
                            versionId = "ASD"
                        ),

                    ),
                    certification = null
                )*/
            }
        }
    }
}