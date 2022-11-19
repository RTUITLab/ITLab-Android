package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar

@Composable
fun EmployeesBottomBar(
    mainFloatingActionButton: @Composable (() -> Unit),
    employeesViewModel: EmployeesViewModel = viewModel()
) {
    BottomAppBar(
        mainFloatingActionButton = mainFloatingActionButton,
        searchBar = {
            SearchBar(
                onSearch = employeesViewModel::onSearchQueryChange,
                onDismissRequest = it
            )
        }
    )
}