package com.example.myspotify.ui.postlogin.album

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myspotify.R
import com.example.myspotify.data.model.Album
import com.example.myspotify.databinding.FragmentAlbumDetailsBinding
import com.example.myspotify.ui.postlogin.album.adapter.ArtistAdapter
import com.example.myspotify.ui.postlogin.album.adapter.CommentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AlbumDetailsFragment : Fragment() {

    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var artistAdapter: ArtistAdapter

    private val args: AlbumDetailsFragmentArgs by navArgs()

    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initObservers()
        viewModel.initAlbum(args.album)

        _binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun initObservers() {
        viewModel.album.observe(this.viewLifecycleOwner) {
            initViews(it)
        }
    }

    private fun initViews(album: Album) {
        artistAdapter = ArtistAdapter()
        binding.artists.adapter = artistAdapter

        artistAdapter.updateData(album.artists.toMutableList())

        Glide.with(binding.root)
            .load(album.imageUrl)
            .centerCrop()
            .override(650, 650)
            .into(binding.albumImage)

        binding.albumName.text = album.name
        (album.albumType.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + " • " + album.releaseDate.substring(0, 4)).also { binding.albumType.text = it }

        binding.releaseDate.text = album.releaseDate
        binding.tracks.text = album.tracks.joinToString (" • ") { it.name }

        if (album.isUserLiking) {
            binding.likeButton.setImageResource(R.drawable.heart_icon_filled)
        } else {
            binding.likeButton.setImageResource(R.drawable.heart_icon)
        }

        binding.likeButton.setOnClickListener {
            viewModel.likeAlbum(album)
        }

        binding.commentButton.setOnClickListener {
            showBottomSheetDialog(album)
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, album.externalUrl);
            startActivity(Intent.createChooser(shareIntent,"Pošalji"))
        }
    }

    private fun showBottomSheetDialog(album: Album) {

        viewModel.getComments(album)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.comments_bottom_sheet)

        val comment = bottomSheetDialog.findViewById<EditText>(R.id.editText)
        val sendButton = bottomSheetDialog.findViewById<ImageView>(R.id.send_button)
        val noCommentsYet = bottomSheetDialog.findViewById<TextView>(R.id.no_comments_yet_label)

        val commentAdapter: CommentAdapter = CommentAdapter()
        val comments = bottomSheetDialog.findViewById<RecyclerView>(R.id.recyclerView)
        comments?.adapter = commentAdapter

        viewModel.comments.observe(this.viewLifecycleOwner) {
            if (it.isEmpty()) {
                noCommentsYet?.visibility = View.VISIBLE
            } else {
                noCommentsYet?.visibility = View.GONE
            }
            comment?.setText(R.string.EMPTY)
            commentAdapter.updateData(it.toMutableList())
        }

        sendButton?.setOnClickListener {
            if (comment?.text?.isNotEmpty() == true) {
                viewModel.sendComment(comment.text.toString(), album)
            }
        }

        bottomSheetDialog.show()
    }


}