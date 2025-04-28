package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun PlayerProgress(
    currentTime: Float,
    duration: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = currentTime,
            onValueChange = onSeek,
            valueRange = 0f..duration.coerceAtLeast(0.1f),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatDuration(currentTime))
            Text(text = formatDuration(duration))
        }
    }
}

private fun formatDuration(seconds: Float): String {
    val minutes = (seconds / 60).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return String.format(Locale.US, "%d:%02d", minutes, remainingSeconds)
}