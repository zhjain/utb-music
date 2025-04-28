package com.startend.youtubeaudioplayer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.startend.youtubeaudioplayer.data.dao.PlaylistDao
import com.startend.youtubeaudioplayer.data.dao.VideoInfoDao
import com.startend.youtubeaudioplayer.data.database.entity.VideoInfoEntity
import com.startend.youtubeaudioplayer.data.model.Playlist
import com.startend.youtubeaudioplayer.data.model.PlaylistItem

@Database(
    entities = [
        Playlist::class, 
        PlaylistItem::class, 
        VideoInfoEntity::class
    ], 
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun videoInfoDao(): VideoInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}