package com.startend.youtubeaudioplayer.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.startend.youtubeaudioplayer.data.database.AppDatabase
import com.startend.youtubeaudioplayer.data.model.PlaylistItem
import com.startend.youtubeaudioplayer.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

@Composable
fun PlaylistItemsScreen(
    modifier: Modifier = Modifier,
    playlistId: Long,
    onItemSelected: (PlaylistItem) -> Unit,
    currentPlayingItemId: String? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    val playlistRepository = remember { PlaylistRepository(database.playlistDao()) }
    val playlistItems by playlistRepository.getPlaylistItems(playlistId).collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(playlistItems) { item ->
            PlaylistItemCard(
                playlistItem = item,
                isPlaying = item.videoId == currentPlayingItemId,
                onClick = { onItemSelected(item) },
                onDelete = {
                    scope.launch {
                        playlistRepository.deletePlaylistItem(item)
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaylistItemCard(
    playlistItem: PlaylistItem,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = if (isPlaying) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playlistItem.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            
            // Title and other info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlistItem.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Delete button
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "删除歌曲")
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除歌曲 \"${playlistItem.title}\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }
}
