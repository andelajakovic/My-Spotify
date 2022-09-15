package com.example.myspotify.ui.prelogin.artists.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myspotify.R
import com.example.myspotify.databinding.SearchItemViewBinding
import com.example.myspotify.ui.state.ArtistState

class SearchAdapter(private val onClickListener: OnClickListener) : ListAdapter<ArtistState, SearchAdapter.SearchViewHolder>(DiffCallback) {

    inner class SearchViewHolder(
        private var binding: SearchItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(artist: ArtistState) {
            binding.itemName.text = artist.artist.name
            Glide.with(binding.root)
                .load(artist.artist.imageUrl)
                .centerCrop()
                .fallback(R.drawable.artist_image_fallback)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.itemImage)
        }

        fun updateView(position: Int) {
            val artist = getItem(position)
            bind(artist)
            itemView.setOnClickListener {
                onClickListener.onClick(artist)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            SearchItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.updateView(position)
    }

    fun updateData(data: MutableList<ArtistState>) {
        submitList(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ArtistState>() {
        override fun areItemsTheSame(oldItem: ArtistState, newItem: ArtistState): Boolean {
            return oldItem.artist.id == newItem.artist.id
        }

        override fun areContentsTheSame(oldItem: ArtistState, newItem: ArtistState): Boolean {
            return oldItem.artist.name == newItem.artist.name && oldItem.artist.imageUrl == newItem.artist.imageUrl
        }
    }

    class OnClickListener(val clickListener: (artist: ArtistState) -> Unit) {
        fun onClick(artist: ArtistState) = clickListener(artist)
    }
}