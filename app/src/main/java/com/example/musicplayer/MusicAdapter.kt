package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicListBinding

class MusicAdapter(private val context: Context, private val musicList: ArrayList<MusicStorage>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    class ViewHolder(binding: MusicListBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songName
        val album = binding.songAlbum
        val image = binding.songImage
        val duration = binding.songDuration
        val root = binding.root


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        return ViewHolder(MusicListBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)

        Glide.with(context).load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.nav_icon).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener{
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context,intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}