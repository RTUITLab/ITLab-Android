@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.screens.devices

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceCard
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.extensions.collectUiEvents
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel

@ExperimentalAnimationApi
@Composable
fun Devices(
	devicesViewModel: DevicesViewModel = singletonViewModel(),
	bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
	val devicesResource by devicesViewModel.deviceResponsesFlow.collectAsState()
	val isRefreshing by devicesViewModel.isRefreshing.collectAsState()

	val snackbarHostState = remember { SnackbarHostState() }

	devicesViewModel.uiDevices.collectUiEvents(snackbarHostState)

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) {
		SwipeRefresh(
			modifier = Modifier
				.fillMaxSize()
				.padding(it),
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = {
				devicesViewModel.onRefresh()
			}
		) {
				Column(
					modifier = Modifier
						.fillMaxSize()
				) {
					devicesResource.handle(
						onLoading = {
						},
						onError = { msg ->
							LoadingError(msg = msg)
						},
						onSuccess = {
							DeviceList(devicesViewModel, bottomSheetViewModel)
						}
					)
				}
			}
	}

}

@ExperimentalAnimationApi
@Composable
private fun DeviceList(
	devicesViewModel: DevicesViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val devices by devicesViewModel.devicesFlow.collectAsState()

	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(horizontal = 16.dp, vertical = 23.dp),
		modifier = Modifier.fillMaxSize()
	) {
		items(devices) { device ->
			DeviceCard(
				devicesViewModel = devicesViewModel,
				bottomSheetViewModel = bottomSheetViewModel,
				device = device,
				modifier = Modifier
					.fillMaxWidth()

			)
		}
	}
}
