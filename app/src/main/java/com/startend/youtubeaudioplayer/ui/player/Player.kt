package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.launch
import com.startend.youtubeaudioplayer.data.VideoInfo
import com.startend.youtubeaudioplayer.data.PlayMode
import com.startend.youtubeaudioplayer.service.YouTubeService
import com.startend.youtubeaudioplayer.ui.playlist.PlaylistScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Player(
    modifier: Modifier = Modifier
) {
    val youTubePlayerState = remember { mutableStateOf<YouTubePlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(0f) }
    var videoInfo by remember { mutableStateOf<VideoInfo?>(null) }
    val scope = rememberCoroutineScope()
    val youTubeService = remember { YouTubeService() }
    val sheetState = rememberModalBottomSheetState()
    var showPlaylist by remember { mutableStateOf(false) }
    var playMode by remember { mutableStateOf(PlayMode.SEQUENCE) }
    
    // 添加一个 LaunchedEffect 来获取视频信息
    LaunchedEffect(Unit) {
        scope.launch {
            // 这里使用示例视频ID "8ZP5eqm4JqM"，你可以根据需要更改
            val info = youTubeService.getVideoInfo("8ZP5eqm4JqM")
            videoInfo = info
        }
    }

    if (showPlaylist) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylist = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "播放列表",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                PlaylistScreen(
                    onPlaylistSelected = { playlistId ->
                        scope.launch {
                            sheetState.hide()
                            showPlaylist = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        YouTubePlayerView(
            youTubePlayerState = youTubePlayerState,
            onStateChange = { state -> 
                isPlaying = state == PlayerConstants.PlayerState.PLAYING 
            },
            onCurrentSecond = { second -> currentTime = second },
            onDuration = { videoDuration -> duration = videoDuration },
            // 添加视频ID参数
            videoId = "8ZP5eqm4JqM"
        )

        PlayerCover(
            videoInfo = videoInfo,
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = videoInfo?.title ?: "Loading...",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = videoInfo?.channelTitle ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        PlayerProgress(
            currentTime = currentTime,
            duration = duration,
            onSeek = { newPosition ->
                currentTime = newPosition
                youTubePlayerState.value?.seekTo(newPosition)
            }
        )

        PlayerControls(
            isPlaying = isPlaying,
            playMode = playMode,
            onPlayPauseClick = {
                isPlaying = !isPlaying
                youTubePlayerState.value?.let { player ->
                    if (isPlaying) player.play() else player.pause()
                }
            },
            onPreviousClick = { /* TODO */ },
            onNextClick = { /* TODO */ },
            onPlayModeClick = {
                playMode = when (playMode) {
                    PlayMode.SEQUENCE -> PlayMode.LOOP_ALL
                    PlayMode.LOOP_ALL -> PlayMode.SHUFFLE
                    PlayMode.SHUFFLE -> PlayMode.LOOP_ONE
                    PlayMode.LOOP_ONE -> PlayMode.SEQUENCE
                }
            },
            onPlaylistClick = { showPlaylist = true }
        )
    }
}