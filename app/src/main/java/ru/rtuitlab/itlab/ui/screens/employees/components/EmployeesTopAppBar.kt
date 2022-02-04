package ru.rtuitlab.itlab.ui.screens.employees.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.ui.shared.SearchBar
import ru.rtuitlab.itlab.ui.shared.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.ui.shared.top_app_bars.ExtendedTopAppBar
import ru.rtuitlab.itlab.viewmodels.EmployeesViewModel

@Composable
fun EmployeesTopAppBar(
	employeesViewModel: EmployeesViewModel = viewModel()
) {
	var searchActivated by rememberSaveable { mutableStateOf(false) }

	if (searchActivated)
		BackHandler {
			searchActivated = false
		}

	ExtendedTopAppBar(
		options = listOf(
			AppBarOption(
				icon = Icons.Default.Search,
				onClick = {
					searchActivated = true
				}
			)
		),
		hideBackButton = !searchActivated,
		hideOptions = searchActivated,
		onBackAction = {
			searchActivated = false
			employeesViewModel.onSearch("")
		}
	) {
		if (searchActivated) {
			SearchBar(
				onSearch = employeesViewModel::onSearch
			)
		} else {
			Text(
				text = stringResource(R.string.employees),
				fontSize = 20.sp,
				fontWeight = FontWeight(500),
				textAlign = TextAlign.Start,
				color = MaterialTheme.colors.onSurface
			)
		}

	}
}

