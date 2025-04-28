package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.startend.youtubeaudioplayer.data.VideoInfo

@Composable
fun PlayerCover(
    videoInfo: VideoInfo?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (videoInfo?.thumbnailUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(videoInfo.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Album,
                contentDescription = "Album Cover",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}