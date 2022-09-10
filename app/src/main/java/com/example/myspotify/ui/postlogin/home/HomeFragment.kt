package com.example.myspotify.ui.postlogin.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.myspotify.databinding.FragmentHomeBinding
import com.example.myspotify.R
import com.example.myspotify.ui.state.LoadingState
import com.example.myspotify.ui.postlogin.home.adapter.AlbumAdapter
import com.example.myspotify.ui.postlogin.home.adapter.RecommendedArtistAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recommendedArtistAdapter: RecommendedArtistAdapter
    private lateinit var newReleasesAdapter: AlbumAdapter
    private lateinit var albumsFromLikedArtistsAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initAdapters()
        initRecyclerViews()

        return binding.root
    }

    private fun initAdapters() {
        recommendedArtistAdapter = RecommendedArtistAdapter(RecommendedArtistAdapter.OnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArtistFragment(it))
        })

        newReleasesAdapter = AlbumAdapter(AlbumAdapter.OnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAlbumFragment(it))
//            viewModel.getAlbumDetails(album)
        })

        albumsFromLikedArtistsAdapter = AlbumAdapter(AlbumAdapter.OnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAlbumFragment(it))
//            viewModel.getAlbumDetails(album)
        })

    }

    private fun initRecyclerViews() {
        binding.recommendedArtistsRecyclerView.adapter = recommendedArtistAdapter
        binding.newReleasesRecyclerView.adapter = newReleasesAdapter
        binding.albumsRecyclerView.adapter = albumsFromLikedArtistsAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.recommendedArtists.observe(this.viewLifecycleOwner) {
            recommendedArtistAdapter.updateData(it)
        }
        viewModel.newReleases.observe(this.viewLifecycleOwner) {
            newReleasesAdapter.updateData(it)
        }
        viewModel.albumsFromLikedArtists.observe(this.viewLifecycleOwner) {
            albumsFromLikedArtistsAdapter.updateData(it)
        }

        viewModel.loadingState.observe(this.viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    binding.homeMainLayout.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                LoadingState.DONE -> {
                    binding.homeMainLayout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                LoadingState.ERROR -> {
                    Toast.makeText(context, R.string.unexpected_error_has_occurred_error_message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
                else -> {
                    // no-op
                }
            }
        }

//        viewModel.albumLoadingState.observe(this.viewLifecycleOwner) {
//            when (it) {
//                LoadingState.LOADING -> {
//                    // no-op
//                }
//                LoadingState.DONE -> {
//                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAlbumFragment(viewModel.albumWithDetails))
//                    viewModel.clearAlbumLoadingState()
//                }
//                LoadingState.ERROR -> {
//                    toastMessageFactory.createShortToastMessage(getString(R.string.unexpected_error_has_occurred_error_message)).show()
//                }
//                else -> {
//                    // no-op
//                }
//            }
//        }
    }

}