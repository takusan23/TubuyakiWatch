/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package io.github.takusan23.tubuyakiwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import io.github.takusan23.tubuyakiwatch.presentation.screen.TimelineScreen
import io.github.takusan23.tubuyakiwatch.presentation.theme.TubuyakiWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    TubuyakiWatchTheme {
        val controller = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = controller, startDestination = "timeline") {
            composable("timeline"){
                TimelineScreen()
            }
        }
    }
}