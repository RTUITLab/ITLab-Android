package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.projects.models.ProjectRepoEntity
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow

@Composable
fun ProjectReposBottomSheet(
    repos: List<ProjectRepoEntity>?
) {

    val handler = LocalUriHandler.current

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        text = stringResource(R.string.project_repos),
        style = MaterialTheme.typography.titleLarge
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 15.dp)
    ) {

        if (repos != null && repos.isNotEmpty()) {
            items(
                items = repos,
                key = { it.id }
            ) {
                IconizedRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            handler.openUri(it.url)
                        }
                        .padding(vertical = 8.dp),
                    imageVector = Icons.Default.OpenInNew,
                    imageHeight = 30.dp,
                    imageWidth = 30.dp,
                    tint = MaterialTheme.colorScheme.primary,
                    opacity = 1f
                ) {
                    Text(text = it.name)
                }
            }
        } else {
            item {
                Text(text = stringResource(R.string.project_no_repos))
            }
        }
    }
}