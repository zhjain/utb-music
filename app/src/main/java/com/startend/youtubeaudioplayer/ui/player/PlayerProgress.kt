package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import java.util.Locale

@Composable
fun PlayerProgress(
    currentTime: Float,
    duration: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        var isDragging by remember { mutableStateOf(false) }
        var dragPosition by remember { mutableFloatStateOf(0f) }
        var lastSeekPosition by remember { mutableFloatStateOf(-1f) }
        
        // 使用实际显示的值，拖动时显示拖动位置，否则显示当前播放位置
        val displayPosition = if (isDragging) dragPosition else currentTime
        
        Slider(
            value = displayPosition,
            onValueChange = { newValue ->
                isDragging = true
                dragPosition = newValue
            },
            onValueChangeFinished = {
                // 只在拖动结束时调用onSeek，减少不必要的更新
                if (isDragging) {
                    lastSeekPosition = dragPosition
                    onSeek(dragPosition)
                    // 不要立即重置isDragging，等待外部currentTime更新后再重置
                }
            },
            valueRange = 0f..duration.coerceAtLeast(0.1f),
            modifier = Modifier.fillMaxWidth()
        )
        
        // 当外部currentTime更新到接近lastSeekPosition时，重置isDragging状态
        LaunchedEffect(currentTime) {
            if (isDragging && lastSeekPosition > 0 && kotlin.math.abs(currentTime - lastSeekPosition) < 1f) {
                isDragging = false
                lastSeekPosition = -1f
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatDuration(displayPosition))
            Text(text = formatDuration(duration))
        }
    }
}

private fun formatDuration(seconds: Float): String {
    val minutes = (seconds / 60).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return String.format(Locale.US, "%d:%02d", minutes, remainingSeconds)
}