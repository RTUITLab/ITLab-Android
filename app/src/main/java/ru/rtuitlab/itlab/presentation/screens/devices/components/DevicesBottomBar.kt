package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabelledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    devicesViewModel: DevicesViewModel = viewModel(),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {

    val showFreeDevicesChecked by devicesViewModel.isFreeFilterChecked.collectAsState()

    val isRefreshing by devicesViewModel.isRefreshing.collectAsState()
    val navController = LocalNavController.current

    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        secondaryFloatingActionButton = {
            if (!isRefreshing && devicesViewModel.isAccessible.collectAsState().value)
                FloatActionButton(
                    devicesViewModel = devicesViewModel,
                    bottomSheetViewModel = bottomSheetViewModel)

        },
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = { collapseAction ->
                    LabelledCheckBox(
                        checked = showFreeDevicesChecked,
                        onCheckedChange = {
                            devicesViewModel.onFilteringChanged(it)
                            collapseAction()
                        },
                        label = stringResource(R.string.show_only_free)
                    )
                }
            )
        ),
        searchBar = {
            SearchBar(
                onSearch = devicesViewModel::onSearch,
                onDismissRequest = it
            )
        }
    )
}