package com.example.myspotify.ui.postlogin.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myspotify.R
import com.example.myspotify.data.model.Artist
import com.example.myspotify.databinding.FragmentSearchBinding
import com.example.myspotify.ui.postlogin.search.adapter.SearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        initAdapters()
        initRecyclerViews()

        return binding.root
    }

    private fun initAdapters() {

        searchAdapter = SearchAdapter(SearchAdapter.OnClickListener {
//            viewModel.onSearchArtistSelected(it)
            if (it.type == "artist") {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToArtistDetailsFragment(
                        Artist(
                            id = it.id,
                            imageUrl = it.imageUrl,
                            name = it.name,
                            followers = it.followers
                        )
                    )
                )
            }
//            resetSearchView()
        })
    }

    private fun resetSearchView() {
        binding.searchView.setQuery("", false)
        binding.searchView.isIconified = true
        binding.searchView.clearFocus()
    }

    private fun initRecyclerViews() {
        binding.searchRecyclerView.adapter = searchAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.searchedItems.observe(this.viewLifecycleOwner) {
            searchAdapter.updateData(it.toMutableList())
        }

        viewModel.filterArtists.observe(this.viewLifecycleOwner) {
            if (it) {
                binding.artistFilterButton.setBackgroundResource(R.drawable.secondary_button_pressed)
            } else {
                binding.artistFilterButton.setBackgroundResource(R.drawable.secondary_button_selector)
            }
        }

        viewModel.filterAlbums.observe(this.viewLifecycleOwner) {
            if (it) {
                binding.albumFilterButton.setBackgroundResource(R.drawable.secondary_button_pressed)
            } else {
                binding.albumFilterButton.setBackgroundResource(R.drawable.secondary_button_selector)
            }
        }
    }

    private fun initListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return true
            }
        })
        binding.artistFilterButton.setOnClickListener {
            viewModel.toggleArtistFilter(binding.searchView.query)
        }

        binding.albumFilterButton.setOnClickListener {
            viewModel.toggleAlbumFilter(binding.searchView.query)
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}