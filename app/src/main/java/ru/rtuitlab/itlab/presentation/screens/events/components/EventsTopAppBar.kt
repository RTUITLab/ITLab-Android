package ru.rtuitlab.itlab.presentation.screens.events.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarTabRow
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CenterAlignedTopAppBar
import ru.rtuitlab.itlab.presentation.utils.EventTab

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun EventsTopBar(
	eventsViewModel: EventsViewModel = viewModel(),
) {
	CenterAlignedTopAppBar(
		title = stringResource(R.string.events)
	) {
		AppBarTabRow(
			pagerState = eventsViewModel.pagerState,
			tabs = listOf(
				EventTab.All,
				EventTab.My
			)
		)
	}
}

