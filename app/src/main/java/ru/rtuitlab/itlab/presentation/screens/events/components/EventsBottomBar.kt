@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)

package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.util.Pair
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabelledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@Composable
fun EventsBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    eventsViewModel: EventsViewModel = viewModel(),
    appBarViewModel: AppBarViewModel = viewModel()
) {
    val showPastEventsChecked by eventsViewModel.showPastEvents.collectAsState()

    val context = LocalContext.current

    val listener = MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
        eventsViewModel.setEventsDates(it.first, it.second)
    }

    val isDateSelectionMade by eventsViewModel.isDateSelectionMade.collectAsState()

    val beginEventsDate by eventsViewModel.beginEventsDate.collectAsState()
    val endEventsDate by eventsViewModel.endEventsDate.collectAsState()

    val navController by appBarViewModel.currentNavHost.collectAsState()

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = { collapseAction ->
                    LabelledCheckBox(
                        checked = showPastEventsChecked,
                        onCheckedChange = {
                            eventsViewModel.toggleShowPastEvents(it)
                            collapseAction()
                        },
                        label = stringResource(R.string.events_show_past)
                    )
                    DropdownMenuItem(
                        onClick = {
                            if (isDateSelectionMade)
                                eventsViewModel.clearDateSelection()
                            else
                                MaterialDatePicker
                                    .Builder
                                    .dateRangePicker()
                                    .setSelection(
                                        Pair(beginEventsDate, endEventsDate)
                                    )
                                    .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                                    .build()
                                    .apply {
                                        show((context as AppCompatActivity).supportFragmentManager, null)
                                        addOnPositiveButtonClickListener(listener)
                                    }
                            collapseAction()
                        },
                        text = {
                            Text(
                                text = stringResource(
                                    if (isDateSelectionMade) R.string.events_clear_period
                                    else R.string.events_choose_period
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            ),
            AppBarOption.Clickable(
                icon = Icons.Default.Notifications,
                onClick = {
                    appBarViewModel.onNavigate(AppScreen.EventsNotifications)
                    navController?.navigate(AppScreen.EventsNotifications.route)
                },
                badgeCount = eventsViewModel.invitationsCount.collectAsState().value
            )
        ),
        searchBar = {
            SearchBar(
                onSearch = eventsViewModel::onSearch,
                onDismissRequest = it
            )
        }
    )
}