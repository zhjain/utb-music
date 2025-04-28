package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

@Composable
fun YouTubePlayerView(
    youTubePlayerState: MutableState<YouTubePlayer?>,
    onStateChange: (PlayerConstants.PlayerState) -> Unit,
    onCurrentSecond: (Float) -> Unit,
    onDuration: (Float) -> Unit,
    videoId: String
) {
    // 使用key参数确保videoId变化时重新创建视图
    Box(
        modifier = Modifier
            .height(0.dp)
            .width(0.dp)
    ) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayerState.value = youTubePlayer
                            youTubePlayer.loadVideo(videoId, 0f)
                        }

                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                            onCurrentSecond(second)
                        }

                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                            onDuration(duration)
                        }

                        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                            onStateChange(state)
                        }

                        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                            // 视频ID变化时的处理
                            super.onVideoId(youTubePlayer, videoId)
                        }
                    })
                }
            },
            update = { view ->
                // 当videoId变化时，更新播放的视频
                youTubePlayerState.value?.let { player ->
                    // 直接加载新视频
                    player.loadVideo(videoId, 0f)
                }
            },
            modifier = Modifier.height(0.dp)
        )
    }
}