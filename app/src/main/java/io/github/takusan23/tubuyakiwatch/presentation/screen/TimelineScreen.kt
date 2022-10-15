package io.github.takusan23.tubuyakiwatch.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import io.github.takusan23.tubuyakiwatch.presentation.component.TimelineItem
import io.github.takusan23.tubuyakiwatch.presentation.component.WatchScrollableLazyColumn
import io.github.takusan23.tubuyakiwatch.presentation.screen.viewmodel.TimelineScreenViewModel

/** タイムライン画面 */
@Composable
fun TimelineScreen(viewModel: TimelineScreenViewModel = viewModel()) {
    val listState = rememberScalingLazyListState()
    val timeline = viewModel.timeline.collectAsState()

    Scaffold(timeText = { TimeText() }, vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }, positionIndicator = { PositionIndicator(scalingLazyListState = listState) }) {
        WatchScrollableLazyColumn(
            modifier = Modifier.fillMaxSize(),
            autoCentering = AutoCenteringParams(itemIndex = 0),
            listState = listState,
        ) {
            if (timeline.value.isEmpty()) {
                item { LoadingScreen() }
            } else {
                items(timeline.value) { tweet ->
                    TimelineItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        status = tweet
                    )
                }
            }
        }
    }
}