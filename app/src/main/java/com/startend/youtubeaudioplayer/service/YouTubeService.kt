package com.startend.youtubeaudioplayer.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoListResponse
import com.startend.youtubeaudioplayer.data.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YouTubeService {
    private val youtube: YouTube by lazy {
        YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null
        ).setApplicationName("YoutubeAudioPlayer").build()
    }

    suspend fun getVideoInfo(videoId: String): VideoInfo? = withContext(Dispatchers.IO) {
        try {
            val response: VideoListResponse = youtube.videos()
                .list("snippet,contentDetails")
                .setKey("YOUR_API_KEY") // 替换为你的 API key
                .setId(videoId)
                .execute()

            if (response.items.isNullOrEmpty()) return@withContext null

            val video = response.items[0]
            val snippet = video.snippet
            val contentDetails = video.contentDetails

            VideoInfo(
                id = videoId,
                title = snippet.title,
                channelTitle = snippet.channelTitle,
                thumbnailUrl = snippet.thumbnails.default.url,
                duration = contentDetails.duration
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}