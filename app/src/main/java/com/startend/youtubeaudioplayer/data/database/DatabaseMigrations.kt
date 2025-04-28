package com.startend.youtubeaudioplayer.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop the existing empty table if it exists
        database.execSQL("DROP TABLE IF EXISTS video_info")
        
        // Create the table with all required columns
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `video_info` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `channelTitle` TEXT NOT NULL,
                `thumbnailUrl` TEXT NOT NULL,
                `duration` TEXT NOT NULL,
                `lastUpdated` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
    }
}