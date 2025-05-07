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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf

@Composable
fun YouTubePlayerView(
    youTubePlayerState: MutableState<YouTubePlayer?>,
    onStateChange: (PlayerConstants.PlayerState) -> Unit,
    onCurrentSecond: (Float) -> Unit,
    onDuration: (Float) -> Unit,
    videoId: String,
    onVideoEnded: () -> Unit
) {
    // 记住上一次报告的时间，避免频繁更新
    val lastReportedTime = remember { mutableFloatStateOf(0f) }
    // 使用防抖动机制减少更新频率
    val updateThreshold = 0.2f // 0.2秒的阈值
    
    // 记录是否正在进行seek操作
    val isSeeking = remember { mutableStateOf(false) }
    val pendingSeekTime = remember { mutableFloatStateOf(-1f) }
    
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
                            // 如果有待处理的seek操作，执行它
                            if (pendingSeekTime.floatValue >= 0) {
                                youTubePlayer.seekTo(pendingSeekTime.floatValue)
                                pendingSeekTime.floatValue = -1f
                                return
                            }
                            
                            // 只有当时间变化超过阈值时才更新UI
                            if (kotlin.math.abs(second - lastReportedTime.floatValue) >= updateThreshold) {
                                onCurrentSecond(second)
                                lastReportedTime.floatValue = second
                            }
                        }

                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                            onDuration(duration)
                        }

                        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                            onStateChange(state)
                            
                            // 检测视频结束状态
                            if (state == PlayerConstants.PlayerState.ENDED) {
                                onVideoEnded()
                            }
                        }
                    })
                }
            },
            update = { view ->
                // 当videoId变化时，更新播放的视频
                youTubePlayerState.value?.loadVideo(videoId, 0f)
            },
            modifier = Modifier.height(0.dp)
        )
    }
    
    // 提供一个函数来处理seek操作
    youTubePlayerState.value?.let { player ->
        LaunchedEffect(player) {
            // 这个效果只在player变化时运行一次
            // 实际上不做任何事情，只是确保player被正确初始化
        }
    }
}