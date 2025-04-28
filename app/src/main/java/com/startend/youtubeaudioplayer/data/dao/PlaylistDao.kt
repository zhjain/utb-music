package com.startend.youtubeaudioplayer.data.dao

import androidx.room.*
import com.startend.youtubeaudioplayer.data.model.Playlist
import com.startend.youtubeaudioplayer.data.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId ORDER BY position")
    fun getPlaylistItems(playlistId: Long): Flow<List<PlaylistItem>>

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Insert
    suspend fun insertPlaylistItems(items: List<PlaylistItem>)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylistItem(item: PlaylistItem)
}