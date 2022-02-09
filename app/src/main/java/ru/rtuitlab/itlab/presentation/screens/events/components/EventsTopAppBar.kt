package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabelledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarTabRow
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CollapsibleTopAppBar
import ru.rtuitlab.itlab.presentation.utils.EventTab

@ExperimentalMaterialApi
@ExperimentalMotionApi
@ExperimentalPagerApi
@Composable
fun EventsTopAppBar(
	eventsViewModel: EventsViewModel = viewModel()
) {

	var searchActivated by rememberSaveable { mutableStateOf(false) }

	if (searchActivated)
		BackHandler {
			searchActivated = false
		}

	val showPastEventsChecked by eventsViewModel.showPastEvents.collectAsState()

	CollapsibleTopAppBar(
		title = stringResource(R.string.events),
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
				}
			),
			AppBarOption.Clickable(
				icon = Icons.Default.Search,
				onClick = {
					searchActivated = true
				}
			)
		),
		swipingState = eventsViewModel.swipingState,
		hideBackButton = !searchActivated,
		hideOptions = searchActivated,
		onBackAction = {
			searchActivated = false
			eventsViewModel.onSearch("")
		},
		searchActivated = searchActivated,
		searchBar = {
			SearchBar(
				onSearch = eventsViewModel::onSearch
			)
		}
	) {
		AppBarTabRow(
			modifier = it,
			pagerState = eventsViewModel.pagerState,
			tabs = listOf(
				EventTab.All,
				EventTab.My
			)
		)
	}
}
