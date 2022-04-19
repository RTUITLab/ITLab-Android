package ru.rtuitlab.itlab.presentation.ui.components.wheelBottomNavigation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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


) {

	val currentTab = appBarViewModel.currentTab.collectAsState().value

	val invitationsCount by eventsViewModel.invitationsCountFlow.collectAsState()


	val appTabsForCircle by appTabsViewModel.appTabs.collectAsState()

	val appTabNull by appTabsViewModel.appTabNull.collectAsState()

	val statePage = appTabsViewModel.statePage.collectAsState().value

	val coroutineScope = rememberCoroutineScope()

	val eventsResetTask = RunnableHolder()
	val projectsResetTask = RunnableHolder()
	val devicesResetTask = RunnableHolder()
	val employeesResetTask = RunnableHolder()
	val feedbackResetTask = RunnableHolder()
	val profileResetTask = RunnableHolder()

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
		val sizeremwidth = remember { mutableStateOf(0.dp) }
		val sizeremheight = remember { mutableStateOf(0.dp) }

		val density = LocalDensity.current

		val currentContext = LocalContext.current
		val swipeableState = rememberSwipeableState(1)
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
							//visibilityPage.value = false
							appTabsViewModel.setSecondPage(coroutineScope)
						} else {
							//visibilityPage.value = true
							appTabsViewModel.setFirstPage(coroutineScope)
						}
						//if(it == with(density){sizeremwidth.value.toPx()}){
						currentState = if (swipeableState.direction > 0) 2 else 0
						Log.d(
							"Auth",
							"$currentState LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL ${swipeableState.direction}"
						)
						coroutineScope.launch {


							swipeableState.snapTo(1)

						}
					}
				}
			)

			//print(screenWidth)
			appTabsForCircle
				.filter { it.accessible }
				.forEach { screen ->
					var positionremx by remember { mutableStateOf(0.dp) }
					var positionremy by remember { mutableStateOf(0.dp) }

					var positionsumx by remember { mutableStateOf(0.dp) }
					val sizeitemwidth = remember { mutableStateOf(0.dp) }
					val sizeitemheight = remember { mutableStateOf(0.dp) }

					val statedir =
						(if ((currentState == 2 && swipeableState.progress.to == 1) || swipeableState.targetValue == 0) -1 else 1)

					Log.d("Auth", "$currentState ${swipeableState.progress.to}")

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
							.offset((statedir * with(density) { rotationAngle.toDp().value }).dp + (300 / appTabsForCircle.filter { it.accessible }.size / 2 + (300 / appTabsForCircle.filter { it.accessible }.size) * appTabsForCircle
								.filter { it.accessible }
								.indexOf(screen)).dp - sizeitemwidth.value / 2,
								(sizeremheight.value - sizeitemheight.value) + curve((statedir * with(
									density
								) { rotationAngle.toDp().value }).dp +
										(30 + (300 / appTabsForCircle.filter { it.accessible }.size) * appTabsForCircle
											.filter { it.accessible }
											.indexOf(screen)).dp,
									marginDown.value,
									sizeremwidth.value.div(2),
									sizeremheight.value.plus(sizeremwidth.value / 2),
									appTabsForCircle.filter { it.accessible }.size,
									appTabsForCircle
										.filter { it.accessible }
										.indexOf(screen),
									sizeremwidth.value.times(0.5f),
									setOffsetY,
									offsetY,
									setFirstTime,
									firstTime
								)
							),//curve(appTabs.filter{ it.accessible }.size,appTabs.filter{ it.accessible }.indexOf(screen),100.dp,10.dp)),
						icon = {

							BadgedBox(
								badge = {
									if (screen is AppTab.Events && invitationsCount > 0)
									Badge(
									backgroundColor = Color.Red,
									contentColor = Color.White
								) {
										Text(invitationsCount.toString())
								}
								}
							) {
								if (screen != appTabNull) {
									Icon(screen.icon, null)
								}
							}
						},
						label = {
							if (screen != appTabNull) {

								Text(
									text = stringResource(screen.resourceId),
									fontSize = 9.sp,
									lineHeight = 16.sp,
									modifier = Modifier
										.onSizeChanged {
											marginDown.value = (-10).dp
										}
								)
							}
						},
						selected = currentTab == screen,
						alwaysShowLabel = true,
						onClick = {
							when {
								screen != currentTab -> appBarViewModel.onChangeTab(screen)
								screen == AppTab.Events -> eventsResetTask.run()
								screen == AppTab.Projects -> projectsResetTask.run()
								screen == AppTab.Devices -> devicesResetTask.run()
								screen == AppTab.Employees -> employeesResetTask.run()
								screen == AppTab.Feedback -> feedbackResetTask.run()
								screen == AppTab.Profile -> profileResetTask.run()
							}
							if (screen != appTabNull) {

								appBarViewModel.onNavigate(screen.asScreen())


							}
						}
					)

				}

			//}
		}

	}
}