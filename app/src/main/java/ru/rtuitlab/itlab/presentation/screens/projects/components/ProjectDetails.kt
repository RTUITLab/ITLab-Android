package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.presentation.screens.projects.ProjectViewModel
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.SharedElement
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun ProjectDetails(
    projectViewModel: ProjectViewModel
) {
    val project by projectViewModel.project.collectAsState()
    val versions by projectViewModel.versions.collectAsState()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        project.handle(
            onSuccess = {
                SharedElement(
                    key = it.id,
                    screenKey = AppScreen.ProjectDetails
                ) {
                    ProjectHeader(project = it)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        versions.handle(
            onSuccess = {
                Text(text = it.versions.joinToString())
            }
        )
    }
}

@Composable
private fun ProjectHeader(
    project: ProjectDetailsDto
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            Box {
                SharedElement(
                    key = "${project.id}/image",
                    screenKey = AppScreen.ProjectDetails
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(56.dp)
                                .height(56.dp),
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
            }
            Spacer(modifier = Modifier.height(8.dp))

            SharedElement(
                key = "${project.id}/description",
                screenKey = AppScreen.ProjectDetails
            ) {
                Box(
                    Modifier.padding(PaddingValues(horizontal = 16.dp))
                ) {
                    if (project.shortDescription.isNotBlank()) {
                        MarkdownTextArea(
                            textMd = project.shortDescription,
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
            }
        }
    }
}