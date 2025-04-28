package com.startend.youtubeaudioplayer.data.dao

import androidx.room.*
import com.startend.youtubeaudioplayer.data.database.entity.VideoInfoEntity

@Dao
interface VideoInfoDao {
    @Query("SELECT * FROM video_info WHERE id = :videoId")
    suspend fun getVideoInfo(videoId: String): VideoInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoInfo(videoInfo: VideoInfoEntity)
}