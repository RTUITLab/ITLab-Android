package ru.rtuitlab.itlab.presentation.screens.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@Composable
fun ProfileBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        options = listOf(
            AppBarOption.Clickable(
                icon = Icons.Default.Settings,
                contentDescription = null,
                onClick = {
                    bottomSheetViewModel.show(
                        sheet = AppBottomSheet.ProfileSettings,
                        scope = scope
                    )
                }
            )
        )
    )
}