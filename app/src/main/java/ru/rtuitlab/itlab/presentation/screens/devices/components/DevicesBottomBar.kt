package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LabeledCheckBox
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.ITLabBottomBarDefaults
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@Composable
fun DevicesBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    devicesViewModel: DevicesViewModel = viewModel(),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {

    val showFreeDevicesChecked by devicesViewModel.isFreeFilterChecked.collectAsState()

    val isRefreshing by devicesViewModel.isRefreshing.collectAsState()

    val coroutineScope = rememberCoroutineScope()


    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        secondaryFloatingActionButton = {
            if (!isRefreshing && devicesViewModel.isAccessible.collectAsState().value)
                FloatingActionButton(
                    containerColor = ITLabBottomBarDefaults.secondaryFloatingActionButtonContainerColor,
                    elevation = ITLabBottomBarDefaults.floatingActionButtonsElevation,
                    onClick = {
                        bottomSheetViewModel.show(
                            AppBottomSheet.DeviceNew(devicesViewModel,bottomSheetViewModel),
                            coroutineScope)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }

        },
        options = listOf(
            AppBarOption.Dropdown(
                icon = Icons.Default.FilterList,
                dropdownMenuContent = { collapseAction ->
                    LabeledCheckBox(
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