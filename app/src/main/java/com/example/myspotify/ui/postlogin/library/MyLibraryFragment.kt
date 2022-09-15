package com.example.myspotify.ui.postlogin.library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myspotify.R
import com.example.myspotify.databinding.FragmentMyLibraryBinding
import com.example.myspotify.ui.postlogin.HomeActivity
import com.example.myspotify.ui.state.LoadingState
import com.example.myspotify.ui.postlogin.library.adapter.FavoriteArtistAdapter
import com.example.myspotify.ui.postlogin.library.adapter.LikedAlbumAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : Fragment() {

    private val viewModel: MyLibraryViewModel by viewModels()

    private var _binding: FragmentMyLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteArtistAdapter: FavoriteArtistAdapter
    private lateinit var likedAlbumAdapter: LikedAlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyLibraryBinding.inflate(inflater, container, false)

        initAdapters()
        initRecyclerViews()

        return binding.root
    }

    private fun initAdapters() {
        favoriteArtistAdapter = FavoriteArtistAdapter(
            FavoriteArtistAdapter.OnClickListener {
                findNavController().navigate(MyLibraryFragmentDirections.actionMyLibraryFragmentToArtistDetailsFragment(it))
            },
            FavoriteArtistAdapter.OnClickListener {
                viewModel.followArtist(it)
            }
        )

        likedAlbumAdapter = LikedAlbumAdapter(LikedAlbumAdapter.OnClickListener {
            findNavController().navigate(MyLibraryFragmentDirections.actionMyLibraryFragmentToAlbumDetailsFragment(it))
//            viewModel.getAlbumDetails(album)
        })

    }

    private fun initRecyclerViews() {
        binding.artists.adapter = favoriteArtistAdapter
        binding.albums.adapter = likedAlbumAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        viewModel.initLists()
    }

    private fun initListeners() {
        binding.discoverMusicLabel.setOnClickListener {
            (activity as HomeActivity).binding.activityHomeBottomNavigationView.selectedItemId = R.id.homeFragment
        }
        binding.discoverArtistsLabel.setOnClickListener {
            (activity as HomeActivity).binding.activityHomeBottomNavigationView.selectedItemId = R.id.homeFragment
        }
    }

    private fun initObservers() {
        viewModel.favoriteArtists.observe(this.viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.artists.visibility = View.GONE
                binding.discoverArtistsLabel.visibility = View.VISIBLE
            } else {
                binding.artists.visibility = View.VISIBLE
                binding.discoverArtistsLabel.visibility = View.GONE
            }
            favoriteArtistAdapter.updateData(it)
        }
        viewModel.likedAlbums.observe(this.viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.albums.visibility = View.GONE
                binding.discoverMusicLabel.visibility = View.VISIBLE
            } else {
                binding.albums.visibility = View.VISIBLE
                binding.discoverMusicLabel.visibility = View.GONE
            }
            likedAlbumAdapter.updateData(it)
        }

        viewModel.loadingState.observe(this.viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    binding.layout.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                LoadingState.DONE -> {
                    binding.layout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                LoadingState.ERROR -> {
                    Toast.makeText(context, R.string.unexpected_error_has_occurred_error_message, Toast.LENGTH_SHORT).show()
                    binding.layout.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
                else -> {
                    // no-op
                }
            }
        }

    }

}