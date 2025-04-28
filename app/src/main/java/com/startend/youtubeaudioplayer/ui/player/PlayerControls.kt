package com.startend.youtubeaudioplayer.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.startend.youtubeaudioplayer.data.PlayMode

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    playMode: PlayMode,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPlayModeClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (playModeIcon, playModeDescription) = when (playMode) {
        PlayMode.SEQUENCE -> Icons.Default.Repeat to "顺序播放"
        PlayMode.LOOP_ALL -> Icons.Default.RepeatOne to "循环播放"
        PlayMode.SHUFFLE -> Icons.Default.Shuffle to "随机播放"
        PlayMode.LOOP_ONE -> Icons.Default.RepeatOne to "单曲循环"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayModeClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = playModeIcon,
                contentDescription = playModeDescription,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.SkipPrevious, "Previous")
        }

        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.Center
        ) {
            FilledIconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    "Play/Pause",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        IconButton(
            onClick = onNextClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.SkipNext, "Next")
        }

        IconButton(
            onClick = onPlaylistClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = "Playlist"
            )
        }
    }
}