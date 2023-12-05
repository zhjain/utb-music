package com.zj.music

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import com.zj.music.databinding.ActivityPlaylistBinding

class PlaylistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}