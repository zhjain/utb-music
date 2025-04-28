package com.startend.youtubeaudioplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.startend.youtubeaudioplayer.MainActivity
import com.startend.youtubeaudioplayer.R
import com.startend.youtubeaudioplayer.data.model.VideoInfo

class PlaybackService : Service() {
    private var youTubePlayer: YouTubePlayer? = null
    private var currentVideoInfo: VideoInfo? = null
    private var isPlaying = false
    private val binder = PlaybackBinder()
    private lateinit var mediaSession: MediaSessionCompat
    private var onPreviousClick: (() -> Unit)? = null
    private var onNextClick: (() -> Unit)? = null

    inner class PlaybackBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun setNavigationCallbacks(
        onPrevious: () -> Unit,
        onNext: () -> Unit
    ) {
        onPreviousClick = onPrevious
        onNextClick = onNext
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaSession()
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(this, "PlaybackService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    handlePlayPause()
                }

                override fun onPause() {
                    handlePlayPause()
                }

                override fun onSkipToPrevious() {
                    onPreviousClick?.invoke()
                }

                override fun onSkipToNext() {
                    onNextClick?.invoke()
                }
            })
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> handlePlayPause()
            ACTION_PREVIOUS -> onPreviousClick?.invoke()
            ACTION_NEXT -> onNextClick?.invoke()
        }
        return START_NOT_STICKY
    }

    private fun handlePlayPause() {
        youTubePlayer?.let { player ->
            isPlaying = !isPlaying
            if (isPlaying) {
                player.play()
            } else {
                player.pause()
            }
            updateNotification()
        }
    }

    fun setPlayer(player: YouTubePlayer) {
        youTubePlayer = player
    }

    fun updateVideoInfo(info: VideoInfo) {
        currentVideoInfo = info
        updateNotification()
    }

    fun updatePlayingState(playing: Boolean) {
        isPlaying = playing
        updateNotification()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "YouTube Audio Player Controls"
                setShowBadge(false)
            }
            
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 创建上一首按钮的 PendingIntent
        val previousIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            this, 1, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 创建播放/暂停按钮的 PendingIntent
        val playPauseIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 2, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 创建下一首按钮的 PendingIntent
        val nextIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 3, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentVideoInfo?.title ?: "Playing")
            .setContentText(currentVideoInfo?.channelTitle)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(null as Bitmap?)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            // 添加上一首按钮
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_previous,
                    "Previous",
                    previousPendingIntent
                ).build()
            )
            // 添加播放/暂停按钮
            .addAction(
                NotificationCompat.Action.Builder(
                    playPauseIcon,
                    playPauseTitle,
                    playPausePendingIntent
                ).build()
            )
            // 添加下一首按钮
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_skip_next,
                    "Next",
                    nextPendingIntent
                ).build()
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2) // 显示所有三个按钮
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        youTubePlayer = null
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "com.startend.youtubeaudioplayer.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.startend.youtubeaudioplayer.PREVIOUS"
        const val ACTION_NEXT = "com.startend.youtubeaudioplayer.NEXT"
    }
}