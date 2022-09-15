package com.example.myspotify.ui.postlogin.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myspotify.R
import com.example.myspotify.databinding.ArtistViewBinding
import com.example.myspotify.data.model.Artist


class RecommendedArtistAdapter(private val onClickListener: OnClickListener) : ListAdapter<Artist, RecommendedArtistAdapter.ArtistViewHolder>(DiffCallback) {

    inner class ArtistViewHolder(
        private var binding: ArtistViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(artist: Artist) {
            binding.artistName.text = artist.name
            Glide.with(binding.root)
                .load(artist.imageUrl)
                .centerCrop()
                .fallback(R.drawable.artist_image_fallback)
                .apply(RequestOptions.circleCropTransform())
                .override(400, 400)
                .into(binding.artistImage)
        }

        fun updateView(position: Int) {
            val artist = getItem(position)
            bind(artist)
            itemView.setOnClickListener {
                onClickListener.onClick(artist)
                bind(artist)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArtistViewHolder {
        return ArtistViewHolder(
            ArtistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.updateView(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(data: MutableList<Artist>) {
        submitList(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl
        }
    }

    class OnClickListener(val clickListener: (artist: Artist) -> Unit) {
        fun onClick(artist: Artist) = clickListener(artist)
    }
}