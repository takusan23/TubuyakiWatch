package io.github.takusan23.tubuyakiwatch.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import twitter4j.v1.Status
import java.time.format.DateTimeFormatter

/** ツイートを表示するカード */
@Composable
fun TimelineItem(
    modifier: Modifier = Modifier,
    status: Status,
) {
    AppCard(
        modifier = modifier,
        appImage = {
            AsyncImage(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50)),
                model = status.user.profileImageURLHttps,
                contentDescription = null
            )
        },
        appName = {
            Text(
                text = "@${status.user.screenName}",
                fontSize = 12.sp,
                maxLines = 1
            )
        },
        time = {/* do nothing */ },
        title = {
            Text(
                text = status.user.name,
                maxLines = 1
            )
        },
        onClick = {
            // do nothing
        }
    ) {
        Text(text = status.text)
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            modifier = Modifier.align(Alignment.End),
            text = status.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
            maxLines = 1,
            fontSize = 12.sp
        )
    }
}