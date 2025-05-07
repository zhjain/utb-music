package com.startend.youtubeaudioplayer.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.startend.youtubeaudioplayer.data.database.AppDatabase
import com.startend.youtubeaudioplayer.data.model.PlayMode
import com.startend.youtubeaudioplayer.data.model.Playlist
import com.startend.youtubeaudioplayer.data.model.PlaylistItem
import com.startend.youtubeaudioplayer.data.model.VideoInfo
import com.startend.youtubeaudioplayer.data.repository.PlaylistRepository
import com.startend.youtubeaudioplayer.service.PlaybackService
import com.startend.youtubeaudioplayer.service.YouTubeService
import com.startend.youtubeaudioplayer.ui.playlist.PlaylistItemsScreen
import com.startend.youtubeaudioplayer.ui.playlist.PlaylistScreen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    val context = LocalContext.current
    val youTubeService = remember { YouTubeService(context) }
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistRepository = remember { PlaylistRepository(database.playlistDao()) }
    val sheetState = rememberModalBottomSheetState()
    var showPlaylist by remember { mutableStateOf(false) }
    var playMode by remember { mutableStateOf(PlayMode.SEQUENCE) }

    // 当前播放列表状态
    var currentPlaylistId by remember { mutableStateOf<Long?>(null) }
    var currentPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var currentPlaylistItems by remember { mutableStateOf<List<PlaylistItem>>(emptyList()) }
    var currentPlayingItemIndex by remember { mutableIntStateOf(0) }
    var currentVideoId by remember { mutableStateOf<String?>(null) }
    
    // 显示播放列表选择还是播放列表内容
    var showPlaylistSelector by remember { mutableStateOf(true) }

    val playbackService = remember { mutableStateOf<PlaybackService?>(null) }
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                playbackService.value = (service as PlaybackService.PlaybackBinder).getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                playbackService.value = null
            }
        }
    }

    // 播放指定的项目
    fun playItem(item: PlaylistItem) {
        scope.launch {
            currentVideoId = item.videoId
            val info = youTubeService.getVideoInfo(item.videoId)
            videoInfo = info
            youTubePlayerState.value?.loadVideo(item.videoId, 0f)
            // 更新服务中的视频信息
            info?.let { playbackService.value?.updateVideoInfo(it) }
        }
    }

    // 播放下一首
    fun playNext() {
        if (currentPlaylistItems.isEmpty()) return

        val nextIndex = when (playMode) {
            PlayMode.SHUFFLE -> (0 until currentPlaylistItems.size).random()
            else -> (currentPlayingItemIndex + 1) % currentPlaylistItems.size
        }

        currentPlayingItemIndex = nextIndex
        playItem(currentPlaylistItems[nextIndex])
    }

    // 播放上一首
    fun playPrevious() {
        if (currentPlaylistItems.isEmpty()) return

        val prevIndex = when (playMode) {
            PlayMode.SHUFFLE -> (0 until currentPlaylistItems.size).random()
            else -> if (currentPlayingItemIndex > 0) currentPlayingItemIndex - 1 else currentPlaylistItems.size - 1
        }

        currentPlayingItemIndex = prevIndex
        playItem(currentPlaylistItems[prevIndex])
    }

    // 处理视频播放结束
    fun handleVideoEnded() {
        when (playMode) {
            PlayMode.SEQUENCE -> {
                // 顺序播放：播放下一首，如果是最后一首则停止
                if (currentPlayingItemIndex < currentPlaylistItems.size - 1) {
                    playNext()
                }
            }
            PlayMode.LOOP_ALL -> {
                // 循环播放：总是播放下一首
                playNext()
            }
            PlayMode.SHUFFLE -> {
                // 随机播放：随机选择下一首
                playNext() // playNext 方法已经包含了随机逻辑
            }
            PlayMode.LOOP_ONE -> {
                // 单曲循环：重新播放当前歌曲
                currentPlaylistItems.getOrNull(currentPlayingItemIndex)?.let { item ->
                    playItem(item)
                }
            }
        }
    }

    // 绑定服务
    LaunchedEffect(Unit) {
        val intent = Intent(context, PlaybackService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // 设置播放控制回调
    LaunchedEffect(playbackService.value) {
        playbackService.value?.setNavigationCallbacks(
            onPrevious = { playPrevious() },
            onNext = { playNext() }
        )
    }

    // 在组件销毁时解绑服务
    DisposableEffect(Unit) {
        onDispose {
            try {
                context.unbindService(serviceConnection)
            } catch (_: Exception) {
                // 忽略解绑异常
            }
        }
    }

    // 加载播放列表并播放第一个项目
    fun loadPlaylist(playlistId: Long) {
        scope.launch {
            val playlist = playlistRepository.getAllPlaylists().firstOrNull()?.find { it.id == playlistId }
            if (playlist != null) {
                currentPlaylistId = playlistId
                currentPlaylist = playlist

                val items = playlistRepository.getPlaylistItems(playlistId).firstOrNull() ?: emptyList()
                currentPlaylistItems = items

                if (items.isNotEmpty()) {
                    currentPlayingItemIndex = 0
                    playItem(items[0])
                }
            }
        }
    }

    // 初始化播放器
    LaunchedEffect(Unit) {
        scope.launch {
            // 尝试加载最后播放的播放列表
            val playlists = playlistRepository.getAllPlaylists().firstOrNull()
            if (!playlists.isNullOrEmpty()) {
                val firstPlaylist = playlists.first()
                loadPlaylist(firstPlaylist.id)
            }
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
                if (showPlaylistSelector) {
                    // 显示播放列表选择界面
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "选择播放列表",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    PlaylistScreen(
                        onPlaylistSelected = { playlistId ->
                            scope.launch {
                                loadPlaylist(playlistId)
                                showPlaylistSelector = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // 显示当前播放列表内容
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showPlaylistSelector = true }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回播放列表")
                            }
                            Text(
                                text = currentPlaylist?.name ?: "当前播放列表",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        TextButton(
                            onClick = { /* TODO: 添加编辑功能 */ }
                        ) {
                            Text("编辑")
                        }
                    }

                    // 显示播放列表内容
                    PlaylistItemsScreen(
                        playlistId = currentPlaylistId ?: 0,
                        currentPlayingItemId = currentVideoId,
                        onItemSelected = { item ->
                            val index = currentPlaylistItems.indexOfFirst { it.id == item.id }
                            if (index >= 0) {
                                currentPlayingItemIndex = index
                                playItem(item)
                                scope.launch {
                                    sheetState.hide()
                                    showPlaylist = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
                // 更新服务状态
                playbackService.value?.setPlayer(youTubePlayerState.value!!)
            },
            onCurrentSecond = { second -> currentTime = second },
            onDuration = { videoDuration -> duration = videoDuration },
            videoId = currentVideoId ?: "8ZP5eqm4JqM", // 使用当前播放的视频ID或默认ID
            onVideoEnded = { handleVideoEnded() } // 添加视频结束回调
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
                // 立即更新UI显示的当前时间，避免闪烁
                currentTime = newPosition
                
                // 强制执行seek操作，确保位置更新
                youTubePlayerState.value?.let { player ->
                    // 先暂停播放，避免seek后立即被覆盖
                    val wasPlaying = isPlaying
                    if (wasPlaying) {
                        player.pause()
                        isPlaying = false
                    }
                    
                    // 执行seek操作
                    player.seekTo(newPosition)
                    
                    // 延迟恢复播放，确保seek操作完成
                    scope.launch {
                        delay(100) // 短暂延迟
                        if (wasPlaying) {
                            player.play()
                            isPlaying = true
                        }
                    }
                }
            }
        )

        PlayerControls(
            isPlaying = isPlaying,
            playMode = playMode,
            onPlayPauseClick = {
                // 立即更新UI状态，提高响应速度
                isPlaying = !isPlaying
                
                // 异步处理播放器操作
                scope.launch {
                    youTubePlayerState.value?.let { player ->
                        if (isPlaying) player.play() else player.pause()
                        // 更新服务中的播放状态
                        playbackService.value?.updatePlayingState(isPlaying)
                    }
                }
            },
            onPreviousClick = { 
                // 使用协程异步处理，避免UI卡顿
                scope.launch { playPrevious() } 
            },
            onNextClick = { 
                // 使用协程异步处理，避免UI卡顿
                scope.launch { playNext() } 
            },
            onPlayModeClick = {
                playMode = when (playMode) {
                    PlayMode.SEQUENCE -> PlayMode.LOOP_ALL
                    PlayMode.LOOP_ALL -> PlayMode.SHUFFLE
                    PlayMode.SHUFFLE -> PlayMode.LOOP_ONE
                    PlayMode.LOOP_ONE -> PlayMode.SEQUENCE
                }
            },
            onPlaylistClick = {
                showPlaylistSelector = currentPlaylistId == null
                showPlaylist = true
            }
        )
    }
}