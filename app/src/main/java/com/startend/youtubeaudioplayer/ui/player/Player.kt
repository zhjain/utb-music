package com.startend.youtubeaudioplayer.ui.player

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import com.startend.youtubeaudioplayer.data.VideoInfo
import com.startend.youtubeaudioplayer.service.YouTubeService

@Composable
fun Player(
    modifier: Modifier = Modifier
) {
    val youTubePlayerState = remember { mutableStateOf<YouTubePlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableFloatStateOf(0f) }
    var videoInfo by remember { mutableStateOf<VideoInfo?>(null) }
    val scope = rememberCoroutineScope()
    val youTubeService = remember { YouTubeService() }

    // 加载视频信息的函数
    fun loadVideoInfo(videoId: String) {
        scope.launch {
            videoInfo = youTubeService.getVideoInfo(videoId)
        }
    }

    // 在视频加载时获取信息
    LaunchedEffect(Unit) {
        loadVideoInfo("8ZP5eqm4JqM")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // YouTube Player (隐藏)
        Box(
            modifier = Modifier.height(0.dp).width(0.dp)
        ) {
            AndroidView(
                factory = { context ->
                    YouTubePlayerView(context).apply {
                        enableAutomaticInitialization = false
                        initialize(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayerState.value = youTubePlayer
                                youTubePlayer.loadVideo("8ZP5eqm4JqM", 0f)
                            }
                        })
//                        lifecycleOwner.lifecycle.addObserver(this)
                    }
                },
                modifier = Modifier.height(0.dp)
            )
        }

        // 播放器界面
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 封面区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = "Album Cover",
                    modifier = Modifier.size(200.dp)
                )
            }

            // 标题和艺术家
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = videoInfo?.title ?: "Loading...",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = videoInfo?.channelTitle ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // 进度条
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentTime,
                    onValueChange = { currentTime = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0:00")
                    Text(text = "3:45")
                }
            }

            // 控制按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.SkipPrevious, "Previous")
                }

                FilledIconButton(
                    onClick = {
                        isPlaying = !isPlaying
                        youTubePlayerState.value?.let { player ->
                            if (isPlaying) player.play() else player.pause()
                        }
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        "Play/Pause",
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.SkipNext, "Next")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerPreview() {
    Player()
}