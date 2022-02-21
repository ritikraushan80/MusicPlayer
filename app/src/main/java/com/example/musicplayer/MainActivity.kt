package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var mainMusicList: ArrayList<MusicStorage>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermission()
        setTheme(R.style.orange_nav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Drawer//////////////////////////
        toogle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (requestRuntimePermission()) {
            initializeLayout()
        }

        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)

        }

        binding.favbtn.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.playlistbtn.setOnClickListener {
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings ->
                    Toast.makeText(baseContext, "settings", Toast.LENGTH_SHORT).show()
                R.id.feedback ->
                    Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.about ->
                    Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                R.id.exit ->
                    exitProcess(1)
            }
            true

        }

    }
    // permission request////

    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),10)
            return false
        }
            return  true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 10) {
            if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            }

            else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    10
                )

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {



       mainMusicList = fetchAllAudio()


        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(10)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, mainMusicList)
        binding.recyclerView.adapter = musicAdapter
        binding.totalSongs.text = "Total Songs : " + musicAdapter.itemCount


    }
    @SuppressLint("Recycle", "Range")
    private fun fetchAllAudio(): ArrayList<MusicStorage>{
        val tempList = ArrayList<MusicStorage>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection, null,
        MediaStore.Audio.Media.DATE_ADDED, null)

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val curTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val curId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val curAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val curArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val curPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val curDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val curAlbumId =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                   val uri = Uri.parse("content://media/external/audio/albumart")
                    val curArtUri = Uri.withAppendedPath(uri, curAlbumId).toString()
                    val music = MusicStorage(id = curId, title = curTitle, album = curAlbum, artist = curArtist, path = curPath, duration = curDuration, artUri = curArtUri)
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
                cursor.close()
        }
        return tempList
    }
}