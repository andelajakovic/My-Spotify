package com.example.myspotify.ui.prelogin.artists

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.example.myspotify.R
import com.example.myspotify.databinding.FragmentChooseArtistsBinding
import com.example.myspotify.ui.prelogin.artists.adapter.ArtistAdapter
import com.example.myspotify.ui.prelogin.artists.adapter.SearchAdapter
import com.example.myspotify.ui.state.LoadingState
import com.example.myspotify.ui.state.NavigationState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseArtistsFragment : Fragment() {

    private val viewModel: ChooseArtistsViewModel by viewModels()

    private var _binding: FragmentChooseArtistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseArtistsBinding.inflate(inflater, container, false)

        initAdapters()
        initRecyclerViews()

        return binding.root
    }

    private fun initAdapters() {
        artistAdapter = ArtistAdapter(ArtistAdapter.OnClickListener {
            viewModel.onArtistSelected(it)
        })

        searchAdapter = SearchAdapter(SearchAdapter.OnClickListener {
            viewModel.onSearchArtistSelected(it)
            resetSearchView()
        })
    }

    private fun resetSearchView() {
        binding.searchView.setQuery("", false)
        binding.searchView.isIconified = true
        binding.searchView.clearFocus()
    }

    private fun initRecyclerViews() {
        binding.artistsRecyclerView.adapter = artistAdapter
        binding.searchRecyclerView.adapter = searchAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }


    private fun initListeners() {
        binding.nextButton.setOnClickListener {
            viewModel.synchronizeSelectedArtists()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchArtists(newText)
                return true
            }
        })

        activity?.onBackPressedDispatcher?.addCallback {
            viewModel.onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.artists.observe(this.viewLifecycleOwner) {
            artistAdapter.updateData(it.data)
            binding.artistsRecyclerView.visibility = if(it.isVisible) View.VISIBLE else View.GONE
        }

        viewModel.searchedArtists.observe(this.viewLifecycleOwner) {
            searchAdapter.updateData(it.data)
            binding.searchRecyclerView.visibility = if (it.isVisible) View.VISIBLE else View.GONE
        }

        viewModel.navigationState.observe(this.viewLifecycleOwner) {
            when (it) {
                NavigationState.BACK -> {
                    activity?.finish()
                }
                NavigationState.NAVIGATE_TO_HOME_ACTIVITY -> {
//                    val action = TODO
//                    findNavController().navigate(action)
//                    activity?.finish()
                }
                else -> {
                    // no-op
                }
            }
        }

        viewModel.continueButtonState.observe(this.viewLifecycleOwner) {
            if (it.isEnabled) {
                binding.nextButton.setBackgroundResource(R.drawable.white_button_selector)
                binding.nextButton.isClickable = true
            } else {
                binding.nextButton.setBackgroundResource(R.drawable.white_button_disabled)
                binding.nextButton.isClickable = false
            }
        }

        viewModel.relatedArtistsLoadingState.observe(this.viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                LoadingState.DONE -> {
//                    artistAdapter.updateData(viewModel.artists)
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

        viewModel.searchArtistsLoadingState.observe(this.viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                LoadingState.DONE, LoadingState.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
                else -> {
                    // no-op
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}