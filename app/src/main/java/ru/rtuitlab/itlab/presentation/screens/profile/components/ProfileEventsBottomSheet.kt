package ru.rtuitlab.itlab.presentation.screens.profile.components

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.presentation.UserViewModel
import ru.rtuitlab.itlab.presentation.screens.events.components.UserEventCardContent
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.extensions.toClientDate
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ProfileEventsBottomSheet(
	onNavigate: (id: String, title: String) -> Unit,
	bottomSheetViewModel: BottomSheetViewModel = viewModel(),
	userViewModel: UserViewModel
) {
	val userEventsResource by userViewModel.userEventsFlow.collectAsState()
	val beginEventsDate by userViewModel.beginEventsDate.collectAsState()
	val endEventsDate by userViewModel.endEventsDate.collectAsState()
	LaunchedEffect(userEventsResource) {
		if (userEventsResource is Resource.Empty)
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
		tint = AppColors.accent.collectAsState().value
	) {
		Text("${beginEventsDate.toClientDate()} — ${endEventsDate.toClientDate()}")
	}
	userEventsResource.handle(
		onLoading = {
			CircularProgressIndicator(
				modifier = Modifier.padding(16.dp),
				color = AppColors.accent.collectAsState().value
			)
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { events ->
			Spacer(modifier = Modifier.height(10.dp))
			if (events.isEmpty())
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					contentAlignment = Alignment.Center
				) {
					Text(stringResource(R.string.events_nothing_in_period))
				}
			else
				LazyColumn {
					items(
						items = events.distinctBy { it.id },
						key = { it.id }
					) {
						Column(
							modifier = Modifier.clickable {
								onNavigate(it.id, it.title)
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
	)
}