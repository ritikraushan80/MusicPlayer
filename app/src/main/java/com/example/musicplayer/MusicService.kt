package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat




class MusicService : Service() {
    private var myBinder=MyBinder()
    var mediaPlayer: MediaPlayer?=null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder {
        mediaSession=MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {
        val prevIntent=
            Intent(baseContext, NotificationReceiver::class.java).setAction(AppClass.PREVIOUS)
        val prevPendingIntent=PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent=
            Intent(baseContext, NotificationReceiver::class.java).setAction(AppClass.PLAY)
        val playPendingIntent=PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent=
            Intent(baseContext, NotificationReceiver::class.java).setAction(AppClass.NEXT)
        val nextPendingIntent=PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent=
            Intent(baseContext, NotificationReceiver::class.java).setAction(AppClass.EXIT)
        val exitPendingIntent=PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt=getImageArt(PlayerActivity.playMusicList[PlayerActivity.songPosition].path)
        val image=if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.large_icon)
        }

        val notification=NotificationCompat.Builder(baseContext, AppClass.CHANEL_ID)
            .setContentTitle(PlayerActivity.playMusicList[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.playMusicList[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.notification_icon)
            .setLargeIcon(image)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_baseline_skip_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_baseline_skip_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_baseline_exit, "Exit", exitPendingIntent)
            .build()

        startForeground(10, notification)
    }

    fun createMediaPlayer() {
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) {
                PlayerActivity.musicService!!.mediaPlayer=MediaPlayer()
                PlayerActivity.musicService!!.mediaPlayer!!.reset()
                PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.playMusicList[PlayerActivity.songPosition].path)
                PlayerActivity.musicService!!.mediaPlayer!!.prepare()
                PlayerActivity.binding.playPausebtn.setIconResource(R.drawable.ic_baseline_pause)
                PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause)
                PlayerActivity.binding.startTime.text=
                    formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
                PlayerActivity.binding.endTime.text=
                    formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.duration.toLong())
                PlayerActivity.binding.seekbar.progress=0
                PlayerActivity.binding.seekbar.max=
                    PlayerActivity.musicService!!.mediaPlayer!!.duration
            }
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarsetup() {
        runnable=Runnable {
            PlayerActivity.binding.startTime.text=
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbar.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

}

//private fun setToolbar() {
//    try {
//        if (toolbar != null) {
//            toolbar.setVisibility(View.VISIBLE)
//            val frameLayout=toolbar.getChildAt(0) as FrameLayout
//            for (i in 0 until frameLayout.childCount) frameLayout.getChildAt(i).visibility=View.GONE
//            toolbarTitle.setVisibility(View.VISIBLE)
//        }
//        if (botNavigation != null) botNavigation.setVisibility(View.VISIBLE)
//    } catch (ex: java.lang.Exception) {
//    }
//}
