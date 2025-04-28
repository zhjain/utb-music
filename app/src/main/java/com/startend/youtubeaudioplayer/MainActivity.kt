package com.startend.youtubeaudioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.startend.youtubeaudioplayer.ui.player.Player
import com.startend.youtubeaudioplayer.ui.theme.YoutubeAudioPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubeAudioPlayerTheme {
                Player()
            }
        }
    }
}