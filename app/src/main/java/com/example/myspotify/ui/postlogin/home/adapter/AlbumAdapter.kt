package com.example.myspotify.ui.postlogin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myspotify.R
import com.example.myspotify.databinding.AlbumViewBinding
import com.example.myspotify.data.model.Album

class AlbumAdapter(private val onClickListener: OnClickListener) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(DiffCallback) {

    inner class AlbumViewHolder(
        private var binding: AlbumViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(album: Album) {
            binding.albumName.text = album.name
            binding.artistName.text = album.artists.joinToString { it.name }
            Glide.with(binding.root)
                .load(album.imageUrl)
                .fallback(R.drawable.album_image_fallback)
                .centerCrop()
                .override(400, 400)
                .into(binding.albumImage)
        }

        fun updateView(position: Int) {
            val album = getItem(position)
            bind(album)
            itemView.setOnClickListener {
                onClickListener.onClick(album)
                bind(album)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {
        return AlbumViewHolder(
            AlbumViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.updateView(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(data: MutableList<Album>) {
        submitList(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl
        }
    }

    class OnClickListener(val clickListener: (album: Album) -> Unit) {
        fun onClick(album: Album) = clickListener(album)
    }
}