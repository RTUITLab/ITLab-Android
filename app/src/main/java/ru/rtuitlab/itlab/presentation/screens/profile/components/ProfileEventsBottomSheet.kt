package ru.rtuitlab.itlab.presentation.screens.profile.components

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.util.Pair
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.extensions.toClientDate
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.screens.events.components.UserEventCardContent
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.LoadingErrorRetry
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ProfileEventsBottomSheet(
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	userViewModel: UserViewModel
) {
	val beginEventsDate by userViewModel.beginEventsDate.collectAsState()
	val endEventsDate by userViewModel.endEventsDate.collectAsState()

	val events by userViewModel.events.collectAsState()
	val areEventsRefreshing by userViewModel.areEventsRefreshing.collectAsState()
	val errorMessage by userViewModel.eventUpdateErrorMessage.collectAsState()

	LaunchedEffect(Unit) {
		if (events.isEmpty())
			userViewModel.setEventsDates(
				begin = beginEventsDate,
				end = endEventsDate
			)
	}
	val scope = rememberCoroutineScope()
	val listener = MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
		userViewModel.setEventsDates(it.first, it.second)
	}
	val context = LocalContext.current
	val navController = LocalNavController.current
	IconizedRow(
		modifier = Modifier
			.clickable {
				MaterialDatePicker
					.Builder
					.dateRangePicker()
					.setSelection(
						Pair(beginEventsDate, endEventsDate)
					)
					.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
					.build()
					.apply {
						show((context as AppCompatActivity).supportFragmentManager, null)
						addOnPositiveButtonClickListener(listener)
					}
			}
			.fillMaxWidth(),
		imageVector = Icons.Default.DateRange,
		spacing = 10.dp,
		opacity = 1f,
		tint = MaterialTheme.colorScheme.primary
	) {
		Text("${beginEventsDate.toClientDate()} — ${endEventsDate.toClientDate()}")
	}

	Spacer(modifier = Modifier.height(10.dp))
	if (events.isEmpty()) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			contentAlignment = Alignment.Center
		) {
			Text(stringResource(R.string.events_nothing_in_period))
		}
	} else {
		LazyColumn {
			items(
				items = events.distinctBy { it.id },
				key = { it.id }
			) {
				Column(
					modifier = Modifier.clickable {
						navController.navigate("${AppScreen.EventDetails.navLink}/${it.id}")
						bottomSheetViewModel.hide(scope)
					}
				) {
					Divider()
					Spacer(modifier = Modifier.height(10.dp))
					UserEventCardContent(it)
					Spacer(modifier = Modifier.height(10.dp))
				}
			}
		}
	}

	errorMessage?.let {
		LoadingErrorRetry(errorMessage = it) {
			userViewModel.updateEvents()
		}
	}

	if (areEventsRefreshing) {
		CircularProgressIndicator(
			modifier = Modifier.padding(16.dp)
		)
	}
}