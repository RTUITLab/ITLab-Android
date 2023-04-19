@file:OptIn(ExperimentalFoundationApi::class)

package ru.rtuitlab.itlab.presentation.screens.projects.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.projects.models.MilestoneEntity
import ru.rtuitlab.itlab.data.local.projects.models.VersionFileEntity
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

@Composable
fun VersionResourcesBottomSheet(
    functionalTasks: List<VersionFileEntity>,
    files: List<VersionFileEntity>,
    links: List<MilestoneEntity>
) {

    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 15.dp)
    ) {

        // Functional tasks
        stickyHeader {
            Header(
                icon = Icons.Default.FactCheck,
                text = stringResource(R.string.version_functional_task)
            )
        }
        if (functionalTasks.isNotEmpty()) {
            items(
                items = functionalTasks,
                key = { it.id }
            ) {
                Item(
                    onClick = {
                        uriHandler.openUri(it.fileUrl)
                    },
                    name = it.name,
                    icon = Icons.Default.Download
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.version_no_functional_tasks),
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }

        // Links
        stickyHeader {
            Divider()
            Spacer(modifier = Modifier.height(4.dp))
            Header(
                icon = Icons.Default.Link,
                text = stringResource(R.string.version_links)
            )
        }
        if (links.isNotEmpty()) {
            items(
                items = links,
                key = { it.id }
            ) {
                Item(
                    onClick = {
                        uriHandler.openUri(it.url)
                    },
                    name = it.name,
                    icon = Icons.Default.OpenInNew
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.version_no_links),
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }

        // Files
        stickyHeader {
            Divider()
            Spacer(modifier = Modifier.height(4.dp))
            Header(
                icon = Icons.Default.AttachFile,
                text = stringResource(R.string.version_files)
            )
        }
        if (files.isNotEmpty()) {
            items(
                items = files,
                key = { it.id }
            ) {
                Item(
                    onClick = {
                        uriHandler.openUri(it.fileUrl)
                    },
                    name = it.name,
                    icon = Icons.Default.Download
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.version_no_files),
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }
    }
}

@Composable
private fun Header(
    icon: ImageVector,
    text: String
) {

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        TextLineIcon(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            icon = icon,
            iconTint = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            iconRightPadding = 4.dp
        )
        /*IconizedRow(
            imageVector = icon,
            imageHeight = 30.dp,
            imageWidth = 30.dp,
            tint = MaterialTheme.colorScheme.primary,
            opacity = 1f
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }*/
    }
}

@Composable
private fun Item(
    onClick: () -> Unit,
    name: String,
    icon: ImageVector
) {
    TextLineIcon(
        text = name,
        icon = icon,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 32.dp
            )
            .fillMaxWidth(),
        iconRightPadding = 4.dp,
        color = LocalContentColor.current.copy(.8f)
    )
}

@Preview
@Composable
fun HeaderPreview() {
    ITLabTheme {
        Surface {
            Column {
                val (headerText, setHeaderText) = remember { mutableStateOf("Functional task words words words words words words words") }
                Header(
                    icon = Icons.Default.FactCheck,
                    text = headerText
                )

                Item(
                    onClick = {},
                    name = "ФЗ.docx",
                    icon = Icons.Default.Download
                )

                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    scope.launch {
                        repeat(100) {
                            delay(100)
                            setHeaderText(headerText.dropLast(1))
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun TextLineIcon(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    iconRightPadding: Dp = 0.dp,
    iconLine: Int = 0,
    iconTint: Color = LocalContentColor.current
    // etc
) {
    val painter = rememberVectorPainter(image = icon)
    var lineTop = 0f
    var lineBottom = 0f
    var lineLeft = 0f
    with(LocalDensity.current) {
        val imageSize = Size(icon.defaultWidth.toPx(), icon.defaultHeight.toPx())
        val rightPadding = iconRightPadding.toPx()
        Text(
            text = text,
            color = color,
            style = style,
            fontSize = fontSize,
            fontWeight = fontWeight,
            onTextLayout = { layoutResult ->
                if (layoutResult.lineCount > iconLine) {
                    lineTop = layoutResult.getLineTop(iconLine)
                    lineBottom = layoutResult.getLineBottom(iconLine)
                    lineLeft = layoutResult.getLineLeft(iconLine)
                }
            },
            modifier = modifier
                .padding(start = icon.defaultWidth + iconRightPadding)
                .drawBehind {
                    with(painter) {
                        translate(
                            left = lineLeft - imageSize.width - rightPadding.also {
                                Log.v("DrawScope", "Left: $it")
                            },
                            top = lineTop + (lineBottom - lineTop) / 2 - imageSize.height / 2.also {
                                Log.v("DrawScope", "Top: $it")
                            },
                        ) {
                            draw(painter.intrinsicSize, colorFilter = ColorFilter.tint(iconTint))
                        }
                    }
                }
        )
    }
}