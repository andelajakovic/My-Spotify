package com.example.myspotify.ui.postlogin.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myspotify.R
import com.example.myspotify.data.model.SearchItem
import com.example.myspotify.databinding.SearchItemViewBinding

class SearchAdapter(private val onClickListener: OnClickListener) : ListAdapter<SearchItem, SearchAdapter.SearchViewHolder>(DiffCallback) {

    inner class SearchViewHolder(
        private var binding: SearchItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(item: SearchItem) {
            binding.itemName.text = item.name

            if (item.type == "artist") {
                Glide.with(binding.root)
                    .load(item.imageUrl)
                    .centerCrop()
                    .fallback(R.drawable.artist_image_fallback)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.itemImage)
            } else {
                Glide.with(binding.root)
                    .load(item.imageUrl)
                    .fallback(R.drawable.album_image_fallback)
                    .centerCrop()
//                    .override(400, 400)
                    .into(binding.itemImage)
            }


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

    fun updateData(data: MutableList<SearchItem>) {
        submitList(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SearchItem>() {
        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl && oldItem.type == newItem.type
        }
    }

    class OnClickListener(val clickListener: (artist: SearchItem) -> Unit) {
        fun onClick(artist: SearchItem) = clickListener(artist)
    }
}