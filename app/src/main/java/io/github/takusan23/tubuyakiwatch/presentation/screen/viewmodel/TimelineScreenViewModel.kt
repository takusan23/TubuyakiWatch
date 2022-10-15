package io.github.takusan23.tubuyakiwatch.presentation.screen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.tubuyakiwatch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.Twitter
import twitter4j.v1.Paging
import twitter4j.v1.Status

class TimelineScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val twitter by lazy {
        // コンシューマキーがもれないように、バージョン管理対象外のリソースから取得
        Twitter.newBuilder()
            .oAuthConsumer(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret))
            .oAuthAccessToken(context.getString(R.string.token), context.getString(R.string.token_secret))
            .build()
    }

    private val _timeline = MutableStateFlow<List<Status>>(listOf())
    val timeline = _timeline.asStateFlow()

    init {
        viewModelScope.launch {
            getTimeline()
        }
    }

    private suspend fun getTimeline() = withContext(Dispatchers.IO) {
//        _timeline.value = twitter.v1().timelines().getUserTimeline(1001123359487180807)
        _timeline.value = twitter.v1().timelines().getHomeTimeline(Paging.ofCount(100))
    }

}