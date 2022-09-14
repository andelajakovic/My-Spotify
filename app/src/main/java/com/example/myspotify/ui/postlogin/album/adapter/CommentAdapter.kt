package com.example.myspotify.ui.postlogin.album.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.example.myspotify.data.model.Comment
import com.example.myspotify.databinding.CommentViewBinding

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {

    lateinit var context: Context

    inner class CommentViewHolder(
        private var binding: CommentViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun bind(comment: Comment) {
            binding.name.text = comment.name
            binding.text.text = comment.text

            Glide.with(binding.root)
                .load( AvatarGenerator.AvatarBuilder(context)
                           .setAvatarSize(100)
                           .setLabel(comment.name)
                           .setTextSize(24)
                           .toCircle()
                           .build())
                .into(binding.itemImage)

        }

        fun updateView(position: Int) {
            val comment = getItem(position)
            bind(comment)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        context = parent.context
        return CommentViewHolder(
            CommentViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.updateView(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(data: MutableList<Comment>) {
        submitList(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.userId == newItem.userId && oldItem.text == newItem.text
        }
    }
}