package ru.rtuitlab.itlab.ui.shared.top_app_bars

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout

const val heightDelta = 66
/**
 * How to use this monstrosity:
 *
 * Use [CollapsibleTopAppBar] as you would any other AppBar, but provide it with a [ru.rtuitlab.itlab.ui.shared.SearchBar] and a [SwipeableState] of [SwipingStates]
 *
 * Your last(!) scrollable child must be [CollapsibleScrollArea], also having access to a [SwipeableState]
 * @sample ru.rtuitlab.itlab.ui.screens.events.Events
 * TODO: As of now its height is hardcoded to 168dp, but it will have to be fixed. I'm too tired for that
 */
@ExperimentalMotionApi
@ExperimentalMaterialApi
@Composable
fun CollapsibleTopAppBar(
	title: String,
	options: List<AppBarOption>,
	onBackAction: () -> Unit = emptyBackAction,
	hideBackButton: Boolean = false,
	hideOptions: Boolean = false,
	searchActivated: Boolean = false,
	swipingState: SwipeableState<SwipingStates>,
	searchBar: @Composable () -> Unit = {},
	content: @Composable (modifier: Modifier) -> Unit
) {

	val height by animateDpAsState(
		targetValue = 102.dp + heightDelta.dp *
				with(LocalDensity.current) {
					(swipingState.offset.value.toDp() / heightDelta).toPx() / this.density
				}
	)
	Surface(
		color = MaterialTheme.colors.primarySurface,
		contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
		elevation = AppBarDefaults.TopAppBarElevation,
		shape = RectangleShape
	) {
		MotionLayout(
			start = StartConstraintSet(),
			end = EndConstraintSet(),
			progress = if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction else 1f - swipingState.progress.fraction,
			modifier = Modifier
				.fillMaxWidth()
				.height(height)
		) {
			Text(
				text = title,
				modifier = Modifier
					.layoutId("title")
					.wrapContentWidth(unbounded = true)
					.wrapContentHeight()
					.scale(motionInt("title", "fontSize") / 32f)
					.alpha(if (searchActivated) motionFloat("title", "alpha") else 1f),
				color = MaterialTheme.colors.onSurface,
				fontWeight = FontWeight(motionInt("title", "fontWeight")),
				fontSize = 32.sp
			)
			Box(
				modifier = Modifier
					.layoutId("options")
					.fillMaxWidth()
					.height(36.dp),
				contentAlignment = Alignment.BottomEnd
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					if (onBackAction != emptyBackAction && !hideBackButton) {
						IconButton(onClick = onBackAction) {
							Icon(Icons.Default.ArrowBack, contentDescription = null)
						}
						Spacer(modifier = Modifier.width(16.dp))
					}
					if (searchActivated)
						searchBar()
				}
				if (!hideOptions)
						OptionsRow(
							options = options,
							modifier = Modifier
								.fillMaxWidth()
								.padding(end = 16.dp)
						)

			}

			content(
				Modifier
					.layoutId("content")
			)

		}
	}
}

@Composable
private fun StartConstraintSet() = ConstraintSet(
	""" {
	title: {
		top: ['parent', 'top'],
		start: ['parent', 'start'],
		end: ['parent', 'end'],
		bottom: ['content', 'top', 24],
		custom: {
			fontSize: 32,
			fontWeight: 400,
			alpha: 1
		}
	},
	options: { 
		end: ['parent', 'end', 0],
		bottom: ['content', 'top', 15]
	},
	content: {
		bottom: ['parent', 'bottom', 0]
	}
} """
)

@Composable
private fun EndConstraintSet() = ConstraintSet(
	""" {
	title: {
		top: ['parent', 'top', 0],
		start: ['parent', 'start', 0],
		bottom: ['content', 'top', 0],
		custom: {
			fontSize: 20,
			fontWeight: 500,
			alpha: 0
        }
	},
	options: { 
		end: ['parent', 'end', 0],
		bottom: ['content', 'top', 0],
		top: ['parent', 'top', 0]
	},
	content: {
		bottom: ['parent', 'bottom', 0]
	}
                  
} """
)


@ExperimentalMaterialApi
@Composable
fun CollapsibleScrollArea(
	swipingState: SwipeableState<SwipingStates>,
	content: @Composable () -> Unit
) {

	val connection = remember {
		object : NestedScrollConnection {

			override fun onPreScroll(
				available: Offset,
				source: NestedScrollSource
			): Offset {
				val delta = available.y
				return if (delta < 0) {
					swipingState.performDrag(delta).toOffset()
				} else {
					Offset.Zero
				}
			}

			override fun onPostScroll(
				consumed: Offset,
				available: Offset,
				source: NestedScrollSource
			): Offset {
				val delta = available.y
				return swipingState.performDrag(delta).toOffset()
			}

			override suspend fun onPostFling(
				consumed: Velocity,
				available: Velocity
			): Velocity {
				swipingState.performFling(velocity = available.y)
				return super.onPostFling(consumed, available)
			}

			private fun Float.toOffset() = Offset(0f, this)
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.swipeable(
				state = swipingState,
				thresholds = { _, _ -> FractionalThreshold(0.1f) },
				orientation = Orientation.Vertical,
				anchors = mapOf(
					0f to SwipingStates.COLLAPSED,
					with(LocalDensity.current) { heightDelta.dp.toPx() } to SwipingStates.EXPANDED,
				)
			)
			.nestedScroll(connection)
	) {
		content()
	}

}

enum class SwipingStates {
	EXPANDED, COLLAPSED
}
