package com.startend.youtubeaudioplayer.data.repository

import com.startend.youtubeaudioplayer.data.dao.PlaylistDao
import com.startend.youtubeaudioplayer.data.model.Playlist
import com.startend.youtubeaudioplayer.data.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {
    
    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
    }
    
    fun getPlaylistItems(playlistId: Long): Flow<List<PlaylistItem>> {
        return playlistDao.getPlaylistItems(playlistId)
    }
    
    suspend fun insertPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlist)
    }
    
    suspend fun insertPlaylistItems(items: List<PlaylistItem>) {
        playlistDao.insertPlaylistItems(items)
    }
    
    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }
    
    suspend fun deletePlaylistItem(item: PlaylistItem) {
        playlistDao.deletePlaylistItem(item)
    }
}
