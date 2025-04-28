package com.startend.youtubeaudioplayer.service

import android.util.Log
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoListResponse
import com.startend.youtubeaudioplayer.data.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YouTubeService {
    companion object {
        private const val API_KEY = "AIzaSyBVCOaK_vhgfKhOI_Uu8YR1IijEYp6a6Wg" // 替换为你的 API key
        private const val APPLICATION_NAME = "YoutubeAudioPlayer"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    }

    private val youtube: YouTube by lazy {
        try {
            Log.d("YouTubeService", "Initializing YouTube service...")
            // 使用 AndroidNetworkingConfig 替代默认的网络传输
            val httpTransport = NetHttpTransport.Builder()
                .trustCertificates(null) // 使用系统默认的证书
                .build()
            Log.d("YouTubeService", "Transport created successfully")
            
            YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build()
                .also { Log.d("YouTubeService", "YouTube service initialized successfully") }
        } catch (e: Exception) {
            Log.e("YouTubeService", "Error initializing YouTube service", e)
            Log.e("YouTubeService", "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }

    suspend fun getVideoInfo(videoId: String): VideoInfo? = withContext(Dispatchers.IO) {
        try {
            Log.d("YouTubeService", "Starting video info request for $videoId")
            val request = youtube.videos()
                .list("snippet,contentDetails")
                .setKey(API_KEY)
                .setId(videoId)
            
            Log.d("YouTubeService", "Executing request...")
            val response: VideoListResponse = request.execute()
            Log.d("YouTubeService", "Response received successfully")
            
            if (response.items.isNullOrEmpty()) {
                Log.w("YouTubeService", "No items found in response")
                return@withContext null
            }

            val video = response.items[0]
            Log.d("YouTubeService", """
                Video info retrieved successfully:
                ID: $videoId
                Title: ${video.snippet.title}
                Channel: ${video.snippet.channelTitle}
                Thumbnail: ${video.snippet.thumbnails.default.url}
                Duration: ${video.contentDetails.duration}
            """.trimIndent())
            VideoInfo(
                id = videoId,
                title = video.snippet.title,
                channelTitle = video.snippet.channelTitle,
                thumbnailUrl = video.snippet.thumbnails.default.url,
                duration = video.contentDetails.duration
            )
            
        } catch (e: Exception) {
            Log.e("YouTubeService", "Error getting video info", e)
            Log.e("YouTubeService", "Detailed error: ${e.message}")
            Log.e("YouTubeService", "Stack trace: ${e.stackTrace.joinToString("\n")}")
            null
        }
    }

    suspend fun getPlaylistItems(playlistId: String): List<VideoInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d("YouTubeService", "Starting playlist items request for $playlistId")
            val request = youtube.playlistItems()
                .list("snippet,contentDetails")
                .setKey(API_KEY)
                .setPlaylistId(playlistId)
                .setMaxResults(50L) // 可以根据需要调整

            Log.d("YouTubeService", "Executing playlist request...")
            val response = request.execute()
            Log.d("YouTubeService", "Response received successfully")

            if (response.items.isNullOrEmpty()) {
                Log.w("YouTubeService", "No items found in playlist")
                return@withContext emptyList()
            }

            response.items.mapNotNull { playlistItem ->
                try {
                    // 获取视频详细信息（包括时长）
                    val videoInfo = getVideoInfo(playlistItem.contentDetails.videoId)
                    videoInfo
                } catch (e: Exception) {
                    Log.e("YouTubeService", "Error getting video info for ${playlistItem.contentDetails.videoId}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("YouTubeService", "Error getting playlist items", e)
            Log.e("YouTubeService", "Detailed error: ${e.message}")
            Log.e("YouTubeService", "Stack trace: ${e.stackTrace.joinToString("\n")}")
            emptyList()
        }
    }

    // 从YouTube播放列表URL中提取ID
    fun extractPlaylistId(url: String): String? {
        val pattern = "list=([\\w-]+)".toRegex()
        return pattern.find(url)?.groupValues?.get(1)
    }
}
