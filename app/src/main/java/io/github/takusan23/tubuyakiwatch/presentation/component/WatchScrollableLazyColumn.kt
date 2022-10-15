package io.github.takusan23.tubuyakiwatch.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.wear.compose.material.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow


/**
 * Pixel Watch のつまみでスクロールできる LazyColumn
 */
@Composable
fun WatchScrollableLazyColumn(
    modifier: Modifier = Modifier,
    listState: ScalingLazyListState = rememberScalingLazyListState(),
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    reverseLayout: Boolean = false,
    autoCentering: AutoCenteringParams = AutoCenteringParams(),
    content: ScalingLazyListScope.() -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val flingBehavior = ScrollableDefaults.flingBehavior()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    ScalingLazyColumn(
        modifier = modifier.rsbScroll(
            scrollableState = listState,
            flingBehavior = flingBehavior,
            focusRequester = focusRequester
        ),
        state = listState,
        reverseLayout = reverseLayout,
        scalingParams = scalingParams,
        flingBehavior = flingBehavior,
        autoCentering = autoCentering,
        content = content
    )
}

private data class TimestampedDelta(val time: Long, val delta: Float)

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("ModifierInspectorInfo")
@Suppress("ComposableModifierFactory")
@Composable
fun Modifier.rsbScroll(
    scrollableState: ScrollableState,
    flingBehavior: FlingBehavior,
    focusRequester: FocusRequester,
): Modifier {
    val channel = remember {
        Channel<TimestampedDelta>(
            capacity = 10,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    var lastTimeMillis = remember { 0L }
    var smoothSpeed = remember { 0f }
    val speedWindowMillis = 200L
    val timeoutToFling = 100L

    return composed {
        var rsbScrollInProgress by remember { mutableStateOf(false) }
        LaunchedEffect(rsbScrollInProgress) {
            if (rsbScrollInProgress) {
                scrollableState.scroll(MutatePriority.UserInput) {
                    channel.receiveAsFlow().collectLatest {
                        val toScroll = if (lastTimeMillis > 0L && it.time > lastTimeMillis) {
                            val timeSinceLastEventMillis = it.time - lastTimeMillis

                            // Speed is in pixels per second.
                            val speed = it.delta * 1000 / timeSinceLastEventMillis
                            val cappedElapsedTimeMillis =
                                timeSinceLastEventMillis.coerceAtMost(speedWindowMillis)
                            smoothSpeed = ((speedWindowMillis - cappedElapsedTimeMillis) * speed +
                                    cappedElapsedTimeMillis * smoothSpeed) / speedWindowMillis
                            smoothSpeed * cappedElapsedTimeMillis / 1000
                        } else {
                            0f
                        }
                        lastTimeMillis = it.time
                        scrollBy(toScroll)

                        // If more than the given time pass, start a fling.
                        delay(timeoutToFling)

                        lastTimeMillis = 0L

                        if (smoothSpeed != 0f) {
                            val launchSpeed = smoothSpeed
                            smoothSpeed = 0f
                            with(flingBehavior) {
                                performFling(launchSpeed)
                            }
                            rsbScrollInProgress = false
                        }
                    }
                }
            }
        }
        this
            .onRotaryScrollEvent {
                channel.trySend(TimestampedDelta(it.uptimeMillis, it.verticalScrollPixels))
                rsbScrollInProgress = true
                true
            }
            .focusRequester(focusRequester)
            .focusable()
    }
}