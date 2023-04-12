@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.projects.models.Version
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.projects.components.TasksTable
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectScreenState
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.components.text_fields.OutlinedAppTextField
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProjectDetails(
    projectViewModel: ProjectViewModel,
) {
    val state by projectViewModel.uiState.collectAsState()

    val animationState by remember {
        mutableStateOf(MutableTransitionState(false))
    }
    LaunchedEffect(state) {
        animationState.targetState = state.selectedVersion != null
    }

    val snackbarHostState = remember { SnackbarHostState() }
    projectViewModel.uiEvents.collectUiEvents(snackbarHostState)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        state.projectInfo?.let { projectInfo ->
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SharedElement(
                    key = projectInfo.project.id,
                    screenKey = AppScreen.ProjectDetails
                ) {
                    ProjectHeader(state = state)
                }


                AnimatedVisibility(
                    visibleState = animationState,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it }
                    )
                ) {
                    Column {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            VersionSelector(
                                versions = state.projectInfo?.versions,
                                selectedVersion = state.selectedVersion?.version,
                                onVersionSelected = projectViewModel::onVersionSelected
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Versions(state = state)
                    }
                }
            }
        }
    }

}

@Composable
private fun ProjectHeader(
    state: ProjectScreenState
) {
    state.projectInfo ?: return

    val navController = LocalNavController.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .zIndex(2f)
                ) {
                    SharedElement(
                        key = "${state.projectInfo.project.id}/image",
                        screenKey = AppScreen.ProjectDetails
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
                        ) {
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .width(56.dp)
                                    .height(56.dp),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(state.projectInfo.project.logoUrl)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                error = {
                                    Image(
                                        modifier = Modifier.requiredSize(24.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_itlab),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                                    )
                                }
                            )
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .zIndex(1f)
                ) {
                    SharedElement(
                        key = "${state.projectInfo.project.id}/owners",
                        screenKey = AppScreen.ProjectDetails
                    ) {
                        if (state.projectInfo.owners.isEmpty()) {
                            Text(
                                text = stringResource(R.string.project_no_owners),
                                style = MaterialTheme.typography.bodySmall,
                                color = LocalContentColor.current.copy(.8f)
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .layout { measurable, constraints ->
                                        val width =
                                            constraints.maxWidth + 32.dp.roundToPx() // Adding horizontal padding
                                        val placeable = measurable.measure(
                                            constraints.copy(
                                                maxWidth = width,
                                                minWidth = width
                                            )
                                        )
                                        layout(placeable.width, placeable.height) {
                                            placeable.place(0, 0)
                                        }
                                    },
                                contentPadding = PaddingValues(start = 28.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item {
                                    SuggestionChip(
                                        onClick = {
                                            navController.navigate("${AppScreen.EmployeeDetails.navLink}/${state.projectInfo.owners[0].userEntity.id}")
                                        },
                                        label = {
                                            Text(
                                                text = state.projectInfo.owners[0].toUser().abbreviatedName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    )
                                }
                                if (state.projectInfo.owners.size > 1) {
                                    item {
                                        Divider(
                                            modifier = Modifier
                                                .height(32.dp)
                                                .width(1.dp),
                                            color = MaterialTheme.colorScheme.onSurface.copy(.5f)
                                        )

                                    }

                                    items(
                                        items = state.projectInfo.owners.takeLast(state.projectInfo.owners.size - 1)
                                            .map { it.toUser() },
                                        key = { it.id }
                                    ) {
                                        SuggestionChip(
                                            onClick = {
                                                navController.navigate("${AppScreen.EmployeeDetails.navLink}/${it.id}")
                                            },
                                            label = {
                                                Text(
                                                    text = it.abbreviatedName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            SharedElement(
                key = "${state.projectInfo.project.id}/description",
                screenKey = AppScreen.ProjectDetails
            ) {
                if (state.projectInfo.project.shortDescription.isNotBlank()) {
                    MarkdownTextArea(
                        modifier = Modifier
                            .wrapContentWidth(),
                        textMd = if (!state.projectInfo.project.description.isNullOrBlank()) state.projectInfo.project.description else state.projectInfo.project.shortDescription,
                        noDescriptionTextAlignment = Alignment.CenterStart,
                        paddingValues = PaddingValues(0.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.event_no_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.project_in_dev_since,
                    state.projectInfo.project.creationDateTime.format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss").withZone(
                            ZoneId.systemDefault()
                        )
                    )
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.7f)
            )
        }
    }
}


@Composable
fun VersionSelector(
    versions: List<Version>?,
    selectedVersion: Version?,
    onVersionSelected: (Version) -> Unit
) {
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)


    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = isExpanded,
        onExpandedChange = {
            setExpanded(!isExpanded)
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = selectedVersion?.name ?: " ",
            onValueChange = {},
            leadingIcon = if (versions == null) {{
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.25f)
                )
            }} else null,
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .rotate(rotation),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            singleLine = true,
            readOnly = true,
            label = {
                Text(text = stringResource(R.string.project_version_select))
            },
            onClick = { setExpanded(true) }
        )

        ExposedDropdownMenu(
            modifier = Modifier,
            expanded = isExpanded,
            onDismissRequest = { setExpanded(false) }
        ) {
            val spanStyle = LocalTextStyle.current.toSpanStyle()
            val color = LocalContentColor.current
            versions.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(spanStyle) {
                                    append(it.name)
                                }
                                if (it.isArchived) {
                                    withStyle(spanStyle.copy(color.copy(.6f))) {
                                        append(" (")
                                        append(stringResource(R.string.version_archived))
                                        append(')')
                                    }
                                }
                            }
                        )
                    },
                    onClick = {
                        onVersionSelected(it)
                        setExpanded(false)
                    }
                )
            }
        }
    }
}

@Composable
fun Versions(
    state: ProjectScreenState
) {
    val navController = LocalNavController.current
    state.selectedVersion ?: return
    Card(
        modifier = Modifier.padding(
            top = 0.dp,
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    text = state.selectedVersion.version.name
                )

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null
                    )
                }

                state.selectedVersion.owner?.let {
                    SuggestionChip(
                        onClick = {
                            navController.navigate("${AppScreen.EmployeeDetails.navLink}/${it.userEntity.id}")
                        },
                        label = {
                            Text(
                                text = it.toUser().abbreviatedName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.project_soft_deadline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(.8f)
                    )

                    Text(
                        text = state.selectedVersion.version.softDeadline.format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.project_hard_deadline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(.8f)
                    )

                    Text(
                        text = state.selectedVersion.version.hardDeadline.format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MarkdownTextArea(textMd = state.selectedVersion.version.description ?: "")

            if (state.selectedVersion.tasks?.isEmpty() != true) {
                TasksTable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(
                                    maxWidth = constraints.maxWidth + 32.dp.roundToPx(), // Adding horizontal padding
                                    minWidth = constraints.maxWidth + 32.dp.roundToPx()
                                )
                            )
                            layout(placeable.width, placeable.height) {
                                placeable.place(0, 0)
                            }
                        }
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    tasks = state.selectedVersion.tasks,
                    certification = state.selectedVersion.budgetWithIssuer?.budget,
                    workers = state.selectedVersion.workers,
                    roleTotals = state.selectedVersion.roleTotals
                )
            } else {
                Text(
                    text = stringResource(R.string.project_version_tasks_no_data),
                    color = LocalContentColor.current.copy(.6f)
                )
            }


        }
    }
}