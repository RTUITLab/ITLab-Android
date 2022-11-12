package ru.rtuitlab.itlab.presentation.screens.events.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme

@Preview
@Composable
fun SegmentedPreview() {
    ITLabTheme {
        Surface {
            Column(Modifier.padding(16.dp), verticalArrangement = spacedBy(16.dp)) {
                Text("SEGMENTS", style = MaterialTheme.typography.labelMedium)

                val twoSegments = remember { listOf("Foo", "Bar") }
                var selectedTwoSegment by remember { mutableStateOf(twoSegments.first()) }
                SegmentedControl(
                    twoSegments,
                    selectedTwoSegment,
                    onSegmentSelected = { selectedTwoSegment = it }
                ) {
                    SegmentText(
                        modifier = Modifier.padding(10.dp),
                        text = it,
                        selected = selectedTwoSegment == it
                    )
                }

                val threeSegments = remember { listOf("Participant", "Организатор", "Intern") }
                var selectedThreeSegment by remember { mutableStateOf(threeSegments[1]) }
                SegmentedControl(
                    threeSegments,
                    selectedThreeSegment,
                    onSegmentSelected = { selectedThreeSegment = it }
                ) {
                    SegmentText(
                        modifier = Modifier.padding(10.dp),
                        text = it,
                        selected = selectedThreeSegment == it
                    )
                }

                val fourSegments = remember { listOf("Very long string that cannot be here", "Foo", "Bar", "Baz") }
                var selectedFourSegment by remember { mutableStateOf(fourSegments[0]) }
                SegmentedControl(
                    segments = fourSegments,
                    selectedSegment = selectedFourSegment,
                    onSegmentSelected = { selectedFourSegment = it }
                ) {
                    SegmentText(
                        modifier = Modifier.padding(10.dp),
                        text = it,
                        selected = selectedThreeSegment == it
                    )
                }
            }
        }
    }
}

private const val NO_SEGMENT_INDEX = -1

/** Padding inside the track. */
private val TRACK_PADDING = 0.dp

/** Additional padding to inset segments and the thumb when pressed. */
private val PRESSED_TRACK_PADDING = 1.dp

/** Padding inside individual segments. */
private val SEGMENT_PADDING = 2.dp

/** Alpha to use to indicate pressed state when unselected segments are pressed. */
private const val PRESSED_UNSELECTED_ALPHA = .6f

@Composable
fun <T : Any> SegmentedControl(
    segments: List<T>,
    selectedSegment: T,
    onSegmentSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    val state = remember { SegmentedControlState() }
    state.segmentCount = segments.size
    state.selectedSegment = segments.indexOf(selectedSegment)
    state.onSegmentSelected = { onSegmentSelected(segments[it]) }

    var isThumbOffsetDefined by remember { mutableStateOf(false) }
    var isThumbWidthDefined by remember { mutableStateOf(false) }

    val thumbOffset by animateIntAsState(
        targetValue = state.segmentsWidths.sumSlice(0, state.selectedSegment),
        finishedListener = {
            isThumbOffsetDefined = true
        }
    )
    val thumbWidth by animateIntAsState(
        targetValue = state.segmentsWidths.getOrNull(state.selectedSegment) ?: 0,
        finishedListener = {
            isThumbWidthDefined = true
        }
    )


    // Use a custom layout so that we can measure the thumb using the height of the segments. The thumb
    // is whole composable that draws itself – this layout is just responsible for placing it under
    // the correct segment.
    Layout(
        content = {
            // Each of these produces a single measurable.
            Segments(state, segments, content)
            Dividers(state)
            Thumb(state)
        },
        modifier = modifier
            .fillMaxWidth()
            .then(state.inputModifier)
            .background(
                color = Color.Transparent,
                shape = MaterialTheme.shapes.extraLarge
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(TRACK_PADDING)
    ) { measurables, constraints ->
        val (segmentsMeasurable, dividersMeasurable, thumbMeasurable) = measurables

        // Measure the segments first so we know how tall to make the thumb.
        val segmentsPlaceable = segmentsMeasurable.measure(constraints)
        state.updatePressedScale(segmentsPlaceable.height, this)

        // Now we can measure the thumb and dividers to be the right size.
        val thumbPlaceable = thumbMeasurable.measure(
            Constraints.fixed(
                width = if (isThumbWidthDefined) thumbWidth else state.segmentsWidths[state.selectedSegment],
                height = segmentsPlaceable.height
            )
        )
        val dividersPlaceable = dividersMeasurable.measure(
            Constraints.fixed(
                width = segmentsPlaceable.width,
                height = segmentsPlaceable.height
            )
        )

        layout(segmentsPlaceable.width, segmentsPlaceable.height) {
            segmentsPlaceable.placeRelative(IntOffset.Zero)
            thumbPlaceable.placeRelative(
                x = if (isThumbOffsetDefined) thumbOffset else state.segmentsWidths.sumSlice(0, state.selectedSegment),
                y = 0
            )
            dividersPlaceable.placeRelative(IntOffset.Zero)
        }
    }
}

/**
 * Dictates how [SegmentedControl] distributes segments.
 * @see [EQUAL]
 * @see [SIZE_AWARE]
 */
// This may be needed in the future to provide customization for SegmentedControl
enum class SegmentsDistributionPolicy {
    /**
     * Segmented control will distribute children evenly without adjusting for their true size
     */
    EQUAL,

    /**
     * Segmented control will distribute children based on their true size, i.e.:
     * - Some of the segments may be smaller than others to provide more space for those which need it
     * - If there is no way to distribute segments such that every one's content is fully visible,
     * [SegmentedControl] will fallback to [EQUAL]
     */
    SIZE_AWARE
}

/**
 * Wrapper around [Text] that is configured to display appropriately inside of a [SegmentedControl].
 */
@Composable
fun SegmentText(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    val color by animateColorAsState(
        targetValue = if (selected) selectedColor else unselectedColor
    )
    Text(
        modifier = modifier,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color,
        style = style
    )
}

/**
 * Draws the thumb (selected indicator) on a [SegmentedControl] track, underneath the [Segments].
 */
@Composable
private fun Thumb(state: SegmentedControlState) {

    val width by animateDpAsState(
        targetValue = with(LocalDensity.current) {
            state.segmentsWidths.getOrNull(state.selectedSegment)?.toDp() ?: 0.dp
        }
    )

    Box(
        Modifier
            .width(width)
            .then(
                state.segmentScaleModifier(
                    pressed = state.pressedSegment == state.selectedSegment,
                    segment = state.selectedSegment
                )
            )
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = .15f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
    )
}

/**
 * Draws dividers between segments. No dividers are drawn around the selected segment.
 */
@Composable
private fun Dividers(state: SegmentedControlState) {
    // Animate each divider independently.
    val alphas = (0 until state.segmentCount).map { i ->
        val selectionAdjacent = i == state.selectedSegment || i - 1 == state.selectedSegment
        animateFloatAsState(if (selectionAdjacent) 0f else 1f)
    }

    val color = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(Modifier.fillMaxSize()) {
        val segmentWidths = state.segmentsWidths
        val dividerPadding = TRACK_PADDING + PRESSED_TRACK_PADDING

        var x = 0f
        alphas.forEachIndexed { i, alpha ->
            drawLine(
                color = color,
                alpha = alpha.value,
                start = Offset(x, dividerPadding.toPx()),
                end = Offset(x, size.height - dividerPadding.toPx())
            )
            x += segmentWidths[i]
        }
    }
}

/**
 * Draws the actual segments in a [SegmentedControl].
 */
@Composable
private fun <T> Segments(
    state: SegmentedControlState,
    segments: List<T>,
    content: @Composable (T) -> Unit
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontWeight = FontWeight.Medium)
    ) {

        LaunchedEffect(state) {
            // Clear widths before adding new ones
            state.segmentsWidths.clear()
        }

        Row(
            horizontalArrangement = spacedBy(TRACK_PADDING),
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
        ) {
            Layout(
                content = {
                    segments.forEachIndexed { i, segment ->
                        val isSelected = i == state.selectedSegment
                        val isPressed = i == state.pressedSegment

                        // Unselected presses are represented by fading.
                        val alpha by animateFloatAsState(if (!isSelected && isPressed) PRESSED_UNSELECTED_ALPHA else 1f)

                        val semanticsModifier = Modifier.semantics(mergeDescendants = true) {
                            selected = isSelected
                            role = Role.Button
                            onClick { state.onSegmentSelected(i); true }
                            stateDescription = if (isSelected) "Selected" else "Not selected"
                        }

                        Box(
                            Modifier
                                .then(semanticsModifier)
                                .padding(SEGMENT_PADDING)
                                // Draw pressed indication when not selected.
                                .alpha(alpha)
                                // Selected presses are represented by scaling.
                                .then(state.segmentScaleModifier(isPressed && isSelected, i))
                                // Center the segment content.
                                .wrapContentWidth()
                        ) {
                            content(segment)
                        }
                    }
                }
            ) { measurables, constraints ->

                // Using intrinsic measurements to obtain measurable`s desired width
                val widths = measurables.map { it.maxIntrinsicWidth(constraints.maxHeight) }
                val totalWidth = widths.sum()
                val placeables: List<Placeable>

                // Segments do not fit inside the container - distribute them evenly
                if (totalWidth > constraints.maxWidth) {
                    val width = constraints.maxWidth / measurables.size
                    placeables = measurables.map {
                        it.measure(
                            constraints = constraints.copy(
                                minWidth = width,
                                maxWidth = width
                            )
                        )
                    }
                } else { // Segments can be fit to display all content - distribute them accordingly
                    // Leftover space when all measurables have desired width
                    val remainder = (constraints.maxWidth - totalWidth) / measurables.size
                    placeables = measurables.mapIndexed { index, measurable ->
                        val width = widths[index] + remainder
                        measurable.measure(
                            constraints = constraints.copy(
                                minWidth = width,
                                maxWidth = width
                            )
                        )
                    }
                }

                state.segmentsWidths.addAll(
                    placeables.map {
                        it.width
                    }
                )

                layout(
                    width = constraints.maxWidth,
                    height = placeables[0].height
                ) {
                    var xOffset = 0
                    placeables.forEach {
                        it.placeRelative(
                            x = xOffset,
                            y = 0
                        )
                        xOffset += it.width
                    }
                }
            }
        }
    }
}

private class SegmentedControlState {
    var segmentCount by mutableStateOf(0)
    var selectedSegment by mutableStateOf(0)
    var onSegmentSelected: (Int) -> Unit by mutableStateOf({})
    var pressedSegment by mutableStateOf(NO_SEGMENT_INDEX)

    val segmentsWidths = mutableStateListOf<Int>()

    /**
     * Scale factor that should be used to scale pressed segments (both the segment itself and the
     * thumb). When this scale is applied, exactly [PRESSED_TRACK_PADDING] will be added around the
     * element's usual size.
     */
    var pressedSelectedScale by mutableStateOf(1f)
        private set

    /**
     * Calculates the scale factor we need to use for pressed segments to get the desired padding.
     */
    fun updatePressedScale(controlHeight: Int, density: Density) {
        with(density) {
            val pressedPadding = PRESSED_TRACK_PADDING * 2
            val pressedHeight = controlHeight - pressedPadding.toPx()
            pressedSelectedScale = pressedHeight / controlHeight
        }
    }

    /**
     * Returns a [Modifier] that will scale an element so that it gets [PRESSED_TRACK_PADDING] extra
     * padding around it. The scale will be animated.
     *
     * The scale is also performed around either the left or right edge of the element if the [segment]
     * is the first or last segment, respectively. In those cases, the scale will also be translated so
     * that [PRESSED_TRACK_PADDING] will be added on the left or right edge.
     */
    @SuppressLint("ModifierFactoryExtensionFunction")
    fun segmentScaleModifier(
        pressed: Boolean,
        segment: Int,
    ): Modifier = Modifier.composed {
        val scale by animateFloatAsState(if (pressed) pressedSelectedScale else 1f)
        val xOffset by animateDpAsState(if (pressed) PRESSED_TRACK_PADDING else 0.dp)

        graphicsLayer {
            this.scaleX = scale
            this.scaleY = scale

            // Scales on the ends should gravitate to that edge.
            this.transformOrigin = TransformOrigin(
                pivotFractionX = when (segment) {
                    0 -> 0f
                    segmentCount - 1 -> 1f
                    else -> .5f
                },
                pivotFractionY = .5f
            )

            // But should still move inwards to keep the pressed padding consistent with top and bottom.
            this.translationX = when (segment) {
                0 -> xOffset.toPx()
                segmentCount - 1 -> -xOffset.toPx()
                else -> 0f
            }
        }
    }

    /**
     * A [Modifier] that will listen for touch gestures and update the selected and pressed properties
     * of this state appropriately.
     *
     * Input will be reset if the [segmentCount] changes.
     */
    val inputModifier = Modifier.pointerInput(segmentCount) {
        // Helper to calculate which segment an event occurred in.
        fun segmentIndex(change: PointerInputChange) =
            segmentsWidths.sumUntil { it > change.position.x } - 1
                .coerceIn(0, segmentCount - 1)

        awaitEachGesture {
            val down = awaitFirstDown()

            pressedSegment = segmentIndex(down)
            val downOnSelected = pressedSegment == selectedSegment
            val segmentBounds = Rect(
                left = segmentsWidths.sumSlice(0, pressedSegment).toFloat(),
                right = segmentsWidths.sumSlice(0, pressedSegment + 1).toFloat(),
                top = 0f,
                bottom = size.height.toFloat()
            )

            // Now that the pointer is down, the rest of the gesture depends on whether the segment that
            // was "pressed" was selected.
            if (downOnSelected) {
                // When the selected segment is pressed, it can be dragged to other segments to animate the
                // thumb moving and the segments scaling.
                horizontalDrag(down.id) { change ->
                    pressedSegment = segmentIndex(change)

                    // Notify the SegmentedControl caller when the pointer changes segments.
                    if (pressedSegment != selectedSegment) {
                        onSegmentSelected(pressedSegment)
                    }
                }
            } else {
                // When an unselected segment is pressed, we just animate the alpha of the segment while
                // the pointer is down. No dragging is supported.
                waitForUpOrCancellation(inBounds = segmentBounds)
                    // Null means the gesture was cancelled (e.g. dragged out of bounds).
                    ?.let { onSegmentSelected(pressedSegment) }
            }

            // In either case, once the gesture is cancelled, stop showing the pressed indication.
            pressedSegment = NO_SEGMENT_INDEX
        }
    }
}

/**
 * Copy of nullary waitForUpOrCancellation that works with bounds that may not be at 0,0.
 */
private suspend fun AwaitPointerEventScope.waitForUpOrCancellation(inBounds: Rect): PointerInputChange? {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Main)
        if (event.changes.all { it.changedToUp() }) {
            // All pointers are up
            return event.changes[0]
        }

        if (event.changes.any { it.isConsumed || !inBounds.contains(it.position) }) {
            return null // Canceled
        }

        // Check for cancel by position consumption. We can look on the Final pass of the
        // existing pointer event because it comes after the Main pass we checked above.
        val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
        if (consumeCheck.changes.any { it.isConsumed }) {
            return null
        }
    }
}

/**
 * Helper function to animate thumb offset. Sums all values
 * from [start], inclusive until [end], exclusive
 */
private fun List<Int>.sumSlice(start: Int, end: Int): Int {
    if (this.isEmpty()) return -1
    var sum = 0
    for (i in start until end) {
        sum += this[i]
    }
    return sum
}

/**
 * Helper function to find what segment was pressed. Sums values
 * until [condition] is met, and returns an index of element that
 * caused it to be true
 */
private fun List<Int>.sumUntil(condition: (Int) -> Boolean): Int {
    if (this.isEmpty()) return 0
    var sum = 0
    var index = 0
    for (i in this.indices) {
        if (condition(sum)) {
            index = i
            break
        }
        sum += this[i]
    }
    return index
}