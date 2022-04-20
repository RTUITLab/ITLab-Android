package ru.rtuitlab.itlab.presentation.ui.components.wheelBottomNavigation

import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.events.EventsViewModel
import ru.rtuitlab.itlab.presentation.ui.components.CustomBottomNavigation
import ru.rtuitlab.itlab.presentation.ui.components.CustomBottomNavigationItem
import ru.rtuitlab.itlab.presentation.ui.components.curve
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppBarViewModel
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.ui.theme.AppColors
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.RunnableHolder
import java.util.*

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalStdlibApi
@Composable
fun WheelNavigation(
	appBarViewModel: AppBarViewModel = viewModel(),
	appTabsViewModel: AppTabsViewModel = viewModel(),
	wheelNavigationViewModel: WheelNavigationViewModel = viewModel(),
	eventsViewModel: EventsViewModel,
	navController:NavHostController


) {

	val currentTab by appBarViewModel.currentTab.collectAsState()

	val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()


	val appTabsForCircle by appTabsViewModel.appTabs.collectAsState()

	//fill empty space in
	val appTabNull by appTabsViewModel.appTabNull.collectAsState()

	val statePage = appTabsViewModel.statePage.collectAsState().value

	val coroutineScope = rememberCoroutineScope()


	ConstraintLayout(
		modifier = Modifier.fillMaxSize()
	) {
		val (bottomnav, image) = createRefs()
		Image(
			painter = painterResource(R.drawable.bottom_navigation),
			contentDescription = "bottom",
			modifier = Modifier
				.constrainAs(image) {
					bottom.linkTo(parent.bottom)
				}
				.fillMaxWidth(),
			colorFilter = ColorFilter.lighting(
				MaterialTheme.colors.primarySurface,
				MaterialTheme.colors.primarySurface
			)
		)
		//sizes bottomnavigation
		val sizeremwidth = remember { mutableStateOf(0.dp) }
		val sizeremheight = remember { mutableStateOf(0.dp) }

		val density = LocalDensity.current

		val currentContext = LocalContext.current
		val swipeableState = rememberSwipeableState(1)
		//three anchors for infinity sliding
		val anchors = mapOf(with(LocalDensity.current) {
			-300.dp.toPx() } to 0,
			0f to 1,
			with(LocalDensity.current) { 300.dp.toPx() } to 2) // Maps anchor points (in px) to states


		CustomBottomNavigation(
			modifier = Modifier
				.width(300.dp)
				.constrainAs(bottomnav) {
					bottom.linkTo(parent.bottom)
					centerHorizontallyTo(parent)

				}
				.onSizeChanged {
					sizeremwidth.value = with(density) {
						it.width.toDp()
					}
					sizeremheight.value = with(density) {
						it.height.toDp()
					}

				}
				.swipeable(
					state = swipeableState,
					anchors = anchors,
					thresholds = { _, _ -> FractionalThreshold(0.3f) },
					orientation = Orientation.Horizontal,
				),
			//			elevation = 10.dp
		) {


			val (offsetY, setOffsetY) = remember { mutableStateOf(0.dp) }

			val (firstTime, setFirstTime) = remember { mutableStateOf(0) }


			val marginDown = remember { mutableStateOf((-20).dp) }
			IconButton(
				modifier = Modifier
					.size(40.dp, 40.dp)
					.align(Alignment.BottomCenter)
					.offset(0.dp, marginDown.value / 2),
				onClick = {

				}
			) {
				Icon(
					painter = painterResource(R.drawable.wheel),
					contentDescription = stringResource(R.string.rtuitlab),

					)
			}
			var currentState by remember { mutableStateOf(1) }
			val rotationAngle by animateFloatAsState(
				targetValue = if (swipeableState.targetValue != 1) with(density) { sizeremwidth.value.toPx() } else 0f,
				animationSpec = tween(durationMillis = 250),
				finishedListener = {
					if (it == with(density) { sizeremwidth.value.toPx() }) {
						if (statePage == 1) {
							appTabsViewModel.setSecondPage(coroutineScope)
						} else {
							appTabsViewModel.setFirstPage(coroutineScope)
						}
						//monitoring when elements is hiden
						//important reverse
						currentState = if (swipeableState.direction > 0) 2 else 0

						coroutineScope.launch {

							swipeableState.snapTo(1)

						}
					}
				}
			)

			val navBackStackEntry by navController.currentBackStackEntryAsState()
			val currentDestination = navBackStackEntry?.destination
			val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()
			appTabsForCircle
				.filter { it.accessible }
				.forEach { tab ->
					var positionremx by remember { mutableStateOf(0.dp) }
					var positionremy by remember { mutableStateOf(0.dp) }

					var positionsumx by remember { mutableStateOf(0.dp) }
					val sizeitemwidth = remember { mutableStateOf(0.dp) }
					val sizeitemheight = remember { mutableStateOf(0.dp) }

					//to what side elements have to move -1 - on the left; 1 - on the right
					val statedir =
						(if ((currentState == 2 && swipeableState.progress.to == 1) || swipeableState.targetValue == 0) -1 else 1)
					val oddValue = if(appTabsForCircle.filter { it.accessible }.size % 2 ==0)37.5f else 30f

					CustomBottomNavigationItem(
						modifier = Modifier
							.onSizeChanged {
								sizeitemwidth.value = with(density) {
									it.width.toDp()
								}
								sizeitemheight.value = with(density) {
									it.height.toDp()
								}
							}
							.onGloballyPositioned {
								positionremx = with(density) {
									it.positionInParent().x.toDp()
								}
								positionremy = with(density) {
									it.positionInParent().y.toDp()
								}
								positionsumx += positionremx
							}

							.offset(
								(statedir * with(density) { rotationAngle.toDp().value }).dp            // for animation move
									+ (oddValue                // margin left and right for first tab
									+ (300 / appTabsForCircle.filter { it.accessible }.size) * appTabsForCircle.filter { it.accessible }.indexOf(tab)).dp // between tabs * num of tab
									- sizeitemwidth.value / 2           // half of tab to the left
									,

								(sizeremheight.value - sizeitemheight.value)    // shift from top navigation to necessary place
										+ curve(                                           //formula for circle
									(statedir * with(density) { rotationAngle.toDp().value }).dp  //for animation move
											+ (oddValue             // optimal  margin left and right for first tab
											+ (300 / appTabsForCircle.filter { it.accessible }.size) * appTabsForCircle.filter { it.accessible }.indexOf(tab)).dp, // between tabs * num of tab
									marginDown.value,
									sizeremwidth.value / 2,
									sizeremheight.value.plus(sizeremwidth.value / 2),
									appTabsForCircle.filter { it.accessible }.size,
									appTabsForCircle.filter { it.accessible }.indexOf(tab),
									sizeremwidth.value / 2,
									setOffsetY,
									offsetY,
									setFirstTime,
									firstTime
								)
							),
						icon = {

							BadgedBox(
								badge = {
									if (tab is AppTab.Events && invitationsCount > 0)
									Badge(
									backgroundColor = Color.Red,
									contentColor = Color.White
								) {
										Text(invitationsCount.toString())
								}
								}
							) {
								if (tab != appTabNull) {
									Icon(tab.icon, null)
								}
							}
						},
						label = {
							if (tab != appTabNull) {

								Text(
									text = stringResource(tab.resourceId),
									fontSize = 9.sp,
									lineHeight = 16.sp,
									modifier = Modifier
										.onSizeChanged {
											marginDown.value = (-10).dp
										}
								)
							}
						},
						selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
						//alwaysShowLabel = true,
						onClick = {
							// As per https://stackoverflow.com/questions/71789903/does-navoptionsbuilder-launchsingletop-work-with-nested-navigation-graphs-in-jet,
							// it seems to not be possible to have all three of multiple back stacks, resetting tabs and single top behavior at once by the means
							// of Jetpack Navigation APIs, but only two of the above.
							// This code provides resetting and singleTop behavior for the default tab.
							if (tab == currentTab) {
								navController.popBackStack(
									route = tab.startDestination,
									inclusive = false
								)
								return@CustomBottomNavigationItem
							}

							// This code always leaves default tab's start destination on the bottom of the stack. Workaround needed?
							navController.navigate(tab.route) {
								popUpTo(navController.graph.findStartDestination().id) {
									saveState = true
								}
								launchSingleTop = true

								// We want to reset the graph if it is clicked while already selected
								restoreState = tab != currentTab
							}
							appBarViewModel.onChangeTab(tab)

						}
					)

				}
			//logging for well markup
			/*Box(
				modifier = Modifier
					.offset(37.5.dp,0.dp)
					.size(1.dp,700.dp)
					.background(Color.Red)
			){
			}
			Box(
				modifier = Modifier
					.offset((37.5+75).dp,0.dp)
					.size(1.dp,700.dp)
					.background(Color.Red)
			){
			}
			Box(
				modifier = Modifier
					.offset((37.5+150).dp,0.dp)
					.size(1.dp,700.dp)
					.background(Color.Red)
			){
			}*/
		}


	}
}