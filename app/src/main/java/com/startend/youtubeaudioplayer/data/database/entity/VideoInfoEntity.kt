package com.startend.youtubeaudioplayer.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.startend.youtubeaudioplayer.data.model.VideoInfo

@Entity(tableName = "video_info")
data class VideoInfoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val duration: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun VideoInfoEntity.toVideoInfo() = VideoInfo(
    id = id,
    title = title,
    channelTitle = channelTitle,
    thumbnailUrl = thumbnailUrl,
    duration = duration
)

fun VideoInfo.toEntity() = VideoInfoEntity(
    id = id,
    title = title,
    channelTitle = channelTitle,
    thumbnailUrl = thumbnailUrl,
    duration = duration
)