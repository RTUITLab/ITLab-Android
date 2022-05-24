package ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.CustomWheelNavigation
import ru.rtuitlab.itlab.presentation.ui.components.CustomWheelNavigationItem
import ru.rtuitlab.itlab.presentation.ui.components.top_app_bars.AppTabsViewModel
import ru.rtuitlab.itlab.presentation.utils.AppTab
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel
import kotlin.math.sqrt

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalStdlibApi
@Composable
fun WheelNavigation(
	modifier: Modifier = Modifier,
	appTabsViewModel: AppTabsViewModel = singletonViewModel(),
	wheelNavigationViewModel: WheelNavigationViewModel = singletonViewModel(),
	onClickWheel: () -> Unit,
	content: @Composable (
	                      /*marginDown: MutableState<Dp>,*/

	                      CustomWheelNavItem:  @Composable (
		                      modifier:Modifier,
		                      indexOfTab:Int,

		                      sizeAppTabs:Int,
		                      onClick: () -> Unit,
		                      icon: @Composable () -> Unit,
		                      label: @Composable (() -> Unit)?,
		                      selected:Boolean,
		                      alwaysShowLabel: Boolean,


		                      ) -> Unit
	) -> Unit
) {
	val SIZEVIEWNAVIGATION = 300.dp

	val isVisible by wheelNavigationViewModel.currentState.collectAsState()

	val density = LocalDensity.current

	val marginDown = remember { mutableStateOf((-20).dp) }


	//three anchors for infinity sliding
	val anchors = mapOf(
		with(density) { -SIZEVIEWNAVIGATION.toPx() } to DirectionWheelNavigation.Left,
		0f to DirectionWheelNavigation.Center,
		with(density) { SIZEVIEWNAVIGATION.toPx() } to DirectionWheelNavigation.Right) // Maps anchor points (in px) to states

	val swipeableState = rememberSwipeableState(DirectionWheelNavigation.Center)

	//sizes bottomnavigation
	val sizeNavWidth = remember { mutableStateOf(0.dp) }
	val sizeNavHeight = remember { mutableStateOf(0.dp) }


	ConstraintLayout {
		AnimatedVisibility(
			visible = isVisible,
			enter = slideInVertically(initialOffsetY = { it -> it }),
			exit = slideOutVertically(targetOffsetY = { it -> it })
		) {

			Box() {
				Image(
					modifier = Modifier
						.align(Alignment.BottomCenter),
					painter = painterResource(R.drawable.bottom_navigation),
					contentDescription = "bottom",

					colorFilter = ColorFilter.lighting(
						MaterialTheme.colors.primarySurface,
						MaterialTheme.colors.primarySurface
					)
				)
				Column(
					modifier = Modifier
						.align(Alignment.BottomCenter)
				) {
					AnimatedVisibility(
						visible = isVisible,

						) {
						CustomWheelNavigation(
							modifier = modifier
								.width(SIZEVIEWNAVIGATION)
								.onSizeChanged {
									sizeNavWidth.value = with(density) {
										it.width.toDp()
									}
									sizeNavHeight.value = with(density) {
										it.height.toDp()
									}
								}
								.swipeable(
									state = swipeableState,
									anchors = anchors,
									thresholds = { _, _ -> FractionalThreshold(0.3f) },
									orientation = Orientation.Horizontal,
								)

						) {
							var currentDirectionState by remember { mutableStateOf(1) }

							val statePage = appTabsViewModel.statePage.collectAsState().value

							val coroutineScope = rememberCoroutineScope()

							val rotationPosition by animateFloatAsState(
								targetValue = if (swipeableState.targetValue != DirectionWheelNavigation.Center) with(
									density
								) { sizeNavWidth.value.toPx() } else 0f,
								animationSpec = tween(durationMillis = 250),
								finishedListener = {
									if (it == with(density) { sizeNavWidth.value.toPx() }) {
										if (statePage == 1) {
											appTabsViewModel.setSecondPage(coroutineScope)
										} else {
											appTabsViewModel.setFirstPage(coroutineScope)
										}
										//monitoring when elements is hiden
										//important reverse
										currentDirectionState =
											if (swipeableState.direction > 0) 2 else 0
										coroutineScope.launch {
											swipeableState.snapTo(DirectionWheelNavigation.Center)
										}
									}
								}
							)

							//to what side elements have to move -1 - on the left; 1 - on the right
							val stateDirection =
								(if ((currentDirectionState == 2 && swipeableState.progress.to == DirectionWheelNavigation.Center) || swipeableState.targetValue == DirectionWheelNavigation.Left) -1 else 1)

							val (offsetY, setOffsetY) = remember { mutableStateOf(0.dp) }
							val (firstTime, setFirstTime) = remember { mutableStateOf(0) }

							val oddValue = appTabsViewModel.oddValue.collectAsState().value


							val customWheel: @Composable (
								modifier:Modifier,
								indexOfTab:Int,
								sizeAppTabs:Int,
								onClick: () -> Unit,
								icon: @Composable () -> Unit,
								label: @Composable (() -> Unit)?,
								selected:Boolean,
								alwaysShowLabel: Boolean,
							) -> Unit = {
									modifier,
									indexOfTab,
									sizeAppTabs,
									onClick,
									icon,
									label,
									selected,
									alwaysShowLabel ->
							CustomWheelNavigationItem(

								modifier = modifier,
								stateDirection = stateDirection,
								oddValue = oddValue,
								sizeAppTabs = sizeAppTabs,
								sizeNavWidth = sizeNavWidth.value,
								sizeNavHeight = sizeNavHeight.value,
								marginDown = marginDown,
								rotationPosition = rotationPosition,
								indexOfTab = indexOfTab,
								offsetY = offsetY,
								setOffsetY = setOffsetY,
								firstTime = firstTime,
								setFirstTime = setFirstTime,
								icon = icon,
								label = label,
								selected = selected,
								alwaysShowLabel = alwaysShowLabel,
								onClick = onClick
							)
						}

							content(

								/*marginDown,
						*/
								customWheel
							)
						}
					}
				}
			}
		}
		val (bottomnav) = createRefs()
		val alpha: Float by animateFloatAsState(if (isVisible) 1f else 0.5f)

		Column(modifier = Modifier
			.constrainAs(bottomnav) {
				bottom.linkTo(parent.bottom)
				centerHorizontallyTo(parent)
			}) {
			IconButton(
				modifier = Modifier
					.size(40.dp, 40.dp)
					.offset(0.dp, marginDown.value / 2),
				onClick = {
					onClickWheel()
				}
			) {
				Icon(
					painter = painterResource(R.drawable.wheel),
					contentDescription = stringResource(R.string.rtuitlab),
					modifier = Modifier.graphicsLayer(alpha = alpha)

				)
			}
		}
	}


}




fun xCoordinate(
	stateDirection: Int,
	density: Density,
	rotationPosition: Float,
	oddValue: Float,
	SIZEVIEWNAVIGATION: Dp,
	sizeAppTabs: Int,
	indexTab: Int,
	sizeItemWidth: Dp
): Dp {
	return ((stateDirection * with(density) { rotationPosition.toDp().value }).dp               // for animation move
			+ (oddValue                                                                                 // margin left and right for first tab
			+ (SIZEVIEWNAVIGATION.value / sizeAppTabs) * indexTab).dp                           // between tabs * num of tab
			- sizeItemWidth / 2)                                                                    // half of tab to the left
}

fun yCoordinate(
	shiftUp: Dp,
	xCoordinate: Dp,
	sizeItemWidth: Dp,
	marginDown: Dp,
	sizeNavWidth: Dp,
	sizeNavHeight: Dp,
	sizeAppTabs: Int,
	indexOfTab: Int,
	setOffsetY: (Dp) -> Unit,
	offsetY: Dp,
	setFirstTime: (Int) -> Unit,
	firstTime: Int
): Dp {
	return (shiftUp                                                 // shift from top navigation to necessary place
			+ curve(                                                        //formula for circle
		xCoordinate + sizeItemWidth / 2,             // between tabs * num of tab
		marginDown,
		sizeNavWidth / 2,
		sizeNavHeight.plus(sizeNavWidth / 2),
		sizeAppTabs,
		indexOfTab,
		sizeNavWidth / 2,
		setOffsetY,
		offsetY,
		setFirstTime,
		firstTime
	)
			)
}

fun curve(
	positionXInParent: Dp,
	marginDown: Dp,
	centerXInParent: Dp,
	centerYInParent: Dp,
	howmany: Int,
	index: Int,
	radius: Dp,
	setOffsetY: (Dp) -> Unit,
	offsetY: Dp,
	setFirstTime: (Int) -> Unit,
	firstTime: Int
): Dp {

	val x = (positionXInParent.value - centerXInParent.value)
	val radius2 = (radius.value) * (radius.value) * 1.6f

	var y = 0f
	if (radius2 - x * x > 0)
		y = -sqrt(radius2 - x * x)
	return if (index == 0) {
		if (firstTime < 2) {
			setOffsetY((-y).dp + marginDown)
			setFirstTime(firstTime + 1)
			marginDown
		} else {
			(y + offsetY.value).dp
		}
	} else {
		(y + offsetY.value).dp
	}


}


enum class DirectionWheelNavigation {
	Left, Center, Right
}