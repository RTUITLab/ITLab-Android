@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rtuitlab.itlab.presentation.ui


import androidx.compose.animation.*
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.navigation.AppNavigation
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.navigation.NavigationControl
import ru.rtuitlab.itlab.presentation.screens.devices.components.DevicesBottomBar
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmployeesBottomBar
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsBottomBar
import ru.rtuitlab.itlab.presentation.screens.events.components.EventsTopBar
import ru.rtuitlab.itlab.presentation.screens.feedback.components.FeedbackTopAppBar
import ru.rtuitlab.itlab.presentation.screens.files.componets.FilesBottomBar
import ru.rtuitlab.itlab.presentation.screens.profile.components.ProfileBottomBar
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectBottomBar
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectTopAppBar
import ru.rtuitlab.itlab.presentation.screens.projects.components.ProjectsBottomBar
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchaseTopAppBar
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchasesBottomBar
import ru.rtuitlab.itlab.presentation.screens.purchases.components.PurchasesTopAppBar
import ru.rtuitlab.itlab.presentation.screens.reports.components.ReportsBottomBar
import ru.rtuitlab.itlab.presentation.ui.components.bottom_app_bar.ITLabBottomBarDefaults
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheet
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel
import ru.rtuitlab.itlab.presentation.ui.components.shared_elements.LocalSharedElementsRootScope
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.BasicTopAppBar
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.CenterAlignedTopAppBar
import ru.rtuitlab.itlab.presentation.utils.AppScreen
import kotlin.math.roundToInt

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

    val density = LocalDensity.current

    // Used to animate FAB between screens that have a bottom bar and the ones that don't
    val mainFabOffset by animateIntOffsetAsState(
        targetValue = if (currentScreen.hasBottomBar)
            IntOffset.Zero
        else with(density) {
            IntOffset(
                x = 0,
                // Difference between bottom bar FAB padding and standalone FAB padding
                y = -4.dp.toPx().roundToInt()
            )
        }
    )

    val mainFloatingActionButton: @Composable () -> Unit = {
        FloatingActionButton(
            modifier = Modifier.offset { mainFabOffset },
            onClick = { isNavigationOpen = true },
            containerColor = ITLabBottomBarDefaults.mainFloatingActionButtonContainerColor,
            elevation = ITLabBottomBarDefaults.floatingActionButtonsElevation
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
            modifier = Modifier.imePadding(),
            topBar = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .statusBarsPadding()
                        .animateContentSize()
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
                        AppScreen.EmployeeDetails ->
                            BasicTopAppBar(
                                text = stringResource(currentScreen.screenNameResource),
                                onBackAction = onBackAction
                            )

                        AppScreen.Employees,
                        AppScreen.Purchases,
                        AppScreen.Devices,
                        AppScreen.Files,
                        AppScreen.Projects,
                        AppScreen.Reports ->
                            CenterAlignedTopAppBar(title = stringResource(currentScreen.screenNameResource))

                        AppScreen.Feedback -> FeedbackTopAppBar()

                        is AppScreen.ReportDetails -> BasicTopAppBar(
                            text = stringResource(
                                currentScreen.screenNameResource,
                                (currentScreen as AppScreen.ReportDetails).title
                            ),
                            onBackAction = onBackAction
                        )
                        AppScreen.Purchases -> PurchasesTopAppBar()
                        is AppScreen.PurchaseDetails -> PurchaseTopAppBar()
                        is AppScreen.ProjectDetails -> ProjectTopAppBar()
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
                        bottom = if (currentScreen.hasBottomBar) it.calculateBottomPadding() else 0.dp,
                        top = it.calculateTopPadding()
                    )
                ) {
                    AppNavigation(navController)
                }
            },
            floatingActionButton = {
                if (!currentScreen.hasBottomBar)
                    mainFloatingActionButton()
            },
            bottomBar = {
                if (!currentScreen.hasBottomBar) return@Scaffold
                when (currentScreen) {
                    AppScreen.Events -> EventsBottomBar(mainFloatingActionButton)
                    AppScreen.Employees -> EmployeesBottomBar(mainFloatingActionButton)
                    AppScreen.Profile -> ProfileBottomBar(mainFloatingActionButton)
                    AppScreen.Reports -> ReportsBottomBar(mainFloatingActionButton)
                    AppScreen.Purchases -> PurchasesBottomBar(mainFloatingActionButton)
                    AppScreen.Devices -> DevicesBottomBar(mainFloatingActionButton)
                    AppScreen.Files -> FilesBottomBar(mainFloatingActionButton)
                    AppScreen.Projects -> ProjectsBottomBar(mainFloatingActionButton)
                    is AppScreen.ProjectDetails -> ProjectBottomBar(mainFloatingActionButton)
                }
            }
        )
        NavigationControl(
            isVisible = isNavigationOpen,
            onDismiss = { isNavigationOpen = false }
        )
    }
}