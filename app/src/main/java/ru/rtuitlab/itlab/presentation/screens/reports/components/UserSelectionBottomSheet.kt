package ru.rtuitlab.itlab.presentation.screens.reports.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeesViewModel
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeeCard
import ru.rtuitlab.itlab.presentation.ui.components.SearchBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalMaterialApi
@Composable
fun UserSelectionBottomSheet(
	employeesViewModel: EmployeesViewModel = viewModel(),
	onSelect: (User) -> Unit,
	bottomSheetViewModel: BottomSheetViewModel
) {
	val users by employeesViewModel.users.collectAsState()
	val currentUser by employeesViewModel.currentUser.collectAsState(initial = null)

	val scope = rememberCoroutineScope()

	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(vertical = 15.dp)
	) {
		item {
			SearchBar {
				employeesViewModel.onSearchQueryChange(it)
			}
		}
		currentUser?.let { currentUser ->
			item {
				EmployeeCard(
                    user = currentUser,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(currentUser)
                            bottomSheetViewModel.hide(scope)
                        }
                )
				Spacer(modifier = Modifier.height(8.dp))
			}
			items(users.filter { it.id != currentUser.id }) { user ->
				EmployeeCard(
                    user = user,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(user)
                            bottomSheetViewModel.hide(scope)
                        }
                )
			}
		}
	}
}