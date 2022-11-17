package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarOption
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.ExtendedTopAppBar

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
			AppBarOption.Clickable(
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
			employeesViewModel.onSearchQueryChange("")
		}
	) {
		if (searchActivated) {
			SearchBar(
				onSearch = employeesViewModel::onSearchQueryChange
			)
		} else {
			Text(
				text = stringResource(R.string.employees),
				style = MaterialTheme.typography.titleLarge
			)
		}

	}
}
