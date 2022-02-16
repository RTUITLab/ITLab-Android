package ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.components.EventDetailsBottomSheet
import ru.rtuitlab.itlab.presentation.screens.events.components.ShiftBottomSheet
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileEventsBottomSheet
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileSettingsBottomSheet
import ru.rtuitlab.itlab.presentation.utils.AppBottomSheet

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun BottomSheet(
	viewModel: BottomSheetViewModel = viewModel()
) {
	val currentSheet by viewModel.currentBottomSheet.collectAsState()
	val sheetIsVisible by viewModel.visibilityAsState.collectAsState()

	val coroutineScope = rememberCoroutineScope()
	if (sheetIsVisible)
		BackHandler {
			viewModel.hide(coroutineScope)
		}

	Column(
		modifier = Modifier
			.padding(
				top = 15.dp,
				start = 15.dp,
				end = 15.dp
			)
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			painter = painterResource(R.drawable.ic_upholder),
			contentDescription = null,
			tint = MaterialTheme.colors.onSurface
		)
		Spacer(Modifier.height(15.dp))

		when (currentSheet) {
			is AppBottomSheet.EventShift -> {
				val shift = currentSheet as AppBottomSheet.EventShift
				ShiftBottomSheet(
					shift = shift.shift,
					salaries = shift.salaries,
					eventViewModel = shift.eventViewModel,
					bottomSheetViewModel = viewModel,
					navController = shift.navController
				)
			}
			is AppBottomSheet.EventDescription -> {
				EventDetailsBottomSheet(markdownText = (currentSheet as AppBottomSheet.EventDescription).markdown)
			}
			is AppBottomSheet.ProfileSettings -> ProfileSettingsBottomSheet()
			is AppBottomSheet.ProfileEvents -> {
				val data = currentSheet as AppBottomSheet.ProfileEvents
				ProfileEventsBottomSheet(
					userViewModel = data.viewModel,
					onNavigate = data.onNavigate
				)
			}
			else -> {}
		}
	}

}