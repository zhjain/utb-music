package com.startend.youtubeaudioplayer.ui.player

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.startend.youtubeaudioplayer.data.model.VideoInfo

@Composable
fun PlayerCover(
    videoInfo: VideoInfo?,
    modifier: Modifier = Modifier
) {
    if (videoInfo?.thumbnailUrl != null) {
        Log.d("PlayerCover", "VideoInfo: ${videoInfo.thumbnailUrl}")
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (videoInfo?.thumbnailUrl != null) {
            // 获取高质量的缩略图URL
            val highQualityThumbnail = getHighQualityThumbnail(videoInfo.thumbnailUrl)
            Log.d("PlayerCover", "Using high quality thumbnail: $highQualityThumbnail")
            
            // 使用SubcomposeAsyncImage替代AsyncImage，可以更灵活地处理加载状态
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(highQualityThumbnail)
                    .crossfade(true)
                    .size(Size.ORIGINAL) // 使用原始尺寸
                    .build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    // 加载中显示进度条
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                error = {
                    // 加载失败显示默认图标
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = "Album Cover",
                        modifier = Modifier.size(200.dp)
                    )
                }
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

// 获取高质量的缩略图URL
private fun getHighQualityThumbnail(url: String): String {
    // YouTube缩略图通常有不同的质量版本
    // 默认URL通常是这样的: https://i.ytimg.com/vi/{VIDEO_ID}/default.jpg
    // 高质量版本: https://i.ytimg.com/vi/{VIDEO_ID}/hqdefault.jpg
    // 最高质量版本: https://i.ytimg.com/vi/{VIDEO_ID}/maxresdefault.jpg
    
    return url.replace("default.jpg", "maxresdefault.jpg")
        .replace("mqdefault.jpg", "maxresdefault.jpg")
        .replace("hqdefault.jpg", "maxresdefault.jpg")
        .replace("sddefault.jpg", "maxresdefault.jpg")
}