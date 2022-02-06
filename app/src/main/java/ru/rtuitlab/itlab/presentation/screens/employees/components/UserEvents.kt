package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.users.models.UserEventModel
import ru.rtuitlab.itlab.presentation.ui.extensions.toClientDate
import ru.rtuitlab.itlab.presentation.UserViewModel

@Composable
fun UserEvents(
	userViewModel: UserViewModel,
	userEventsResource: Resource<List<UserEventModel>>
) {
	userEventsResource.handle(
		onLoading = {
			CircularProgressIndicator()
		},
		onError = { msg ->
			Text(text = msg)
		},
		onSuccess = { events ->
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier
						.padding(16.dp)
				) {
					Text(stringResource(R.string.events), fontSize = 20.sp)
					DateSelection(userViewModel)
					events.forEachIndexed { index, event ->
						Text("$index: ${event.title}")
					}
				}
			}
		}
	)
}

@Composable
private fun DateSelection(userViewModel: UserViewModel) {
	val activity = (LocalContext.current as AppCompatActivity)
	Button(onClick = {
		MaterialDatePicker
			.Builder
			.dateRangePicker()
			.setSelection(
				Pair(userViewModel.beginEventsDate, userViewModel.endEventsDate)
			)
			.build()
			.apply {
				show(activity.supportFragmentManager, null)
				addOnPositiveButtonClickListener {
					userViewModel.setEventsDates(it.first!!, it.second!!)
				}
			}
	}) {
		userViewModel.run {
			Text("${beginEventsDate.toClientDate()} -> ${endEventsDate.toClientDate()}")
		}
	}
}