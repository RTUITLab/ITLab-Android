package ru.rtuitlab.itlab.ui.screens.events.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.ui.shared.SearchBar
import ru.rtuitlab.itlab.ui.shared.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.ui.shared.top_app_bars.AppBarTabRow
import ru.rtuitlab.itlab.ui.shared.top_app_bars.CollapsibleTopAppBar
import ru.rtuitlab.itlab.utils.EventTab
import ru.rtuitlab.itlab.viewmodels.EventsViewModel

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


	CollapsibleTopAppBar(
		title = stringResource(R.string.events),
		options = listOf(
			AppBarOption(
				icon = Icons.Default.Settings,
				onClick = {}
			),
			AppBarOption(
				icon = Icons.Default.Notifications,
				onClick = {}
			),
			AppBarOption(
				icon = Icons.Default.FilterList,
				onClick = {}
			),
			AppBarOption(
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
