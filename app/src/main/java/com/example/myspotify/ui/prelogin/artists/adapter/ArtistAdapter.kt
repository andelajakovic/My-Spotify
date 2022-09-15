package com.example.myspotify.ui.prelogin.artists.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myspotify.R
import com.example.myspotify.databinding.ArtistViewBinding
import com.example.myspotify.ui.state.ArtistState

class ArtistAdapter(private val onClickListener: OnClickListener) : ListAdapter<ArtistState, ArtistAdapter.ArtistViewHolder>(DiffCallback) {

    inner class ArtistViewHolder(
        private var binding: ArtistViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(artist: ArtistState) {
            if (artist.isSelected) {
                binding.checkIcon.visibility = View.VISIBLE
            } else {
                binding.checkIcon.visibility = View.GONE
            }
            binding.artistName.text = artist.artist.name
            Glide.with(binding.root)
                .load(artist.artist.imageUrl)
                .fallback(R.drawable.artist_image_fallback)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(binding.artistImage)
        }

        fun updateView(position: Int) {
            val artist = getItem(position)
            bind(artist)
            itemView.setOnClickListener {
                onClickListener.onClick(artist)
                bind(artist)
                notifyItemChanged(position)
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

    fun updateData(data: MutableList<ArtistState>) {
        submitList(data)
        notifyDataSetChanged()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ArtistState>() {
        override fun areItemsTheSame(oldItem: ArtistState, newItem: ArtistState): Boolean {
            return oldItem.artist.id == newItem.artist.id
        }

        override fun areContentsTheSame(oldItem: ArtistState, newItem: ArtistState): Boolean {
            return oldItem.isSelected == newItem.isSelected && oldItem.artist.name == newItem.artist.name && oldItem.artist.imageUrl == newItem.artist.imageUrl
        }
    }

    class OnClickListener(val clickListener: (artist: ArtistState) -> Unit) {
        fun onClick(artist: ArtistState) = clickListener(artist)
    }
}