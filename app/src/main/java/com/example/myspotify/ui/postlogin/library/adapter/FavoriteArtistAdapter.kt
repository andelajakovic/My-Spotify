package com.example.myspotify.ui.postlogin.library.adapter

import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myspotify.R
import com.example.myspotify.data.model.Artist
import com.example.myspotify.databinding.FavoriteArtistViewBinding


class FavoriteArtistAdapter(private val onClickListener: OnClickListener, private val onFollowButtonClickListener: OnClickListener) : ListAdapter<Artist, FavoriteArtistAdapter.FavoriteArtistViewHolder>(DiffCallback) {

    inner class FavoriteArtistViewHolder(
        private var binding: FavoriteArtistViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(artist: Artist) {
            binding.name.text = artist.name

            if (artist.isUserFollowing) {
                binding.followButton.setBackgroundResource(R.drawable.follow_button_pressed)
                binding.followButton.setText(R.string.fragment_artist_details_unfollow_button_label)
            } else {
                binding.followButton.setBackgroundResource(R.drawable.follow_button_selector)
                binding.followButton.setText(R.string.fragment_artist_details_follow_button_label)
            }

            Glide.with(binding.root)
                .load(artist.imageUrl)
                .centerCrop()
                .fallback(R.drawable.artist_image_fallback)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.itemImage)

            binding.followButton.setOnClickListener {
                onFollowButtonClickListener.onClick(artist)
            }
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
    ): FavoriteArtistViewHolder {
        return FavoriteArtistViewHolder(
            FavoriteArtistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoriteArtistViewHolder, position: Int) {
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
        notifyDataSetChanged()
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