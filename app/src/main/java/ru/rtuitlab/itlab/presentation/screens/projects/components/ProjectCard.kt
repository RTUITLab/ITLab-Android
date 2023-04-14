@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.fromIso8601
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToInstant
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.common.extensions.toUiString
import ru.rtuitlab.itlab.data.remote.api.projects.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElementsRoot
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerThemes
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun ProjectCard(
    project: ProjectCompact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SharedElement(
                    key = "${project.id}/image",
                    screenKey = AppScreen.Projects
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(40.dp)
                                .height(40.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(project.logoUrl)
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

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    SharedElement(
                        key = "${project.id}/name",
                        screenKey = AppScreen.Projects
                    ) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    SharedElement(
                        key = "${project.id}/description",
                        screenKey = AppScreen.Projects
                    ) {
                        if (project.shortDescription.isNotBlank()) {
                            Text(
                                text = project.shortDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.event_no_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = LocalContentColor.current.copy(.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (project.lastVersion?.archived?.archived != true) {
                LastVersionInfo(lastVersion = project.lastVersion)
            } else {
                project.lastVersion.archived.archivedDate?.let {
                    Text(
                        text = stringResource(
                            R.string.project_version_archived_date,
                            project.lastVersion.archived.archivedDate.fromIso8601(LocalContext.current, parseWithTime = false)
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SharedElement(
                key = "${project.id}/owners",
                screenKey = AppScreen.Projects
            ) {
                if (project.owners.isEmpty()) {
                    Text(
                        text = stringResource(R.string.project_no_owners),
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(.8f)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
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
                            },
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            SuggestionChip(
                                onClick = {
                                    navController.navigate("${AppScreen.EmployeeDetails.navLink}/${project.owners[0].id}")
                                },
                                label = {
                                    Text(
                                        text = project.owners[0].abbreviatedName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                        if (project.owners.size > 1) {
                            item {
                                Divider(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(.5f)
                                )

                            }

                            items(
                                items = project.owners.takeLast(project.owners.size - 1),
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
}


@Composable
private fun LastVersionInfo(
    lastVersion: LastVersion?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f, false)
        ) {
            Text(
                text = stringResource(R.string.project_version),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.8f)
            )

            if (lastVersion?.name != null) {
                Text(
                    text = lastVersion.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = stringResource(R.string.unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(.8f)
                )
            }

        }
        Column(
            modifier = Modifier.weight(1f, false)
        ) {
            Text(
                text = stringResource(R.string.project_soft_deadline_abr),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.8f)
            )

            if (lastVersion?.deadlines?.soft != null) {
                Text(
                    text = lastVersion.deadlines.soft.fromIso8601ToInstant().date.toUiString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(.8f)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f, false)
        ) {
            Text(
                text = stringResource(R.string.project_progress),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(.8f)
            )

            if (lastVersion != null) {
                Text(
                    text = stringResource(
                        R.string.percentage,
                        (lastVersion.completeTaskCount.toFloat() / lastVersion.taskCount.coerceAtLeast(
                            1
                        ) * 100).toInt()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(.8f)
                )
            }
        }
    }
}

@Composable
fun ShimmeredProjectCard(
    modifier: Modifier = Modifier
) {
    val defaultShimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = ShimmerThemes.defaultShimmerTheme
    )

    Card(
        modifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Row {
                ShimmerBox(
                    modifier = Modifier
                        .size(40.dp),
                    shape = CircleShape
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .height(20.dp),
                        shimmer = defaultShimmer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(20.dp),
                        shimmer = defaultShimmer
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f, false)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(18.dp),
                        shimmer = defaultShimmer
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    ShimmerBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(20.dp),
                        shimmer = defaultShimmer
                    )

                }
                Column(
                    modifier = Modifier.weight(1f, false)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp),
                        shimmer = defaultShimmer
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    ShimmerBox(
                        modifier = Modifier
                            .width(100.dp)
                            .height(20.dp),
                        shimmer = defaultShimmer
                    )


                }
                Column(
                    modifier = Modifier.weight(1f, false)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(18.dp),
                        shimmer = defaultShimmer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(100.dp)
                            .height(20.dp),
                        shimmer = defaultShimmer
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            ShimmerBox(
                modifier = Modifier
                    .height(32.dp)
                    .fillMaxWidth(),
                shimmer = defaultShimmer
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Preview
@Composable
fun ShimmeredProjectCardPreview() {
    ITLabTheme {
        CompositionLocalProvider(
            LocalNavController provides rememberNavController()
        ) {
            SharedElementsRoot {
                Surface {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProjectCard(
                            project = ProjectCompact(
                                id = "asd",
                                archived = false,
                                archivedBy = null,
                                archivedDate = null,
                                createdAt = ZonedDateTime.now(ZoneId.systemDefault()),
                                lastVersion = LastVersion(
                                    archived = ArchivationInfo(
                                        archived = false,
                                        archivedBy = null,
                                        archivedDate = null
                                    ),
                                    completeTaskCount = 7,
                                    createdAt = ZonedDateTime.now(ZoneId.systemDefault()).toIsoString(),
                                    deadlines = Deadlines(
                                        hard = ZonedDateTime.now(ZoneId.systemDefault()).toIsoString(),
                                        soft = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(10).toIsoString()
                                    ),
                                    id = "asdsad",
                                    name = "v0.3",
                                    owner = null,
                                    taskCount = 10,
                                    updatedAt = null,
                                    workers = emptyList()
                                ),
                                logoUrl = "",
                                name = "Умное общежитие",
                                owners = listOf(
                                    User(
                                        id = "fuck",
                                        firstName = "Александр",
                                        lastName = "Левандровский",
                                        middleName = "Максимович"
                                    )
                                ),
                                shortDescription = "Проект для IoT академии Samsung",
                                updatedAt = null
                            ),
                            onClick = { /*TODO*/ }
                        )
                        ShimmeredProjectCard(
                            modifier = Modifier
                                .offset {
                                    IntOffset(0, -590)
                                }
                        )
                    }
                }
            }
        }
    }
}