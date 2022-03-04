package ru.rtuitlab.itlab.presentation.screens.devices

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceCard
import ru.rtuitlab.itlab.presentation.screens.devices.components.FloatActionButton
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Devices(
	devicesViewModel: DevicesViewModel,
	//employeesViewModel: EmployeesViewModel,
	bottomSheetViewModel: BottomSheetViewModel,
	navController: NavController
) {
	val devicesResource by devicesViewModel.deviceResponsesFlow.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }



	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		scaffoldState = rememberScaffoldState(snackbarHostState = devicesViewModel.snackbarHostState)

	) {


		SwipeRefresh(
			modifier = Modifier
				.fillMaxSize(),
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = devicesViewModel::onRefresh
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
			) {
				devicesResource.handle(
					onLoading = {
						isRefreshing = true
					},
					onError = { msg ->
						isRefreshing = false
						LoadingError(msg = msg)
					},
					onSuccess = {
						isRefreshing = false
						devicesViewModel.onResourceSuccess(it)
						DeviceList(devicesViewModel, bottomSheetViewModel)


					}

				)
			}


			val isAccesile = devicesViewModel.accesibleFlow.collectAsState().value
			if (isAccesile)
				FloatActionButton(devicesViewModel, bottomSheetViewModel)

		}


	}
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun DeviceList(
	devicesViewModel: DevicesViewModel,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val devices by devicesViewModel.devicesFlow.collectAsState()
	val currentDeviceId = devicesViewModel.deviceIdFlow.collectAsState()
	val currentDevice = devices.find { it.id == currentDeviceId.value }

	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
		modifier = Modifier.fillMaxSize()
	) {
/*
                if (currentDevice != null && currentUser!= null)
                        item {
                                DeviceCard(
                                        devicesViewModel = devicesViewModel,
                                        device = currentDevice,
                                        owner = currentUser,
                                        state = state,
                                        scope = scope,
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {

                                                        navController.navigate(AppScreen.Devices.route)
                                                },
                                        navController = navController
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                        }


 */

		items(devices.filter { it.id != currentDeviceId.value }) { device ->


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
