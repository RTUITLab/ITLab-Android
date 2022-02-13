package ru.rtuitlab.itlab.presentation.screens.profile.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalMaterialApi
@Composable
fun ProfileTopAppBar(
	text: String,
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	onBackAction: () -> Unit
) {
	val scope = rememberCoroutineScope()
	BasicTopAppBar(
		text = text,
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
		),
		onBackAction = onBackAction
	)
}