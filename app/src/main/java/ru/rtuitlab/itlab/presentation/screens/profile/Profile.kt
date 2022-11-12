package ru.rtuitlab.itlab.presentation.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.presentation.screens.employees.EmployeeCredentials
import ru.rtuitlab.itlab.presentation.screens.employees.components.UserEvents
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Profile(
    profileViewModel: ProfileViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {
    val user by profileViewModel.user.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            user?.let {
                EmployeeCredentials(it)
            }
            /*UserDevices(userDevicesResource)*/
            UserEvents(
                userViewModel = profileViewModel,
                bottomSheetViewModel = bottomSheetViewModel
            )
        }
    }
}