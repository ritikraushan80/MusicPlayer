package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       when(intent?.action){
           AppClass.PREVIOUS ->prevNextSong(increment = false, context = context!!)
           AppClass.PLAY ->{
               if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
           }
           AppClass.NEXT ->prevNextSong(increment = true, context = context!!)
           AppClass.EXIT -> {
               PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService = null
               exitProcess(1)
           }
       }
    }
    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause)
        PlayerActivity.binding.playPausebtn.setIconResource(R.drawable.ic_baseline_pause)
    }
    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_play)
        PlayerActivity.binding.playPausebtn.setIconResource(R.drawable.ic_baseline_play)
    }
    private  fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
               Glide.with(context)
             .load(PlayerActivity.playMusicList[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.nav_icon).centerCrop())
            .into(PlayerActivity.binding.songImg)
        PlayerActivity.binding.songName.text=
            PlayerActivity.playMusicList[PlayerActivity.songPosition].title
        playMusic()
    }
}