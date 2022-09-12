package com.example.myspotify.ui.postlogin.artist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myspotify.R
import com.example.myspotify.data.model.Artist
import com.example.myspotify.databinding.FragmentArtistDetailsBinding
import com.example.myspotify.ui.postlogin.artist.adapter.PopularReleaseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistDetailsFragment : Fragment() {

    private var _binding: FragmentArtistDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: ArtistDetailsFragmentArgs by navArgs()

    private val viewModel: ArtistDetailsViewModel by viewModels()

    private lateinit var popularReleaseAdapter: PopularReleaseAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initObservers()
        viewModel.initArtist(args.artist)

        _binding = FragmentArtistDetailsBinding.inflate(inflater, container, false)

        initAdapters()
        initRecyclerViews()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initAdapters() {
        popularReleaseAdapter = PopularReleaseAdapter(PopularReleaseAdapter.OnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArtistDetailsFragment(it))
        })
    }

    private fun initRecyclerViews() {
        binding.albums.adapter = popularReleaseAdapter
    }

    private fun initObservers() {
        viewModel.artist.observe(this.viewLifecycleOwner) {
            initViews(it)
        }

        viewModel.popularReleases.observe(this.viewLifecycleOwner) {
            popularReleaseAdapter.updateData(it.toMutableList())
        }
    }

    private fun initViews(artist: Artist) {

        binding.artistName.text = artist.name
        binding.followers.text = resources.getString(R.string.number_of_followers, artist.followers.toString())

        Glide.with(binding.root)
            .load(artist.imageUrl)
            .centerCrop()
            .into(binding.artistImage)
        binding.tracks.text = artist.tracks.joinToString("  •  ") { it.name }

        if (artist.isUserFollowing) {
            binding.followButton.setBackgroundResource(R.drawable.follow_button_pressed)
            binding.followButton.text = resources.getString(R.string.fragment_artist_details_unfollow_button_label)
        } else {
            binding.followButton.setBackgroundResource(R.drawable.follow_button_selector)
            binding.followButton.text = resources.getString(R.string.fragment_artist_details_follow_button_label)
        }

        binding.followButton.setOnClickListener {
            viewModel.followArtist(artist)
        }

        binding.seeDiscographyButton.setOnClickListener {
            // TODO
        }

    }

}