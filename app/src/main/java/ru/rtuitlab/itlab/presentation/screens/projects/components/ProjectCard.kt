@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToInstant
import ru.rtuitlab.itlab.common.extensions.toUiString
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectCompactDto
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerThemes
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun ProjectCard(
    project: ProjectCompactDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier,
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
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium
                    )
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

                    if (project.lastVersion?.name != null) {
                        Text(
                            text = project.lastVersion.name,
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
                        text = stringResource(R.string.project_soft_deadline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(.8f)
                    )

                    if (project.lastVersion?.deadlines?.soft != null) {
                        Text(
                            text = project.lastVersion.deadlines.soft.fromIso8601ToInstant().date.toUiString(),
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

                    if (project.lastVersion != null) {
                        Text(
                            text = stringResource(
                                R.string.percentage,
                                (project.lastVersion.completeTaskCount.toFloat() / project.lastVersion.taskCount.coerceAtLeast(
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

    Card(modifier) {
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
        }
    }
}

@Preview
@Composable
fun ShimmeredProjectCardPreview() {
    ITLabTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(10) {
                    ShimmeredProjectCard()
                }
            }
        }
    }
}