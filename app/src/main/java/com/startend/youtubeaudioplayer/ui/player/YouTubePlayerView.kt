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
                    })
                }
            },
            modifier = Modifier.height(0.dp)
        )
    }
}