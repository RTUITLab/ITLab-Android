@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

/**
 * Number of columns in [NavigationControl]
 */
private const val COLUMNS_COUNT = 2

/**
 * Width of a single navigation item in [dp][androidx.compose.ui.unit.Dp]
 */
private const val ITEM_WIDTH = 89

/**
 * Horizontal and vertical spacing of navigation items in [dp][androidx.compose.ui.unit.Dp]
 */
private const val ITEM_SPACING = 5

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun NavigationControl(
    isVisible: Boolean,
    appTabsViewModel: AppTabsViewModel = singletonViewModel(),
    appBarViewModel: AppBarViewModel = viewModel(),
    eventsViewModel: EventsViewModel = singletonViewModel(),
    onDismiss: () -> Unit,
) {
    if (isVisible)
        BackHandler {
            onDismiss()
        }
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTab by appBarViewModel.currentTab.collectAsState()
    val invitationsCount by eventsViewModel.invitationsCount.collectAsState()

    val tabs by appTabsViewModel.appTabs.collectAsState()


    // We want to distribute destinations into two columns.
    // If total amount of tabs is even, just draw both columns with equal size.
    // If total amount of tabs is odd, we want the leftmost column to hold the least amount of children
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Scrim for navigation
        ru.rtuitlab.itlab.presentation.ui.components.Scrim(
            color = MaterialTheme.colorScheme.scrim.copy(.25f),
            onDismiss = onDismiss,
            visible = isVisible
        )

        AnimatedVisibility(
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            visible = isVisible
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(ITEM_WIDTH.dp * COLUMNS_COUNT + ITEM_SPACING.dp + 16.dp)
                    .padding(
                        end = 16.dp,
                        bottom = 96.dp
                    )
                .background(Color.Transparent)
                    .pointerInput(onDismiss) { detectTapGestures { onDismiss() } },
                verticalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp / COLUMNS_COUNT),
                reverseLayout = true,
                userScrollEnabled = false
            ) {
                items(
                    items = tabs,
                    key = { it.route },
                    step = COLUMNS_COUNT
                ) { tabs ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ITEM_SPACING.dp, Alignment.End)
                    ) {
                        tabs
                            .filterNotNull()
                            .forEach { tab ->
                                NavigationItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                                    onClick = {
                                        onDismiss()

                                        // As per https://stackoverflow.com/questions/71789903/does-navoptionsbuilder-launchsingletop-work-with-nested-navigation-graphs-in-jet,
                                        // it seems to not be possible to have all three of multiple back stacks, resetting tabs and single top behavior at once by the means
                                        // of Jetpack Navigation APIs, but only two of the above.
                                        // This code provides resetting and singleTop behavior for the default tab.
                                        if (tab == currentTab) {
                                            navController.popBackStack(
                                                route = tab.startDestination,
                                                inclusive = false
                                            )
                                            return@NavigationItem
                                        }
                                        // This code always leaves default tab's start destination on the bottom of the stack.
                                        navController.navigate(tab.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true

                                            // We want to reset the graph if it is clicked while already selected
                                            restoreState = tab != currentTab
                                        }
                                        appBarViewModel.setCurrentTab(tab)
                                    },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (tab is AppTab.Events && invitationsCount > 0)
                                                    Badge(
                                                        containerColor = MaterialTheme.colorScheme.error,
                                                        contentColor = MaterialTheme.colorScheme.onError
                                                    ) {
                                                        Text(invitationsCount.toString())
                                                    }
                                            }
                                        ) {
                                            Icon(tab.icon, null)
                                        }
                                    },
                                    modifier = Modifier
                                        .width(ITEM_WIDTH.dp)
                                        .height(76.dp),
                                    label = {
                                        Text(
                                            text = stringResource(tab.resourceId),
                                            style = MaterialTheme.typography.labelSmall,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary
                                )
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current
) {
    val styledLabel: @Composable (() -> Unit)? = label?.let {
        @Composable {
            val style = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.Center)
            ProvideTextStyle(style, content = label)
        }
    }

    val ripple = rememberRipple(color = selectedContentColor)

    CompositionLocalProvider(
        LocalContentColor provides if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current
    ) {
        Column(
            modifier
                .clip(MaterialTheme.shapes.medium)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = ripple
                )
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            styledLabel?.let {
                Spacer(modifier = Modifier.height(4.dp))
                it()
            }
        }
    }

}

private inline fun <T> LazyListScope.items(
    items: List<T>,
    noinline key: (T) -> Any,
    step: Int = 1,
    crossinline itemContent: @Composable LazyItemScope.(items: List<T?>) -> Unit
) = items(
    count = items.size,
    key = { index: Int -> key(items[index]) }
) { index ->
    @Suppress("LABEL_NAME_CLASH")
    if (index % step != 0) return@items
    itemContent(
        (index until step + index).map {
            items.getOrNull(it)
        }
    )
}