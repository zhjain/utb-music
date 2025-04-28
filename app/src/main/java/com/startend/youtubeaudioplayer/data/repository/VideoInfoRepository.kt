package com.startend.youtubeaudioplayer.data.repository

import com.startend.youtubeaudioplayer.data.dao.VideoInfoDao
import com.startend.youtubeaudioplayer.data.database.entity.VideoInfoEntity
import com.startend.youtubeaudioplayer.data.database.entity.toVideoInfo
import com.startend.youtubeaudioplayer.data.model.VideoInfo
import com.startend.youtubeaudioplayer.data.database.entity.toEntity

class VideoInfoRepository(private val videoInfoDao: VideoInfoDao) {
    
    suspend fun getVideoInfo(videoId: String): VideoInfo? {
        val entity = videoInfoDao.getVideoInfo(videoId)
        return entity?.toVideoInfo()
    }
    
    suspend fun insertVideoInfo(videoInfo: VideoInfo) {
        videoInfoDao.insertVideoInfo(videoInfo.toEntity())
    }
    
    suspend fun isCacheValid(videoId: String, cacheExpirationMillis: Long): Boolean {
        val entity = videoInfoDao.getVideoInfo(videoId) ?: return false
        return System.currentTimeMillis() - entity.lastUpdated < cacheExpirationMillis
    }
}
