package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var playMusicList: ArrayList<MusicStorage>
        var songPosition: Int=0
        var isPlaying: Boolean=false
        var musicService: MusicService?=null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.orange)

        binding=ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // For Starting Service
        val intent=Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        playerInitializeLayout()
        binding.playPausebtn.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.prevbtn.setOnClickListener {
            songNextPrev(increment=false)
        }
        binding.nextbtn.setOnClickListener {
            songNextPrev(increment=true)
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)=Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?)=Unit
        })
        binding.repeatBtn.setOnClickListener {
            if (!repeat) {
                repeat=true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.dark_orange))
            } else {
                repeat=false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

            }
        }
    }


    private fun playerInitializeLayout() {
        songPosition=intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                playMusicList=ArrayList()
                playMusicList.addAll(MainActivity.mainMusicList)
                setLayout()


            }
            "MainActivity" -> {
                playMusicList=ArrayList()
                playMusicList.addAll(MainActivity.mainMusicList)
                playMusicList.shuffle()
                setLayout()


            }
        }
    }

    private fun setLayout() {
        Glide.with(this).load(playMusicList[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.nav_icon).centerCrop())
            .into(binding.songImg)
        binding.songName.text=playMusicList[songPosition].title
        if (repeat) binding.repeatBtn.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.purple_500
            )
        )


    }


    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer=MediaPlayer()
                musicService!!.mediaPlayer!!.reset()
                musicService!!.mediaPlayer!!.setDataSource(playMusicList[songPosition].path)
                musicService!!.mediaPlayer!!.prepare()
                musicService!!.mediaPlayer!!.start()
                isPlaying=true
                binding.playPausebtn.setIconResource(R.drawable.ic_baseline_pause)
                musicService!!.showNotification(R.drawable.ic_baseline_pause)
                binding.startTime.text=
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.endTime.text=formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress=0
                binding.seekbar.max=musicService!!.mediaPlayer!!.duration
                musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            }
        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        binding.playPausebtn.setIconResource(R.drawable.ic_baseline_pause)
        musicService!!.showNotification(R.drawable.ic_baseline_pause)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.playPausebtn.setIconResource(R.drawable.ic_baseline_play)
        musicService!!.showNotification((R.drawable.ic_baseline_play))
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun songNextPrev(increment: Boolean) {
        if (increment) {
            setSongPosition(increment=true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment=false)
            setLayout()
            createMediaPlayer()
        }

    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        musicService=binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarsetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

    override fun onCompletion(mediaP: MediaPlayer?) {
        setSongPosition(increment=true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }
}