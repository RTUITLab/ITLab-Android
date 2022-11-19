@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.ui


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.AppNavigation
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.navigation.NavigationControl
import ru.rtuitlab.itlab.presentation.screens.devices.components.DevicesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesBottomBar
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsBottomBar
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopBar
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileBottomBar
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchaseTopAppBar
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchasesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.reports.components.ReportsTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.BottomAppBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CenterAlignedTopAppBar
import ru.rtuitlab.itlab.presentation.utils.AppScreen

@ExperimentalSerializationApi
@ExperimentalStdlibApi
@ExperimentalMotionApi
@ExperimentalMaterialApi
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ITLabApp(
    appBarViewModel: AppBarViewModel = viewModel(),
    bottomSheetViewModel: BottomSheetViewModel = viewModel()
) {
    val currentScreen by appBarViewModel.currentScreen.collectAsState()

    val navController = LocalNavController.current

    val sharedElementScope = LocalSharedElementsRootScope.current

    val onBackAction: () -> Unit = {
        if (sharedElementScope?.isRunningTransition == false)
            if (!navController.popBackStack()) appBarViewModel.handleDeepLinkPop()
    }

    LaunchedEffect(bottomSheetViewModel.bottomSheetState.currentValue) {
        if (bottomSheetViewModel.bottomSheetState.currentValue == ModalBottomSheetValue.Hidden)
            bottomSheetViewModel.hide(this)
    }

    var isNavigationOpen by remember { mutableStateOf(false) }

    val mainFloatingActionButton: @Composable () -> Unit = {
        FloatingActionButton(
            onClick = { isNavigationOpen = true },
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(512.dp),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(0.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(R.drawable.ic_itlab),
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetViewModel.bottomSheetState,
        sheetContent = { BottomSheet() },
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        scrimColor = MaterialTheme.colorScheme.scrim.copy(.25f)
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier.animateContentSize()
                ) {
                    when (currentScreen) {
                        AppScreen.Events -> EventsTopBar()
                        is AppScreen.EventDetails -> BasicTopAppBar(
                            text = stringResource(
                                currentScreen.screenNameResource,
                                (currentScreen as AppScreen.EventDetails).title
                            ),
                            onBackAction = onBackAction
                        )
                        AppScreen.EventNew,
                        AppScreen.Profile,
                        AppScreen.EmployeeDetails -> BasicTopAppBar(
                            text = stringResource(currentScreen.screenNameResource),
                            onBackAction = onBackAction
                        )
                        AppScreen.Employees -> CenterAlignedTopAppBar(title = stringResource(R.string.employees))
                        AppScreen.Feedback -> FeedbackTopAppBar()
                        AppScreen.Devices -> DevicesTopAppBar()
                        AppScreen.Reports -> ReportsTopAppBar()
                        is AppScreen.ReportDetails -> BasicTopAppBar(
                            text = stringResource(
                                currentScreen.screenNameResource,
                                (currentScreen as AppScreen.ReportDetails).title
                            ),
                            onBackAction = onBackAction
                        )
                        AppScreen.Purchases -> PurchasesTopAppBar()
                        is AppScreen.PurchaseDetails -> PurchaseTopAppBar()
                        else -> BasicTopAppBar(
                            text = stringResource(currentScreen.screenNameResource),
                            onBackAction = onBackAction
                        )
                    }
                }
            },
            content = {
                Box(
                    modifier = Modifier.padding(
                        bottom = it.calculateBottomPadding(),
                        top = it.calculateTopPadding()
                    )
                ) {
                    AppNavigation(navController)
                }
            },
            bottomBar = {
                when (currentScreen) {
                    is AppScreen.Events -> EventsBottomBar(mainFloatingActionButton)
                    is AppScreen.Employees -> EmployeesBottomBar(mainFloatingActionButton)
                    is AppScreen.Profile -> ProfileBottomBar(mainFloatingActionButton)
                    else -> BottomAppBar(mainFloatingActionButton = mainFloatingActionButton)
                }
            }
        )
        NavigationControl(
            isVisible = isNavigationOpen,
            onDismiss = { isNavigationOpen = false }
        )
    }
}