package ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.OptionsRow

/**
 * This component is designed to suit ITLab-Android's needs regarding bottom app bar.
 * @param mainFloatingActionButton navigation-related FAB, defined in [ITLabApp][ru.rtuitlab.itlab.presentation.ui.ITLabApp].
 * Must be present in all bottom app bars.
 * @param secondaryFloatingActionButton optional FAB that performs specific action on selected screen
 * @param options list of current screen's options **excluding search**.
 * Search action is handled separately - see [searchBar]
 * @param searchBar search bar composable that is handled by the bottom bar
 */
@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    mainFloatingActionButton: @Composable (() -> Unit),
    secondaryFloatingActionButton: @Composable (() -> Unit)? = null,
    options: List<AppBarOption> = emptyList(),
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    searchBar: @Composable ((onDismissRequest: () -> Unit) -> Unit)? = null
) {

    var isSearchActivated by rememberSaveable { mutableStateOf(false) }

    if (isSearchActivated)
        BackHandler {
            isSearchActivated = false
        }


    androidx.compose.material3.BottomAppBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        windowInsets = windowInsets,
        contentPadding = contentPadding
    ) {
        Spacer(modifier = Modifier.width(FABHorizontalPadding))

        searchBar?.let {
            AnimatedVisibility(
                modifier = Modifier.padding(end = FABHorizontalPadding),
                visible = isSearchActivated,
            ) {
                it {
                    isSearchActivated = false
                }
            }
        }

        OptionsRow(
            options = if (searchBar != null)
                listOf(AppBarOption.Clickable(
                    icon = Icons.Default.Search,
                    onClick = { isSearchActivated = true }
                )) + options
            else options
        )
        Spacer(Modifier.weight(1f, true))
        secondaryFloatingActionButton?.let {
            Box(
                Modifier
                    .fillMaxHeight()
                    .padding(
                        top = FABVerticalPadding,
                        end = FABHorizontalPadding
                    ),
                contentAlignment = Alignment.TopStart
            ) {
                it()
            }
        }
        Box(
            Modifier
                .fillMaxHeight()
                .padding(
                    top = FABVerticalPadding,
                    end = FABHorizontalPadding
                ),
            contentAlignment = Alignment.TopStart
        ) {
            mainFloatingActionButton()
        }
    }
}

// Padding minus IconButton's min touch target expansion
private val BottomAppBarHorizontalPadding = 16.dp - 12.dp
internal val BottomAppBarVerticalPadding = 16.dp - 12.dp

// Padding minus content padding
private val FABHorizontalPadding = 16.dp - BottomAppBarHorizontalPadding
private val FABVerticalPadding = 12.dp - BottomAppBarVerticalPadding