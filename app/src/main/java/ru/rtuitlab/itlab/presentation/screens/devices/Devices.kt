package ru.rtuitlab.itlab.presentation.screens.devices

import android.util.Log
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
import java.sql.Time
import java.time.Instant

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Devices(
	devicesViewModel: DevicesViewModel,

	bottomSheetViewModel: BottomSheetViewModel,

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


			if (devicesViewModel.accesibleFlow.collectAsState().value)
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



	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
		modifier = Modifier.fillMaxSize()
	) {


		items(devices.filter { it ->
				it.id != currentDeviceId.value
		}) { device ->


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
