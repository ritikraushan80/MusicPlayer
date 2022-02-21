package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AppClass: Application() {
    companion object{
        const val CHANEL_ID = "channel"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANEL_ID, "Playing Now", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Current Song Playing Now!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE)as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}