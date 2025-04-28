package com.startend.youtubeaudioplayer.service

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoListResponse
import com.startend.youtubeaudioplayer.data.database.AppDatabase
import com.startend.youtubeaudioplayer.data.model.VideoInfo
import com.startend.youtubeaudioplayer.data.repository.VideoInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YouTubeService(context: Context) {
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

    private val database = AppDatabase.getDatabase(context)
    private val videoInfoRepository = VideoInfoRepository(database.videoInfoDao())

    suspend fun getVideoInfo(videoId: String): VideoInfo? = withContext(Dispatchers.IO) {
        try {
            // 首先尝试从本地数据库获取
            val cacheExpiration = 7 * 24 * 60 * 60 * 1000L // 7天的毫秒数
            if (videoInfoRepository.isCacheValid(videoId, cacheExpiration)) {
                val cachedInfo = videoInfoRepository.getVideoInfo(videoId)
                Log.d("YouTubeService", "Retrieved video info from cache for $videoId")
                return@withContext cachedInfo
            }

            // 如果没有缓存或缓存已过期，从YouTube API获取
            Log.d("YouTubeService", "Fetching video info from YouTube API for $videoId")
            val request = youtube.videos()
                .list("snippet,contentDetails")
                .setKey(API_KEY)
                .setId(videoId)

            val response: VideoListResponse = request.execute()

            if (response.items.isNullOrEmpty()) {
                Log.w("YouTubeService", "No items found in response")
                return@withContext null
            }

            val video = response.items[0]
            val videoInfo = VideoInfo(
                id = videoId,
                title = video.snippet.title,
                channelTitle = video.snippet.channelTitle,
                thumbnailUrl = video.snippet.thumbnails.default.url,
                duration = video.contentDetails.duration
            )

            // 保存到本地数据库
            videoInfoRepository.insertVideoInfo(videoInfo)
            Log.d("YouTubeService", "Saved video info to cache for $videoId")

            videoInfo

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
        return try {
            val uri = url.toUri()
            uri.getQueryParameter("list")?.also { playlistId ->
                Log.d("YouTubeService", "Extracted playlist ID: $playlistId from URL: $url")
            }
        } catch (e: Exception) {
            Log.e("YouTubeService", "Failed to parse URL: $url", e)
            null
        }
    }
}
