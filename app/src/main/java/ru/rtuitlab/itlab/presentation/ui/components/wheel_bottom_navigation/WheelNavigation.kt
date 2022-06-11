package ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
    pagesSize: IntArray,
    appTabsViewModel: AppTabsViewModel = singletonViewModel(),
    wheelNavigationViewModel: WheelNavigationViewModel = singletonViewModel(),
    onClickWheel: () -> Unit,
    content: @Composable (CustomWheelNavItem: @Composable (modifier: Modifier, indexOfTab: Int, sizeAppTabs: Int, onClick: () -> Unit, icon: @Composable () -> Unit, label: @Composable() (() -> Unit)?, selected: Boolean, alwaysShowLabel: Boolean) -> Unit, MutableList<AppTab>) -> Unit,

    ) {

    val appsPage by remember {
        mutableStateOf(
            appTabsViewModel.changePage(
                pagesSize,
                1
            ) as MutableList<AppTab>
        )
    }


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
		                            enabled = pagesSize.size > 1 && pagesSize[0] < appTabsViewModel.allAppTabsAccess().size,
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

                                        //changePage to the RIGHT
                                        if (swipeableState.direction > 0) {
                                            appsPage.clear()
                                            appsPage.addAll(
                                                appTabsViewModel.changePage(
                                                    pagesSize,
                                                    statePage - 1
                                                )
                                            )
                                        } else { //changePage to the LEFT
                                            appsPage.clear()

                                            appsPage.addAll(
                                                appTabsViewModel.changePage(
                                                    pagesSize,
                                                    statePage + 1
                                                )
                                            )
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


                            val customWheel: @Composable (
                                modifier: Modifier,
                                indexOfTab: Int,
                                sizeAppTabs: Int,
                                onClick: () -> Unit,
                                icon: @Composable () -> Unit,
                                label: @Composable (() -> Unit)?,
                                selected: Boolean,
                                alwaysShowLabel: Boolean,
                            ) -> Unit = { modifier,
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
                                    sizeAppTabs = sizeAppTabs,
                                    sizeNavWidth = sizeNavWidth.value,
                                    sizeNavHeight = sizeNavHeight.value,
                                    marginDown = marginDown,
                                    rotationPosition = rotationPosition,
                                    indexOfTab = indexOfTab,

                                    icon = icon,
                                    label = label,
                                    selected = selected,
                                    alwaysShowLabel = alwaysShowLabel,
                                    onClick = onClick
                                )
                            }

                            content(
                                customWheel,
                                appsPage
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
    SIZEVIEWNAVIGATION: Dp,
    sizeAppTabs: Int,
    indexTab: Int,
    sizeItemWidth: Dp
): Dp {


    return ((stateDirection * with(density) { rotationPosition.toDp().value }).dp               // for animation move
            + (((SIZEVIEWNAVIGATION.value / 2) / sizeAppTabs)                                                                                // margin left and right for first tab
            + (SIZEVIEWNAVIGATION.value / sizeAppTabs) * (indexTab)).dp                           // between tabs * num of tab
            - sizeItemWidth / 2)                                                                    // half of tab to the left
}

fun yCoordinate(
    shiftUp: Dp,

    SIZEVIEWNAVIGATION: Dp,
    sizeAppTabs: Int,
    xCoordinate: Dp,
    sizeItemWidth: Dp,
    marginDown: Dp,
    sizeNavWidth: Dp,

    ): Dp {

    return (shiftUp                                                 // shift from top navigation to necessary place
            + curve(
        //formula for circle
        xCoordinate + sizeItemWidth / 2,             // between tabs * num of tab
        SIZEVIEWNAVIGATION,
        sizeAppTabs,
        marginDown,
        sizeNavWidth / 2,
        sizeNavWidth / 2,
    )
            )
}

fun curve(
    positionXInParent: Dp,
    SIZEVIEWNAVIGATION: Dp,
    sizeAppTabs: Int,
    marginDown: Dp,
    centerXInParent: Dp,

    radius: Dp

): Dp {
    val x = (positionXInParent.value - centerXInParent.value)
    val radius2 = (radius.value) * (radius.value) * 1.6f
    var size = sizeAppTabs
    if (sizeAppTabs % 2 == 1)
        size -= 1
    val oddValue = ((SIZEVIEWNAVIGATION.value / 2) / size)
    val offsetoddValueX = (oddValue - centerXInParent.value)
    var offsetoddValue = 0f

    if (radius2 - offsetoddValueX * offsetoddValueX > 0)
        offsetoddValue = -sqrt(radius2 - offsetoddValueX * offsetoddValueX)

    val offset = (-offsetoddValue).dp + marginDown

    var y = 0f
    if (radius2 - x * x > 0)
        y = -sqrt(radius2 - x * x)

    return (y + offset.value).dp


}


enum class DirectionWheelNavigation {
    Left, Center, Right
}