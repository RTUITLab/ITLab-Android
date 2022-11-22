package ru.rtuitlab.itlab.presentation.screens.reports.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.reports.ReportsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.TransitionFloatingActionButton
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalPagerApi
@Composable
fun ReportsBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val isRefreshing by reportsViewModel.isRefreshing.collectAsState()
    val navController = LocalNavController.current

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        secondaryFloatingActionButton = {
            if (!isRefreshing)
                TransitionFloatingActionButton(
                    key = "Reports/New",
                    screenKey = AppScreen.Reports.route,
                    icon = Icons.Default.Add,
                    onClick = {
                        navController.navigate(AppScreen.NewReport.route)
                    },
                    transitionProgressSetter = {}
                )
        },
        searchBar = {
            SearchBar(
                onSearch = reportsViewModel::onSearch,
                onDismissRequest = it
            )
        }
    )
}