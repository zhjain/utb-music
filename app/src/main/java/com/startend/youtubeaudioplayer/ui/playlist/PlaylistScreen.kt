package com.startend.youtubeaudioplayer.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.startend.youtubeaudioplayer.data.database.AppDatabase
import com.startend.youtubeaudioplayer.data.model.Playlist
import com.startend.youtubeaudioplayer.data.model.PlaylistItem
import com.startend.youtubeaudioplayer.data.repository.PlaylistRepository
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.startend.youtubeaudioplayer.service.YouTubeService

@Composable
fun PlaylistScreen(
    onPlaylistSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistRepository = remember { PlaylistRepository(database.playlistDao()) }
    val playlists by playlistRepository.getAllPlaylists().collectAsState(initial = emptyList())

    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column {
                SmallFloatingActionButton(
                    onClick = { showImportDialog = true },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, "Import from YouTube")
                }
                FloatingActionButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(Icons.Default.Add, "Create Playlist")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistSelected(playlist.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                scope.launch {
                    playlistRepository.insertPlaylist(
                        Playlist(name = name, description = description)
                    )
                }
                showCreateDialog = false
            }
        )
    }

    if (showImportDialog) {
        ImportPlaylistDialog(
            onDismiss = { showImportDialog = false },
            onImport = { playlistUrl ->
                scope.launch {
                    // TODO: 实现YouTube播放列表导入逻辑
                }
                showImportDialog = false
            }
        )
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (playlist.description.isNotEmpty()) {
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Playlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Playlist Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ImportPlaylistDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var playlistUrl by remember { mutableStateOf("") }
    var playlistName by remember { mutableStateOf("导入的歌单") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val youTubeService = remember { YouTubeService(context) }
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistRepository = remember { PlaylistRepository(database.playlistDao()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导入YouTube播放列表") },
        text = {
            Column {
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("播放列表名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = playlistUrl,
                    onValueChange = {
                        playlistUrl = it
                        errorMessage = null
                    },
                    label = { Text("YouTube播放列表URL") },
                    isError = errorMessage != null,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        val result = runCatching {
                            val playlistId = youTubeService.extractPlaylistId(playlistUrl)
                                ?: throw IllegalArgumentException("无效的播放列表URL")

                            val playlistItems = youTubeService.getPlaylistItems(playlistId)
                            if (playlistItems.isEmpty()) {
                                throw IllegalStateException("播放列表中没有找到视频")
                            }

                            // 使用用户输入的名称创建播放列表
                            val playlist = Playlist(
                                name = playlistName,
                                youtubePlaylistId = playlistId
                            )
                            val newPlaylistId = playlistRepository.insertPlaylist(playlist)

                            // 添加播放列表项
                            val newPlaylistItems = playlistItems.mapIndexed { index, videoInfo ->
                                PlaylistItem(
                                    playlistId = newPlaylistId,
                                    videoId = videoInfo.id,
                                    title = videoInfo.title,
                                    thumbnailUrl = videoInfo.thumbnailUrl,
                                    position = index
                                )
                            }
                            playlistRepository.insertPlaylistItems(newPlaylistItems)
                            true
                        }

                        result.fold(
                            onSuccess = { success ->
                                if (success) {
                                    Toast.makeText(context, "播放列表导入成功", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                            },
                            onFailure = { e ->
                                errorMessage = when (e) {
                                    is IllegalArgumentException -> "无效的播放列表URL"
                                    is IllegalStateException -> "播放列表中没有找到视频"
                                    else -> "导入失败: ${e.message}"
                                }
                            }
                        )

                        isLoading = false
                    }
                },
                enabled = playlistUrl.isNotBlank() && playlistName.isNotBlank() && !isLoading
            ) {
                Text("导入")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("取消")
            }
        }
    )
}