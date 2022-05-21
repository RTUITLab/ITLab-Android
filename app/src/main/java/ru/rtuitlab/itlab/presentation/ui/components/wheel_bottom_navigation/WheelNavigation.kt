package ru.rtuitlab.itlab.presentation.ui.components.wheel_bottom_navigation

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.serialization.ExperimentalSerializationApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.CustomWheelNavigation
import ru.rtuitlab.itlab.presentation.utils.singletonViewModel
import kotlin.math.sqrt

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalStdlibApi
@Composable
fun WheelNavigation(
	wheelNavigationViewModel: WheelNavigationViewModel = singletonViewModel(),
	modifier: Modifier,
	onClickWheel: () -> Unit,
	marginDown: Dp,
	content: @Composable () -> Unit,
) {

	val isVisible by wheelNavigationViewModel.currentState.collectAsState()



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
					Column(modifier = Modifier
						.align(Alignment.BottomCenter)
					){
						AnimatedVisibility(
							visible = isVisible,

							) {
							CustomWheelNavigation(
								modifier = modifier

							) {

								content()
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
						.offset(0.dp, marginDown / 2),
					onClick = {
						onClickWheel()
					}
				) {
					Icon(
						painter = painterResource(R.drawable.wheel),
						contentDescription = stringResource(R.string.rtuitlab),
						modifier = Modifier.graphicsLayer( alpha = alpha )

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