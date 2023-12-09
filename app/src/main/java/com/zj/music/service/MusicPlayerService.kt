package com.zj.music.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.zj.music.MainActivity
import com.zj.music.PlaylistActivity
import com.zj.music.R
import com.zj.music.databinding.ActivityPlaylistBinding
import com.zj.music.databinding.RemoteLayoutBinding

class MusicPlayerService : Service() {
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        ServiceCompat.startForeground(this,2,showNotification(),)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("myTest", "music_service start")
        showNotification()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_player_channel",
                "Music Player",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, PlaylistActivity::class.java)

        val builder = NotificationCompat.Builder(this, "music_player_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("音乐播放器")
            .setContentText("正在播放音乐")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                R.mipmap.ic_launcher,
                "下一首",
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }


}