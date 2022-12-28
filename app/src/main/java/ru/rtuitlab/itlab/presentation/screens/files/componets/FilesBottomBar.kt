package ru.rtuitlab.itlab.presentation.screens.files.componets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.FilesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabeledRadioButton
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel


@Composable
fun FilesBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    filesViewModel: FilesViewModel = viewModel(),
    appBarViewModel: AppBarViewModel = viewModel()
) {

    val context = LocalContext.current

    val selectedSortingMethod by filesViewModel.selectedSortingMethod.collectAsState()


    val navController by appBarViewModel.currentNavHost.collectAsState()

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = { collapseAction ->
                    FilesViewModel.SortingMethod.values().forEach { sortingMethod ->
                        LabeledRadioButton(
                            state = sortingMethod == selectedSortingMethod,
                            onCheckedChange = {
                                filesViewModel.onSortingChanged(sortingMethod)
                                collapseAction()
                            },

                            label = stringResource(sortingMethod.nameResource)
                        )
                    }
                }
            )
        ),
        searchBar = {
            SearchBar(
                onSearch = filesViewModel::onSearch,
                onDismissRequest = it
            )
        }
    )
}